package com.ebstrada.formreturn.manager.logic.export.xml;

import java.io.StringWriter;
import java.io.Writer;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.StringEscapeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.ebstrada.formreturn.api.messaging.MessageNotification;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.export.filter.ExcludeEmptyRecordsFilter;
import com.ebstrada.formreturn.manager.logic.export.filter.Filter;
import com.ebstrada.formreturn.manager.logic.export.stats.StatisticMap;
import com.ebstrada.formreturn.manager.logic.export.stats.Statistic;
import com.ebstrada.formreturn.manager.persistence.jpa.CheckBox;
import com.ebstrada.formreturn.manager.persistence.jpa.Form;
import com.ebstrada.formreturn.manager.persistence.jpa.FormPage;
import com.ebstrada.formreturn.manager.persistence.jpa.FragmentBarcode;
import com.ebstrada.formreturn.manager.persistence.jpa.FragmentOmr;
import com.ebstrada.formreturn.manager.persistence.jpa.Grading;
import com.ebstrada.formreturn.manager.persistence.jpa.Publication;
import com.ebstrada.formreturn.manager.persistence.jpa.Record;
import com.ebstrada.formreturn.manager.persistence.jpa.Segment;
import com.ebstrada.formreturn.manager.persistence.jpa.SourceField;
import com.ebstrada.formreturn.manager.persistence.jpa.SourceText;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.preferences.persistence.PublicationPreferences;

public class XMLExporter {

    public static final int PUBLICATION_EXPORT = 0;
    public static final int FORM_EXPORT = 1;

    private int exportType = PUBLICATION_EXPORT;

    private ArrayList<Long> publicationIds = new ArrayList<Long>();

    private ArrayList<Long> formIds = new ArrayList<Long>();

    private EntityManager entityManager;

    private StatisticMap statAggregator = new StatisticMap();

    private ArrayList<Filter> filters;

    public XMLExporter(int exportType, ArrayList<Long> ids, EntityManager entityManager,
        ArrayList<Filter> filters) {
        this.exportType = exportType;
        this.entityManager = entityManager;
        this.filters = filters;
        switch (this.exportType) {
            case PUBLICATION_EXPORT:
                this.publicationIds = ids;
                break;
            case FORM_EXPORT:
                this.formIds = ids;
                break;
        }
    }

    public DOMSource getDomSource(MessageNotification messageNotification)
        throws ParserConfigurationException, InterruptedException {

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        Element rootElement = document.createElement("capturedDataExport");
        rootElement.setAttribute("version", com.ebstrada.formreturn.manager.ui.Main.VERSION);
        document.appendChild(rootElement);

        switch (this.exportType) {
            case PUBLICATION_EXPORT:
                createPublicationXML(rootElement, document, messageNotification);
                break;
            case FORM_EXPORT:
                createFormXML(rootElement, document, messageNotification);
                break;
        }

        createStatisticsXML(rootElement, document);

        return new DOMSource(document);

    }

    public void write(Writer writer, MessageNotification messageNotification)
        throws ParserConfigurationException, TransformerException, InterruptedException {

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        Element rootElement = document.createElement("capturedDataExport");
        rootElement.setAttribute("version", com.ebstrada.formreturn.manager.ui.Main.VERSION);
        document.appendChild(rootElement);

        switch (this.exportType) {
            case PUBLICATION_EXPORT:
                createPublicationXML(rootElement, document, messageNotification);
                break;
            case FORM_EXPORT:
                createFormXML(rootElement, document, messageNotification);
                break;
        }

        createStatisticsXML(rootElement, document);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StreamResult streamResult = new StreamResult(writer);
        DOMSource domSource = new DOMSource(document);
        transformer.transform(domSource, streamResult);

    }

    private void createStatisticsXML(Element parentElement, Document document) {
        if (this.statAggregator == null) {
            return;
        }

        Element statisticsElement = document.createElement("statistics");

        ArrayList<String> fieldNames = this.statAggregator.getFieldNames();
        ArrayList<Statistic> statistics = this.statAggregator.getStatistics();

        for (String fieldName : fieldNames) {

            Statistic stat = statistics.get(fieldNames.indexOf(fieldName));

            ArrayList<String> answers = stat.getAnswers();
            ArrayList<Integer> frequencies = stat.getFrequencies();

            double correct = stat.getPercentageCorrect();
            double incorrect = stat.getPercentageIncorrect();
            int totalResponses = (int) stat.getTotalReponses();

            Element fieldElement = document.createElement("field");

            fieldElement.setAttribute("name", fieldName);
            fieldElement.setAttribute("correct", correct + "%");
            fieldElement.setAttribute("incorrect", incorrect + "%");
            fieldElement.setAttribute("responses", totalResponses + "");


            for (String answer : answers) {

                int frequency = frequencies.get(answers.indexOf(answer));

                Element questionElement = document.createElement("question");
                Element answerElement = document.createElement("answer");
                Element frequencyElement = document.createElement("frequency");

                Text answerText = document.createTextNode(answer);
                Text frequencyText = document.createTextNode(frequency + "");

                answerElement.appendChild(answerText);
                frequencyElement.appendChild(frequencyText);

                questionElement.appendChild(answerElement);
                questionElement.appendChild(frequencyElement);

                fieldElement.appendChild(questionElement);

            }

            statisticsElement.appendChild(fieldElement);

        }

        parentElement.appendChild(statisticsElement);

    }

    private void createFormXML(Element rootElement, Document document,
        MessageNotification messageNotification) throws InterruptedException {
        int i = 1;
        for (long formId : this.formIds) {

            if (messageNotification != null) {
                if (messageNotification.isInterrupted()) {
                    throw new InterruptedException();
                }
                messageNotification.setMessage("Exporting Record " + i);
            }

            Form form = getFormDAO(formId);
            List<Grading> gradings = form.getPublicationId().getGradingCollection();
            Grading grading = null;
            if (gradings != null && gradings.size() > 0) {
                grading = gradings.iterator().next();
            }
            appendFormElement(rootElement, document, form, grading);
            i++;
        }
    }

    private String parseXMLString(String str) {
        String value = StringEscapeUtils.escapeXml10(str.replace(" ", "_"));
        return value;
    }

    private void appendFormElement(Element parentElement, Document document, Form form,
        Grading grading) {
        Element formElement = document.createElement("form");

        long formId = form.getFormId();
        double aggregateMark = form.getAggregateMark();
        long errorCount = form.getErrorCount();
        String formPassword = form.getFormPassword();

        formElement.setAttribute("id", formId + "");

        formElement.setAttribute("password", formPassword);

        // aggregate mark
        Element aggregateMarkElement = document.createElement("score");
        Text aggregateMarkText = document.createTextNode(aggregateMark + "");
        aggregateMarkElement.appendChild(aggregateMarkText);
        formElement.appendChild(aggregateMarkElement);

        // segment aggregate
        for (FormPage formPage : form.getFormPageCollection()) {
            for (Segment segment : formPage.getSegmentCollection()) {
                String name = segment.getName();
                double segmentAggregate = segment.getAggregateMark();
                Element segmentAggregateElement;
                if (name != null && name.trim().length() > 0) {
                    segmentAggregateElement =
                        document.createElement("segment_score_" + parseXMLString(name));
                } else {
                    segmentAggregateElement = document.createElement(
                        "segment_score_" + segment.getBarcodeOne() + "_" + segment.getBarcodeTwo());
                }
                Text segmentAggregateText = document.createTextNode(segmentAggregate + "");
                segmentAggregateElement.appendChild(segmentAggregateText);
                formElement.appendChild(segmentAggregateElement);
            }
        }

        if (grading != null) {

            // precentage
            double percentage = (aggregateMark / grading.getTotalPossibleScore()) * 100.0d;
            Element percentageElement = document.createElement("percentage");
            Text percentageText = document.createTextNode(percentage + "");
            percentageElement.appendChild(percentageText);
            formElement.appendChild(percentageElement);

            // grading
            String grade = Misc.getGrading(aggregateMark, grading.getGradingRuleCollection(),
                grading.getTotalPossibleScore());
            Element gradeElement = document.createElement("grade");
            Text gradeText = document.createTextNode(grade);
            gradeElement.appendChild(gradeText);
            formElement.appendChild(gradeElement);

        }

        // error count
        Element errorCountElement = document.createElement("errors");
        Text errorCountText = document.createTextNode(errorCount + "");
        errorCountElement.appendChild(errorCountText);
        formElement.appendChild(errorCountElement);

        // record
        Record record = form.getRecordId();
        if (record != null) {
            appendRecordElement(formElement, document, record);
        }

        // form page
        List<FormPage> formPages = form.getFormPageCollection();
        for (FormPage formPage : formPages) {
            appendFormPageElement(formElement, document, formPage);
        }

        parentElement.appendChild(formElement);

    }

    private void appendRecordElement(Element parentElement, Document document, Record record) {

        Element recordElement = document.createElement("sourceDataRecord");

        long recordId = record.getRecordId();
        recordElement.setAttribute("id", recordId + "");

        Collection<SourceText> sourceTextCollection = record.getSourceTextCollection();
        for (SourceText sourceText : sourceTextCollection) {
            SourceField sourceField = sourceText.getSourceFieldId();
            String fieldName = sourceField.getSourceFieldName();
            String value = sourceText.getSourceTextString();
            Element fieldElement = document.createElement("field");
            fieldElement.setAttribute("name", fieldName);
            Text fieldValueText = document.createTextNode(value);
            fieldElement.appendChild(fieldValueText);
            recordElement.appendChild(fieldElement);
        }

        parentElement.appendChild(recordElement);

    }

    private void appendFormPageElement(Element parentElement, Document document,
        FormPage formPage) {

        Element formPageElement = document.createElement("page");

        long formPageId = formPage.getFormPageId();
        long pageNumber = formPage.getFormPageNumber();

        formPageElement.setAttribute("id", formPageId + "");
        formPageElement.setAttribute("pageNumber", pageNumber + "");

        Timestamp processedTime = formPage.getProcessedTime();

        if (processedTime != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            String timeString = dateFormat.format(processedTime.getTime());
            formPageElement.setAttribute("processedTime", timeString);
        }

        List<Segment> segments = formPage.getSegmentCollection();

        if (segments != null && segments.size() > 0) {
            for (Segment segment : segments) {
                appendSegmentElement(formPageElement, document, segment);
            }
        }

        parentElement.appendChild(formPageElement);

    }

    private void appendSegmentElement(Element parentElement, Document document, Segment segment) {

        Element segmentElement = document.createElement("segment");

        String barcodeOneValue = segment.getBarcodeOne();
        String barcodeTwoValue = segment.getBarcodeTwo();

        List<FragmentOmr> fragmentOmrCollection = segment.getFragmentOmrCollection();
        List<FragmentBarcode> fragmentBarcodeCollection = segment.getFragmentBarcodeCollection();

        segmentElement.setAttribute("topRightBarcode", barcodeOneValue);
        segmentElement.setAttribute("bottomLeftBarcode", barcodeTwoValue);

        if (fragmentOmrCollection != null && fragmentOmrCollection.size() > 0) {
            for (FragmentOmr fragmentOmr : fragmentOmrCollection) {
                appendFragmentOmrElement(segmentElement, document, fragmentOmr);
            }
        }

        if (fragmentBarcodeCollection != null && fragmentBarcodeCollection.size() > 0) {
            for (FragmentBarcode fragmentBarcode : fragmentBarcodeCollection) {
                appendFragmentBarcodeElement(segmentElement, document, fragmentBarcode);
            }
        }

        parentElement.appendChild(segmentElement);

    }

    private void appendFragmentOmrElement(Element parentElement, Document document,
        FragmentOmr fragmentOmr) {

        Element fragmentOmrElement = document.createElement("markArea");

        String fieldName = fragmentOmr.getCapturedDataFieldName();
        String markFieldName = fragmentOmr.getMarkColumnName();
        double mark = fragmentOmr.getMark();

        fragmentOmrElement.setAttribute("name", fieldName);

        ArrayList<String> answers = new ArrayList<String>();

        List<CheckBox> checkBoxCollection = fragmentOmr.getCheckBoxCollection();
        if (checkBoxCollection != null && checkBoxCollection.size() > 0) {
            for (CheckBox checkBox : checkBoxCollection) {
                if (checkBox.getCheckBoxMarked() > 0) {
                    String answer = checkBox.getCheckBoxValue();
                    answers.add(answer);
                    Element markElement = document.createElement("detectedMark");
                    Text markValueText = document.createTextNode(answer);
                    markElement.appendChild(markValueText);
                    fragmentOmrElement.appendChild(markElement);
                }
            }
        }

        this.statAggregator.addAnswers(fieldName, answers, mark);

        Element scoreElement = document.createElement("score");
        scoreElement.setAttribute("name", markFieldName);
        Text scoreText = document.createTextNode(mark + "");
        scoreElement.appendChild(scoreText);

        fragmentOmrElement.appendChild(scoreElement);

        parentElement.appendChild(fragmentOmrElement);

    }

    private void appendFragmentBarcodeElement(Element parentElement, Document document,
        FragmentBarcode fragmentBarcode) {

        Element fragmentBarcodeElement = document.createElement("barcodeArea");

        String fieldName = fragmentBarcode.getCapturedDataFieldName();
        String barcodeValue = fragmentBarcode.getBarcodeValue();

        fragmentBarcodeElement.setAttribute("name", fieldName);

        Text barcodeValueText = document.createTextNode(barcodeValue);
        fragmentBarcodeElement.appendChild(barcodeValueText);

        parentElement.appendChild(fragmentBarcodeElement);

    }

    private void createPublicationXML(Element rootElement, Document document,
        MessageNotification messageNotification) throws InterruptedException {
        for (long publicationId : this.publicationIds) {
            Publication publication = getPublicationDAO(publicationId);
            appendPublicationElement(rootElement, document, publication, messageNotification);
        }
    }

    private void appendPublicationElement(Element parentElement, Document document,
        Publication publication, MessageNotification messageNotification)
        throws InterruptedException {

        Element publicationElement = document.createElement("publication");
        long publicationId = publication.getPublicationId();
        String publicationName = publication.getPublicationName();

        int publicationType = publication.getPublicationType();

        // get gradings object
        List<Grading> gradings = publication.getGradingCollection();
        Grading grading = null;
        if (gradings != null && gradings.size() > 0) {
            grading = gradings.iterator().next();
        }

        publicationElement.setAttribute("id", publicationId + "");

        publicationElement.setAttribute("name", publicationName);

        switch (publicationType) {
            case PublicationPreferences.FORM_ID_RECONCILE_WITH_SOURCE_DATA_RECORD:
                publicationElement
                    .setAttribute("type", Localizer.localize("Util", "PublicationType0"));
                break;
            case PublicationPreferences.RECONCILE_KEY_WITH_SOURCE_DATA_RECORD_NO_CREATE:
                publicationElement
                    .setAttribute("type", Localizer.localize("Util", "PublicationType1"));
                break;
            case PublicationPreferences.RECONCILE_KEY_WITH_SOURCE_DATA_RECORD_CREATE_NEW:
                publicationElement
                    .setAttribute("type", Localizer.localize("Util", "PublicationType2"));
                break;
        }

        boolean foundExcludeFilter = false;
        for (Filter filter : this.filters) {
            if (filter instanceof ExcludeEmptyRecordsFilter) {
                foundExcludeFilter = true;
                break;
            }
        }

        // forms
        List<Form> forms = null;

        if (foundExcludeFilter) {
            String formQuerySQL =
                "SELECT frm FROM Form frm WHERE frm.formId IN (SELECT fp.formId FROM FormPage fp WHERE fp.processedTime IS NOT NULL) AND frm.publicationId = :publicationId";
            Query formQuery = entityManager.createQuery(formQuerySQL, Form.class);
            formQuery.setParameter("publicationId", publication);
            forms = formQuery.getResultList();
        } else {
            forms = publication.getFormCollection();
        }

        if (forms == null) {
            return;
        }

        int i = 1;
        for (Form form : forms) {
            if (messageNotification != null) {
                if (messageNotification.isInterrupted()) {
                    throw new InterruptedException();
                }
                messageNotification.setMessage("Exporting Record " + i);
            }
            appendFormElement(publicationElement, document, form, grading);
            i++;
        }

        parentElement.appendChild(publicationElement);

    }

    private Form getFormDAO(long formId) {
        return this.entityManager.find(Form.class, formId);
    }

    private Publication getPublicationDAO(long publicationId) {
        return this.entityManager.find(Publication.class, publicationId);
    }

    public String toString() {
        StringWriter stringWriter = new StringWriter();
        String str = "";
        try {
            write(stringWriter, null);
            return stringWriter.toString();
        } catch (ParserConfigurationException e) {
            str += e.getMessage() + "\n";
        } catch (TransformerException e) {
            str += e.getMessage() + "\n";
        } catch (InterruptedException e) {
            str += e.getMessage() + "\n";
        }
        return str;
    }


}
