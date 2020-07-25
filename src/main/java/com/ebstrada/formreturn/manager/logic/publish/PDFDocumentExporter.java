package com.ebstrada.formreturn.manager.logic.publish;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.fop.pdf.PDFInfo;
import org.apache.fop.svg.PDFDocumentGraphics2D;
import org.apache.xmlgraphics.java2d.GraphicContext;

import com.ebstrada.formreturn.manager.gef.graph.presentation.JGraph;
import com.ebstrada.formreturn.manager.gef.ui.PageAttributes;
import com.ebstrada.formreturn.manager.persistence.jpa.Form;
import com.ebstrada.formreturn.manager.persistence.jpa.FormPage;
import com.ebstrada.formreturn.manager.persistence.xstream.Document;
import com.ebstrada.formreturn.manager.persistence.xstream.Page;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.util.Misc;

public class PDFDocumentExporter extends PDFDocumentGraphics2D {

    private FormRenderer formRenderer;

    private Document document;

    private OutputStream outputStream;

    private JGraph graph;

    public PDFDocumentExporter(Document document, String workingDirName,
        OutputStream outputStream) {
        super(true);
        formRenderer = new FormRenderer();
        formRenderer.setGraphics(this);
        formRenderer.setWorkingDirName(workingDirName);
        this.document = document;
        this.outputStream = outputStream;
    }

    public PDFDocumentExporter(JGraph graph, String workingDirName, OutputStream outputStream) {
        super(true);
        formRenderer = new FormRenderer();
        formRenderer.setGraphics(this);
        formRenderer.setWorkingDirName(workingDirName);
        this.graph = graph;
        this.outputStream = outputStream;
    }

    public FormRenderer getFormRenderer() {
        return formRenderer;
    }

    public void setFormRenderer(FormRenderer formRenderer) {
        this.formRenderer = formRenderer;
    }

    public void createPreviewPDF(Map<String, String> recordMap) throws Exception {

        PageAttributes currentPageAttributes = graph.getPageAttributes();

        PDFInfo info = getPDFDocument().getInfo();
        info.setProducer(Main.APPLICATION_NAME + " " + Main.VERSION);

        setDeviceDPI(PDFDocumentGraphics2D.NORMAL_PDF_RESOLUTION);

        setupDefaultFontInfo();

        setGraphicContext(new GraphicContext());

        formRenderer = new FormRenderer();

        formRenderer.setWorkingDirName(graph.getDocumentPackage().getWorkingDirName());
        formRenderer.setPageAttributes(currentPageAttributes);

        formRenderer.setLeftMarginOffset(currentPageAttributes.getLeftMargin());
        formRenderer.setTopMarginOffset(currentPageAttributes.getTopMargin());

        String barcodeValue =
            "12345-67890"; // The default preview barcode (maybe need to make this settable)

        formRenderer.setRecordMap(graph.getRecordMap());
        formRenderer.setStructureData(graph, barcodeValue);

        formRenderer.setGraphics(this);

        OutputStream out = new java.io.BufferedOutputStream(outputStream);
        setupDocument(out, formRenderer.getFullWidth(), formRenderer.getFullHeight());
        preparePainting();

        // paint a white background or it will be transparent.
        formRenderer.getGraphics().setColor(Color.WHITE);
        formRenderer.getGraphics().fill(
            new Rectangle2D.Double(0, 0, currentPageAttributes.getFullWidth(),
                currentPageAttributes.getFullHeight()));

        formRenderer.paintGraph(graph);

        finish();

    }

    public void createPDF(EntityManager entityManager, long formId) throws Exception {

        Form form = entityManager.find(Form.class, formId);

        Query query = entityManager.createNamedQuery("FormPage.findByFormId");
        query.setParameter("formId", form);
        List<FormPage> formPages = query.getResultList();

        formRenderer.setRecordMap(Misc.getRecordMap(form.getRecordId()));

        PDFInfo info = getPDFDocument().getInfo();
        info.setProducer(Main.APPLICATION_NAME + " " + Main.VERSION);
        info.setAuthor(document.getDocumentAttributes().getAuthor());
        info.setTitle(document.getDocumentAttributes().getName());

        setDeviceDPI(PDFDocumentGraphics2D.NORMAL_PDF_RESOLUTION);

        setupDefaultFontInfo();

        setGraphicContext(new GraphicContext());

        try {
            OutputStream out = new java.io.BufferedOutputStream(outputStream);

            for (int i = 0; i < document.getNumberOfPages(); i++) {
                int pageNumber = i + 1;
                Page pageContainer = document.getPageByPageNumber(pageNumber);
                paintPDFPage(out, pageContainer, formPages.get(i));
                nextPage();
            }
            finish();
        } catch (IOException ex) {
            throw ex;
        }
    }

    private void paintPDFPage(OutputStream out, Page pageContainer, FormPage formPage)
        throws IOException {

        PageAttributes pageAttributes = pageContainer.getPageAttributes();
        formRenderer.setPageAttributes(pageAttributes);
        formRenderer.setLeftMarginOffset(pageAttributes.getLeftMargin());
        formRenderer.setTopMarginOffset(pageAttributes.getTopMargin());

        setupDocument(out, formRenderer.getFullWidth(), formRenderer.getFullHeight());

        preparePainting();

        String barcodeValue =
            formPage.getFormPageId() + "-" + formPage.getFormId().getFormPassword();

        formRenderer.paintPage(pageContainer, barcodeValue, formPage);

    }

}
