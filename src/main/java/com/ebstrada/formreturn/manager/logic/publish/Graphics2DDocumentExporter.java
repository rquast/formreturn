package com.ebstrada.formreturn.manager.logic.publish;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

import com.ebstrada.formreturn.manager.gef.graph.presentation.JGraph;
import com.ebstrada.formreturn.manager.gef.ui.PageAttributes;

public class Graphics2DDocumentExporter {

    private FormRenderer formRenderer;

    private JGraph graph;

    public Graphics2DDocumentExporter(JGraph graph) {
        this.graph = graph;
    }

    public Graphics2D getGraphics() {
        if (formRenderer == null) {
            return null;
        }
        return formRenderer.getGraphics();
    }

    public void export(Graphics2D graphics) {

        PageAttributes currentPageAttributes = graph.getPageAttributes();

        if (formRenderer == null) {

            formRenderer = new FormRenderer();


            formRenderer.setWorkingDirName(graph.getDocumentPackage().getWorkingDirName());
            formRenderer.setPageAttributes(currentPageAttributes);

            formRenderer.setLeftMarginOffset(currentPageAttributes.getLeftMargin());
            formRenderer.setTopMarginOffset(currentPageAttributes.getTopMargin());

            String barcodeValue =
                "12345-67890"; // The default preview barcode (maybe need to make this settable)

            formRenderer.setRecordMap(graph.getRecordMap());
            formRenderer.setStructureData(graph, barcodeValue);

        }

        formRenderer.setGraphics(graphics);

        // paint a white background or it will be transparent.
        formRenderer.getGraphics().setColor(Color.WHITE);
        formRenderer.getGraphics().fill(
            new Rectangle2D.Double(0, 0, currentPageAttributes.getFullWidth(),
                currentPageAttributes.getFullHeight()));

        formRenderer.paintGraph(graph);

    }

}
