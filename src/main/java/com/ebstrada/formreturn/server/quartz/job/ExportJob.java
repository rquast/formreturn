package com.ebstrada.formreturn.server.quartz.job;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.HashMap;

import javax.persistence.EntityManager;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

import au.com.bytecode.opencsv.CSVWriter;

import com.ebstrada.formreturn.api.messaging.MessageNotification;
import com.ebstrada.formreturn.manager.logic.export.ExportOptions;
import com.ebstrada.formreturn.manager.logic.export.csv.CSVExporter;
import com.ebstrada.formreturn.manager.logic.export.filter.Filter;
import com.ebstrada.formreturn.manager.logic.export.image.ImageExporter;
import com.ebstrada.formreturn.manager.logic.export.xml.XMLExportPreferences;
import com.ebstrada.formreturn.manager.logic.export.xml.XMLExporter;
import com.ebstrada.formreturn.manager.persistence.jpa.PublicationXSL;
import com.ebstrada.formreturn.server.Main;
import com.ebstrada.formreturn.manager.ui.editor.persistence.XSLTemplate;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.preferences.persistence.CSVExportPreferences;
import com.ebstrada.formreturn.server.ServerGUI;
import com.ebstrada.formreturn.server.preferences.persistence.ExportJobPreferences;
import com.ebstrada.formreturn.server.preferences.persistence.TaskSchedulerJobPreferences;

public class ExportJob extends TaskSchedulerJob implements StatefulJob {

    private static final Logger logger = Logger.getLogger(ExportJob.class);

    // DO NOT REMOVE THE DEFAULT CONSTRUCTOR - IT IS REQUIRED FOR QUARTZ!
    public ExportJob() {
        super();
    }

    public ExportJob(TaskSchedulerJobPreferences jobPreferences) {
        super(jobPreferences);
    }

    public void execute(JobExecutionContext context) throws JobExecutionException {
        if (ServerGUI.getInstance() != null) {
            executeGUI(context);
        } else {
            executeDaemon(context);
        }
    }

    private void executeDaemon(JobExecutionContext context) {
        try {
            export(getExportOptions(context));
        } catch (Exception ex) {
            Misc.printStackTrace(ex);
            try {
                stop(context, ex);
            } catch (JobExecutionException e) {
                Misc.printStackTrace(e);
            }
        }
    }

    public char getQuoteCharacter(int quoteType) {
        switch (quoteType) {
            case CSVExportPreferences.NO_QUOTES:
                return CSVWriter.NO_QUOTE_CHARACTER;
            case CSVExportPreferences.SINGLE_QUOTES:
                return "'".charAt(0);
            case CSVExportPreferences.DOUBLE_QUOTES:
            default:
                return "\"".charAt(0);
        }
    }

    public char getDelimiterCharacter(int delimiterType) {
        switch (delimiterType) {
            case CSVExportPreferences.TSV_DELIMITER:
                return "\t".charAt(0);
            case CSVExportPreferences.CSV_DELIMITER:
            default:
                return ",".charAt(0);
        }
    }

    private void export(ExportOptions exportOptions) throws Exception {

        ArrayList<Filter> filters = exportOptions.getFilters();

        QuartzMessageNotification quartzMessageNotification = new QuartzMessageNotification();

        XMLExportPreferences xmlExportPreferences;
        XSLTemplate xslTemplate;
        PublicationXSL pxsl;
        byte[] xslData;
        switch (exportOptions.getExportType()) {

            case ExportOptions.EXPORT_CSV_WITH_STATS:
            case ExportOptions.EXPORT_CSV:

                EntityManager entityManager =
                    Main.getInstance().getJPAConfiguration().getEntityManager();

                if (entityManager == null) {
                    return;
                }

                CSVWriter writer = null;
                CSVWriter statsWriter = null;

                try {
                    CSVExportPreferences csvep = exportOptions.getCsvExportPreferences();
                    String fileStr = exportOptions.getCsvFile();
                    if (csvep.isTimestampFilenamePrefix()) {
                        fileStr = Misc.getTimestampPrefixedFilename(fileStr);
                    }
                    writer = new CSVWriter(
                        new OutputStreamWriter(new FileOutputStream(fileStr), "UTF-8"),
                        getDelimiterCharacter(csvep.getDelimiterType()),
                        getQuoteCharacter(csvep.getQuotesType()));
                    CSVExporter csve = new CSVExporter(csvep, exportOptions.getFilters(),
                        exportOptions.getPublicationIds(), entityManager);
                    csve.write(writer, quartzMessageNotification);
                    if (csvep.isIncludeStatistics()) {
                        statsWriter = new CSVWriter(new OutputStreamWriter(
                            new FileOutputStream(exportOptions.getCsvStatsFile()), "UTF-8"),
                            getDelimiterCharacter(csvep.getDelimiterType()),
                            getQuoteCharacter(csvep.getQuotesType()));
                        csve.writeStats(statsWriter, quartzMessageNotification);
                    }
                } catch (org.apache.openjpa.persistence.PersistenceException pe) {
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(pe);
                    throw (pe);
                } catch (Exception ex) {
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
                    throw (ex);
                } finally {
                    if (writer != null) {
                        try {
                            writer.close();
                        } catch (IOException e) {
                            throw (e);
                        }
                    }
                    if (statsWriter != null) {
                        try {
                            statsWriter.close();
                        } catch (IOException e) {
                            throw (e);
                        }
                    }
                    if (entityManager != null) {
                        entityManager.close();
                    }
                }

                break;

            case ExportOptions.IMAGE_EXPORT:

                ImageExporter ie = new ImageExporter(exportOptions.getPublicationIds(),
                    exportOptions.getImageExportPreferences());
                ie.setMessageNotification(quartzMessageNotification);
                ie.export();

                break;

            case ExportOptions.EXPORT_XML:

                String xmlFileStr = exportOptions.getXmlFile();
                if (exportOptions.getXmlExportPreferences().isTimestampFilenamePrefix()) {
                    xmlFileStr = Misc.getTimestampPrefixedFilename(xmlFileStr);
                }
                exportXML(xmlFileStr, exportOptions.getPublicationIds(), filters,
                    quartzMessageNotification);

                break;

            case ExportOptions.EXPORT_XSLFO_FROM_FILE:

                xmlExportPreferences = exportOptions.getXmlExportPreferences();
                String pdfFileStr = exportOptions.getPdfFile();
                if (xmlExportPreferences.isTimestampFilenamePrefix()) {
                    pdfFileStr = Misc.getTimestampPrefixedFilename(pdfFileStr);
                }
                xslData = Misc.getBytesFromFile(new File(exportOptions.getXslFile()));
                createXSLReport(exportOptions.getPublicationIds(), xslData, pdfFileStr,
                    quartzMessageNotification, filters);

                break;

            case ExportOptions.EXPORT_XSLFO_FROM_DATABASE:

                xmlExportPreferences = exportOptions.getXmlExportPreferences();
                pdfFileStr = exportOptions.getPdfFile();
                if (xmlExportPreferences.isTimestampFilenamePrefix()) {
                    pdfFileStr = Misc.getTimestampPrefixedFilename(pdfFileStr);
                }
                xslTemplate = xmlExportPreferences.getSelectedXSLTemplate();
                entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();
                pxsl = entityManager.find(PublicationXSL.class, xslTemplate.getPublicationXSLId());
                xslData = pxsl.getXslData();
                createXSLReport(exportOptions.getPublicationIds(), xslData, pdfFileStr,
                    quartzMessageNotification, filters);

                break;

        }

    }

    public void exportXML(String outputXMLFile, ArrayList<Long> publicationIds,
        ArrayList<Filter> filters, MessageNotification messageNotification) throws Exception {
        EntityManager entityManager = null;
        OutputStreamWriter osw = null;
        FileOutputStream fos = null;
        BufferedWriter bufferedWriter = null;
        try {
            entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();
            fos = new FileOutputStream(outputXMLFile);
            osw = new OutputStreamWriter(fos, "UTF-8");
            bufferedWriter = new BufferedWriter(osw);
            XMLExporter xmle =
                new XMLExporter(XMLExporter.PUBLICATION_EXPORT, publicationIds, entityManager,
                    filters);
            xmle.write(bufferedWriter, messageNotification);
        } catch (IOException e) {
            Misc.printStackTrace(e);
            throw (e);
        } catch (ParserConfigurationException e) {
            Misc.printStackTrace(e);
            throw (e);
        } catch (TransformerException e) {
            Misc.printStackTrace(e);
            throw (e);
        } catch (InterruptedException e) {
            Misc.printStackTrace(e);
            throw (e);
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                }
            }
            if (osw != null) {
                try {
                    osw.close();
                } catch (IOException e) {
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public void createXSLReport(ArrayList<Long> publicationIds, byte[] xslData, String pdffile,
        MessageNotification messageNotification, ArrayList<Filter> filters) throws Exception {

        EntityManager entityManager = null;
        OutputStream out = null;
        FileOutputStream fos = null;
        ByteArrayInputStream bais = null;

        try {
            entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();
            XMLExporter xmle =
                new XMLExporter(XMLExporter.PUBLICATION_EXPORT, publicationIds, entityManager,
                    filters);
            DOMSource domSource = xmle.getDomSource(messageNotification);

            FopFactory fopFactory = FopFactory.newInstance();
            Configuration cfg = Misc.getFOPConfiguration();
            fopFactory.setUserConfig(cfg);
            fopFactory.setStrictValidation(false);

            FOUserAgent foUserAgent = fopFactory.newFOUserAgent();

            fos = new java.io.FileOutputStream(pdffile);
            out = new java.io.BufferedOutputStream(fos);
            bais = new ByteArrayInputStream(xslData);

            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);

            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(bais));
            transformer.setParameter("versionParam", "2.0");
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.ENCODING, "UTF-8");
            Result res = new SAXResult(fop.getDefaultHandler());
            transformer.transform(domSource, res);
        } catch (Exception ex) {
            Misc.printStackTrace(ex);
            throw ex;
        } finally {
            if (bais != null) {
                try {
                    bais.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }

    }

    public void createJob(Class<?> clazz) {
        super.createJob(clazz);
        job.getJobDataMap()
            .put("exportOptions", ((ExportJobPreferences) preferences).getExportOptions());
    }

    private ExportOptions getExportOptions(JobExecutionContext context) {
        JobDataMap jdm = context.getJobDetail().getJobDataMap();
        return (ExportOptions) jdm.get("exportOptions");
    }

    private void executeGUI(JobExecutionContext context) {
        try {
            export(getExportOptions(context));
        } catch (Exception ex) {
            Misc.printStackTrace(ex);
            try {
                stop(context, ex);
            } catch (JobExecutionException e) {
                Misc.printStackTrace(e);
                if (ServerGUI.getInstance() != null) {
                    Misc.showErrorMsg(ServerGUI.getInstance().getServerFrame(),
                        e.getLocalizedMessage());
                }
            }
        }
    }

    protected boolean startTransaction(EntityManager entityManager) {
        if (entityManager == null) {
            return false;
        } else {
            boolean hasTransaction = false;
            while (hasTransaction == false) {
                try {
                    if (entityManager.getTransaction().isActive()) {
                        entityManager.getTransaction().rollback();
                    }
                    entityManager.getTransaction().begin();
                    entityManager.flush();
                    hasTransaction = true;
                } catch (Exception ex) {
                    logger.warn(ex.getLocalizedMessage(), ex);
                }
            }
        }
        return true;
    }

    protected void closeEntityManager(EntityManager entityManager) {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
    }

    protected void stop(JobExecutionContext jobExecutionContext, Exception ex)
        throws JobExecutionException {
        JobExecutionException jee = new JobExecutionException(ex);
        jee.setErrorCode(JobExecutionException.ERR_UNSPECIFIED);
        jee.setUnscheduleAllTriggers(true);
        throw jee;
    }

    // check if the file is still being updated, do not process if it is
    protected boolean isFileComplete(File imageFile, HashMap<String, Long> recentFileTimes) {

        FileChannel channel;
        try {
            channel = new RandomAccessFile(imageFile, "rw").getChannel();
        } catch (FileNotFoundException e) {
            logger.warn(e.getLocalizedMessage(), e);
            return false;
        }

        FileLock lock = null;
        boolean isComplete = false;

        try {
            // Get an exclusive lock on the whole file
            lock = channel.lock();
            isComplete = true;
        } catch (IOException e) {
            isComplete = false;
        } finally {
            if (lock != null) {
                try {
                    lock.release();
                } catch (IOException e) {
                    logger.warn(e.getLocalizedMessage(), e);
                }
            }
            try {
                channel.close();
            } catch (IOException e) {
                logger.warn(e.getLocalizedMessage(), e);
            }
        }

        if (!isComplete) {
            return false;
        }

        if (imageFile.length() <= 0) {
            return false;
        }

        String imageFileName = null;
        try {
            imageFileName = imageFile.getCanonicalPath();
        } catch (IOException e) {
            return false;
        }

        if (recentFileTimes.containsKey(imageFileName)) {

            long oldLastModified = recentFileTimes.get(imageFileName);
            long currentLastModified = imageFile.lastModified();

            if (oldLastModified != currentLastModified) {
                recentFileTimes.put(imageFileName, currentLastModified);
                isComplete = false;
            } else {
                recentFileTimes.remove(imageFileName);
                isComplete = true;
            }

        } else {

            // add this file and its timestamp to the list
            recentFileTimes.put(imageFileName, imageFile.lastModified());
            isComplete = false;

        }

        return isComplete;

    }

}
