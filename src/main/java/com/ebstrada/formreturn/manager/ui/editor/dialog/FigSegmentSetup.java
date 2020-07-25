package com.ebstrada.formreturn.manager.ui.editor.dialog;

import java.awt.*;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FileDialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.*;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import com.ebstrada.formreturn.manager.ui.component.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ebstrada.formreturn.manager.gef.base.Globals;
import com.ebstrada.formreturn.manager.gef.graph.presentation.JGraph;
import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.FigCheckbox;
import com.ebstrada.formreturn.manager.gef.presentation.FigSegment;
import com.ebstrada.formreturn.manager.gef.ui.DocumentAttributes;
import com.ebstrada.formreturn.manager.gef.ui.PageAttributes;
import com.ebstrada.formreturn.manager.gef.ui.SegmentContainer;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.persistence.xstream.Document;
import com.ebstrada.formreturn.manager.persistence.xstream.Page;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.editor.table.JTableComponentModel;
import com.ebstrada.formreturn.manager.ui.editor.table.JTableButtonMouseListener;
import com.ebstrada.formreturn.manager.ui.editor.table.JTableButtonRenderer;
import com.ebstrada.formreturn.manager.ui.filefilter.FilenameExtensionFilter;
import com.ebstrada.formreturn.manager.util.Misc;

public class FigSegmentSetup extends JDialog {

    private static final long serialVersionUID = 1L;

    private static Log log = LogFactory.getLog(FigSegmentSetup.class);

    private FigSegment figSegment;

    private SegmentContainer segmentAttributes = new SegmentContainer();

    private int dialogResult = javax.swing.JOptionPane.CANCEL_OPTION;

    public static final int EDIT_BUTTON = 0;

    public static final int REMOVE_BUTTON = 1;

    public static int segmentType = 1;

    public String defaultSelectedSegment = "Random";

    public FigSegmentSetup(FigSegment newFigSegment) {
        super(Main.getInstance(), true);
        figSegment = newFigSegment;
        initComponents();
        setTableModel();
        loadSegmentAttributes();
        getRootPane().setDefaultButton(okButton);
    }

    private void openSegment() {

        File file = null;
        FilenameExtensionFilter filter = new FilenameExtensionFilter();
        filter.addExtension("frs");

        FileDialog fd = new FileDialog(Main.getInstance(),
            Localizer.localize("UI", "SegmentSetupLoadSegmentTitle"), FileDialog.LOAD);
        fd.setLocationRelativeTo(null);
        fd.setFilenameFilter(filter);

        File lastDir = null;

        if (Globals.getLastDirectory() != null) {
            lastDir = new File(Globals.getLastDirectory());
            if (!(lastDir.exists())) {
                lastDir = null;
            }
        }

        if (lastDir == null) {
            lastDir = new File(System.getProperty("user.home"));
        }

        try {
            fd.setDirectory(lastDir.getCanonicalPath());
        } catch (IOException e1) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
            return;
        }

        fd.setModal(true);
        fd.setVisible(true);

        if (fd.getFile() != null) {
            String filename = fd.getFile();
            file = new File(fd.getDirectory() + filename);
            if (file.isDirectory()) {
                return;
            }
            try {
                Globals.setLastDirectory(file.getCanonicalPath());
            } catch (IOException ldex) {
            }
        } else {
            return;
        }

        JGraph graph = new JGraph();
        if (file != null) {
            try {
                graph.getDocumentPackage().open(file, graph);
                Document documentContainer = graph.getDocument();
                addSegment(documentContainer, graph);
            } catch (Exception ex) {
                Main.applicationExceptionLog.error(String
                        .format(Localizer.localize("UI", "UnableToAddFileMessage"), file.getPath()),
                    ex);
                return;
            }
        }
        try {
            graph.getDocumentPackage().close();
        } catch (Exception e) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
        }

        segmentsTable.revalidate();

    }

    public void addSegment(Document segment, JGraph graph) {
        addSegment(segment, graph, getLinkID(), false);
    }

    public void addSegment(Document segment, JGraph graph, String linkID,
        boolean ignoreDuplicates) {

        PageAttributes pageAttributes = segment.getPageByPageNumber(1).getPageAttributes();
        DocumentAttributes documentAttributes = segment.getDocumentAttributes();

        if (verifySegment(pageAttributes) == false) {
            return;
        }

        // check for already registered captured data field names
        // get a list of fieldnames from the graph's document (the form)
        Vector<String> formFieldnames = new Vector<String>();


        // everything inside the active graph
        List<Fig> graphFigs = figSegment.getGraph().getEditor().getLayerManager().getContents();
        if (graphFigs != null && graphFigs.size() > 0) {
            for (Fig fig : graphFigs) {
                if (fig != figSegment && fig instanceof FigSegment) {
                    SegmentContainer segmentContainer = ((FigSegment) fig).getSegmentContainer();
                    int numberOfSegments = segmentContainer.getNumberOfSegments();
                    for (int j = 0; j < numberOfSegments; j++) {
                        Document internalSegment = segmentContainer.getSegment(j);
                        Page internalSegmentPage = internalSegment.getPageByPageNumber(1);
                        ArrayList<Fig> internalSegmentFigs = internalSegmentPage.getFigs();
                        if (internalSegmentFigs != null && internalSegmentFigs.size() > 0) {
                            for (Fig internalSegmentFig : internalSegmentFigs) {
                                if (internalSegmentFig instanceof FigCheckbox) {
                                    formFieldnames
                                        .add(((FigCheckbox) internalSegmentFig).getFieldname());
                                }
                            }
                        }
                    }
                }
            }
        }

        // everything from the stored document inside the graph (eg, other non-displayed pages)
        int numberOfPages = figSegment.getGraph().getDocument().getNumberOfPages();
        for (int i = 1; i <= numberOfPages; i++) {
            Page page = figSegment.getGraph().getDocument().getPageByPageNumber(i);
            ArrayList<Fig> figs = page.getFigs();
            if (figs != null && figs.size() > 0) {
                for (Fig fig : figs) {
                    if (fig != figSegment && fig instanceof FigSegment) {
                        SegmentContainer segmentContainer =
                            ((FigSegment) fig).getSegmentContainer();
                        int numberOfSegments = segmentContainer.getNumberOfSegments();
                        for (int j = 0; j < numberOfSegments; j++) {
                            Document internalSegment = segmentContainer.getSegment(j);
                            Page internalSegmentPage = internalSegment.getPageByPageNumber(1);
                            ArrayList<Fig> internalSegmentFigs = internalSegmentPage.getFigs();
                            if (internalSegmentFigs != null && internalSegmentFigs.size() > 0) {
                                for (Fig internalSegmentFig : internalSegmentFigs) {
                                    if (internalSegmentFig instanceof FigCheckbox) {
                                        formFieldnames
                                            .add(((FigCheckbox) internalSegmentFig).getFieldname());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // get a list of fieldnames from the segment.page(1).figs (if figcheckbox)
        Vector<String> segmentFieldnames = new Vector<String>();
        Page page = segment.getPageByPageNumber(1);
        ArrayList<Fig> figs = page.getFigs();
        if (figs != null && figs.size() > 0) {
            for (Fig fig : figs) {
                if (fig instanceof FigCheckbox) {
                    FigCheckbox figCheckbox = (FigCheckbox) fig;
                    if (!(figCheckbox.isReconciliationKey())) {
                        segmentFieldnames.add(figCheckbox.getFieldname());
                    }
                }
            }
        }

        // compare and print out results. return if found duplicates
        Vector<String> duplicateFieldnames = new Vector<String>();
        for (String segmentFieldname : segmentFieldnames) {
            for (String formFieldname : formFieldnames) {
                if (segmentFieldname.equalsIgnoreCase(formFieldname)) {
                    duplicateFieldnames.add(segmentFieldname);
                }
            }
        }

        if (!ignoreDuplicates && duplicateFieldnames.size() > 0) {

            // create a string of duplicate fieldnames
            String message = Localizer.localize("UI", "SegmentSetupCannotAddSegmentMessage") + "\n";

            for (String duplicateFieldname : duplicateFieldnames) {
                message += duplicateFieldname + "\n";
            }

            // output as error message
            Misc.showErrorMsg(Main.getInstance(), message);

        }


        JTableComponentModel jtbm = (JTableComponentModel) segmentsTable.getModel();
        jtbm.addRow(
            new Object[] {linkID, documentAttributes.getName(), documentAttributes.getGUID(),
                getButton(FigSegmentSetup.REMOVE_BUTTON, documentAttributes.getGUID())});

        segmentAttributes.addSegment(segment);

        if (graph != null) {
            mergeImages(segment, graph);
            mergeFonts(segment, graph);
        }

        updateDefaultSegmentComboBox(jtbm);

    }

    private void mergeImages(Document segment, JGraph graph) {
        Document document =
            Main.getInstance().getSelectedFrame().getGraph().getDocumentPackage().getDocument();

        List segmentImages = segment.getImages();

        String segmentWorkingDirName = graph.getDocumentPackage().getWorkingDirName();

        for (Iterator segmentImageIterator = segmentImages.iterator(); segmentImageIterator
            .hasNext(); ) {
            String segmentImageFileName = (String) segmentImageIterator.next();

            try {
                document
                    .addImage(new File(segmentWorkingDirName + "/images/" + segmentImageFileName),
                        getWorkingDirName());
            } catch (NoSuchAlgorithmException e) {
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
            } catch (IOException e) {
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
            }

        }

    }

    private void mergeFonts(Document segment, JGraph graph) {
        Document document =
            Main.getInstance().getSelectedFrame().getGraph().getDocumentPackage().getDocument();

        List segmentFonts = segment.getFonts();

        String segmentWorkingDirName = graph.getDocumentPackage().getWorkingDirName();

        for (Iterator segmentFontIterator = segmentFonts.iterator(); segmentFontIterator
            .hasNext(); ) {
            String segmentFontFileName = (String) segmentFontIterator.next();

            try {
                document.addFont(new File(segmentWorkingDirName + "/fonts/" + segmentFontFileName),
                    getWorkingDirName());
            } catch (NoSuchAlgorithmException e) {
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
            } catch (IOException e) {
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
            }

        }

    }

    private String getWorkingDirName() {
        return Main.getInstance().getSelectedFrame().getGraph().getDocumentPackage()
            .getWorkingDirName();
    }

    private void updateDefaultSegmentComboBox(JTableComponentModel jtbm) {
        String selectedSegment = (String) defaultSegmentComboBox.getSelectedItem();
        defaultSegmentComboBox.removeAllItems();
        defaultSegmentComboBox.addItem("Random");
        for (int i = 0; i < jtbm.getRowCount(); i++) {
            defaultSegmentComboBox.addItem(jtbm.getValueAt(i, 0));
        }
        defaultSegmentComboBox.setSelectedItem(selectedSegment);
        defaultSelectedSegment = selectedSegment;
    }

    private void defaultSegmentComboBoxActionPerformed(ActionEvent e) {
        defaultSelectedSegment = (String) defaultSegmentComboBox.getSelectedItem();
    }

    private boolean verifySegment(PageAttributes pageAttributes) {

        if (pageAttributes == null) {
            String message = Localizer.localize("UI", "SegmentSetupUnableToReadFileMessage");
            String caption = Localizer.localize("UI", "WarningTitle");
            javax.swing.JOptionPane.showConfirmDialog(Main.getInstance(), message, caption,
                javax.swing.JOptionPane.DEFAULT_OPTION, javax.swing.JOptionPane.WARNING_MESSAGE);
            return false;
        }

        Vector uniqueIDVector = getUniqueIDVector();
        if (uniqueIDVector.contains(pageAttributes.getGUID())) {
            String message = Localizer.localize("UI", "SegmentSetupSegmentAlreadyLoadedMessage");
            String caption = Localizer.localize("UI", "WarningTitle");
            javax.swing.JOptionPane.showConfirmDialog(Main.getInstance(), message, caption,
                javax.swing.JOptionPane.DEFAULT_OPTION, javax.swing.JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    private void loadSegmentAttributes() {

        int listSize = figSegment.getSegmentAttributes().getListSize();

        for (int i = 0; i < listSize; i++) {
            addSegment(figSegment.getSegmentAttributes().getSegment(i), null,
                figSegment.getSegmentAttributes().getLinkID(i), true);
        }

        defaultSelectedSegment = figSegment.getSegmentAttributes().getDefaultSelectedSegment();
        defaultSegmentComboBox.setSelectedItem(defaultSelectedSegment);

        linkIDField.setText(figSegment.getSegmentAttributes().getLinkFieldname());

    }

    private String getLinkID() {

        JTableComponentModel jtbm = (JTableComponentModel) segmentsTable.getModel();
        Vector<Object> linkIDVector = new Vector<Object>();
        String linkID = "linkid0";
        linkIDVector.add(linkID);

        int rowCount = jtbm.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            linkIDVector.add(jtbm.getValueAt(i, 0)); // column 0 is linkID
        }

        for (int i = 0; i < linkIDVector.size(); i++) {
            if (linkIDVector.contains("linkid" + i)) {
                continue;
            } else {
                linkID = "linkid" + i;
                break;
            }

        }

        return linkID;
    }

    private Vector getUniqueIDVector() {

        JTableComponentModel jtbm = (JTableComponentModel) segmentsTable.getModel();
        Vector<Object> uniqueIDVector = new Vector<Object>();
        int rowCount = jtbm.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            uniqueIDVector.add(jtbm.getValueAt(i, 2)); // column 2 is uniqueID
        }
        return uniqueIDVector;

    }

    public void removeSegment(String uniqueID) {

        Vector uniqueIDVector = getUniqueIDVector();
        if (!(uniqueIDVector.contains(uniqueID))) {
            return;
        }
        int index = uniqueIDVector.indexOf(uniqueID);
        JTableComponentModel jtbm = (JTableComponentModel) segmentsTable.getModel();
        jtbm.removeRow(index);
        segmentsTable.updateUI();

        segmentAttributes.removeSegment(index);

        updateDefaultSegmentComboBox(jtbm);

    }

    private void setTableModel() {
        TableCellRenderer defaultRenderer = segmentsTable.getDefaultRenderer(JButton.class);
        segmentsTable.setDefaultRenderer(JButton.class, new JTableButtonRenderer(defaultRenderer));
        segmentsTable.addMouseListener(new JTableButtonMouseListener(segmentsTable));
        segmentsTable.setCellSelectionEnabled(true);
        segmentsTable.setModel(new JTableComponentModel(
            new String[] {Localizer.localize("UI", "SegmentSetupLinkIDColumnName"),
                Localizer.localize("UI", "SegmentSetupSegmentNameColumnName"),
                Localizer.localize("UI", "SegmentSetupIdentifierHashColumnName"),
                Localizer.localize("UI", "SegmentSetupActionColumnName")}, 0) {

            private static final long serialVersionUID = 1L;

            boolean[] columnEditable = new boolean[] {true, false, false, false};

            @Override public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnEditable[columnIndex];
            }
        });

        TableModelAdapter adapt = new TableModelAdapter();
        TableModel tm = segmentsTable.getModel();
        tm.addTableModelListener(adapt);

        TableColumn col;
        col = segmentsTable.getColumnModel().getColumn(0);
        col.setPreferredWidth(50);
        col = segmentsTable.getColumnModel().getColumn(2);
        col.setPreferredWidth(134);
        col = segmentsTable.getColumnModel().getColumn(3);
        col.setPreferredWidth(6);

        segmentsTable.getTableHeader().setReorderingAllowed(false);

    }

    class TableModelAdapter implements javax.swing.event.TableModelListener {
        public void tableChanged(javax.swing.event.TableModelEvent e) {
            if (e.getType() == TableModelEvent.UPDATE) {

                int rowChanged = e.getLastRow();
                int columnChanged = e.getColumn();

                String newValue = (String) segmentsTable.getValueAt(rowChanged, columnChanged);

                TableModel tm = segmentsTable.getModel();
                int foundMatchingValue = 0;

                for (int i = 0; i < tm.getRowCount(); i++) {
                    if (tm.getValueAt(i, columnChanged).equals(newValue)) {
                        foundMatchingValue++;
                    }
                }

                if (foundMatchingValue > 1) {
                    segmentsTable.setValueAt(getLinkID(), rowChanged, columnChanged);
                    String message =
                        Localizer.localize("UI", "SegmentSetupDuplicateLinkExistsMessage");
                    String caption = Localizer.localize("UI", "WarningTitle");
                    javax.swing.JOptionPane.showConfirmDialog(Main.getInstance(), message, caption,
                        javax.swing.JOptionPane.DEFAULT_OPTION,
                        javax.swing.JOptionPane.WARNING_MESSAGE);
                }

                updateDefaultSegmentComboBox((JTableComponentModel) segmentsTable.getModel());

            }
        }
    }


    private JButton getButton(int buttonType, String uniqueID) {

        JButton button = new JButton();

        switch (buttonType) {
            case REMOVE_BUTTON:
                button.setText(Localizer.localize("UI", "RemoveButtonText"));
                button.setActionCommand(uniqueID);
                button.setFont(UIManager.getFont("Button.font"));
                button.setIcon(new ImageIcon(getClass()
                    .getResource("/com/ebstrada/formreturn/manager/ui/icons/delete.png")));
                button.addMouseListener(new MouseListener() {
                    public void mouseClicked(MouseEvent e) {
                        removeSegment(((JButton) (e.getSource())).getActionCommand());
                    }

                    public void mouseEntered(MouseEvent e) {
                    }

                    public void mouseExited(MouseEvent e) {
                    }

                    public void mousePressed(MouseEvent e) {
                    }

                    public void mouseReleased(MouseEvent e) {
                    }
                });
                break;
        }
        return button;
    }

    private ArrayList getLinkIDs() {
        ArrayList<Object> linkIDs = new ArrayList<Object>();
        JTableComponentModel jtbm = (JTableComponentModel) segmentsTable.getModel();
        int rowCount = jtbm.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            linkIDs.add(jtbm.getValueAt(i, 0)); // column 0 is linkID
        }
        return linkIDs;
    }

    private void okButtonActionPerformed(ActionEvent e) {
        setDialogResult(JOptionPane.OK_OPTION);
        segmentAttributes.setLinkIDs(getLinkIDs());
        segmentAttributes.setLinkFieldname(linkIDField.getText());
        defaultSelectedSegment = (String) defaultSegmentComboBox.getSelectedItem();
        segmentAttributes.setDefaultSegment(defaultSelectedSegment);
        figSegment.setSegmentAttributes(segmentAttributes);

        // check the new width and height of figSegment is within page bounds
        int newWidth = figSegment.getWidth();
        int newHeight = figSegment.getHeight();
        int x = figSegment.getX();
        int y = figSegment.getY();

        int croppedWidth = figSegment.getGraph().getPageAttributes().getCroppedWidth();
        int croppedHeight = figSegment.getGraph().getPageAttributes().getCroppedHeight();

        // then move it left if greater than the bounds of its graph
        if ((x + newWidth) > croppedWidth) {
            int moveToX = croppedWidth - newWidth;
            if (moveToX < 0) {
                moveToX = 0;
            }
            figSegment.setX(moveToX);
        }

        if ((y + newHeight) > croppedHeight) {
            int moveToY = croppedHeight - newHeight;
            if (moveToY < 0) {
                moveToY = 0;
            }
            figSegment.setY(moveToY);
        }

        figSegment.damage();

        setVisible(false);
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        setDialogResult(JOptionPane.CANCEL_OPTION);
        dispose();
    }

    public int getDialogResult() {
        return dialogResult;
    }

    public void setDialogResult(int dialogResult) {
        this.dialogResult = dialogResult;
    }

    private void addSegmentButtonActionPerformed(ActionEvent e) {
        openSegment();
    }

    private void thisWindowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                okButton.requestFocusInWindow();
            }
        });
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY
        // //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Roland Quast
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        panel2 = new JPanel();
        segmentAreaDescriptionPanel = new JPanel();
        segmentAreaDescriptionLabel = new JLabel();
        segmentAreaDescriptionHelpLabel = new JHelpLabel();
        panel1 = new JPanel();
        linkFieldnameLabel = new JLabel();
        linkIDField = new JTextField();
        defaultSegmentLabel = new JLabel();
        defaultSegmentComboBox = new JComboBox();
        linkIDDescriptionLabel = new JLabel();
        segmentsScrollPane = new JScrollPane();
        segmentsTable = new JTable();
        panel3 = new JPanel();
        addSegmentButton = new JButton();
        panel4 = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                thisWindowGainedFocus(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));

            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout)contentPanel.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout)contentPanel.getLayout()).rowHeights = new int[] {0, 0};
                ((GridBagLayout)contentPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                ((GridBagLayout)contentPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                //======== panel2 ========
                {
                    panel2.setBorder(null);
                    panel2.setLayout(new GridBagLayout());
                    ((GridBagLayout)panel2.getLayout()).columnWidths = new int[] {0, 0};
                    ((GridBagLayout)panel2.getLayout()).rowHeights = new int[] {45, 55, 0, 0, 0, 0, 0};
                    ((GridBagLayout)panel2.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                    ((GridBagLayout)panel2.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0E-4};

                    //======== segmentAreaDescriptionPanel ========
                    {
                        segmentAreaDescriptionPanel.setBorder(new MatteBorder(0, 0, 3, 0, Color.gray));
                        segmentAreaDescriptionPanel.setOpaque(false);
                        segmentAreaDescriptionPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)segmentAreaDescriptionPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
                        ((GridBagLayout)segmentAreaDescriptionPanel.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)segmentAreaDescriptionPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 1.0, 1.0E-4};
                        ((GridBagLayout)segmentAreaDescriptionPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                        //---- segmentAreaDescriptionLabel ----
                        segmentAreaDescriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        segmentAreaDescriptionLabel.setFont(UIManager.getFont("Label.font"));
                        segmentAreaDescriptionLabel.setText("<html><body><strong>" + Localizer.localize("UICDM", "SegmentAreaDescriptionLabel") + "</strong></body></html>");
                        segmentAreaDescriptionPanel.add(segmentAreaDescriptionLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- segmentAreaDescriptionHelpLabel ----
                        segmentAreaDescriptionHelpLabel.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/help.png")));
                        segmentAreaDescriptionHelpLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        segmentAreaDescriptionHelpLabel.setFont(UIManager.getFont("Label.font"));
                        segmentAreaDescriptionHelpLabel.setHelpGUID("embed-segments");
                        segmentAreaDescriptionHelpLabel.setToolTipText(Localizer.localize("UI", "ClickHereForHelpToolTip"));
                        segmentAreaDescriptionPanel.add(segmentAreaDescriptionHelpLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));
                    }
                    panel2.add(segmentAreaDescriptionPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //======== panel1 ========
                    {
                        panel1.setLayout(new GridBagLayout());
                        ((GridBagLayout)panel1.getLayout()).columnWidths = new int[] {0, 205, 0, 0, 150, 0};
                        ((GridBagLayout)panel1.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)panel1.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0, 0.0, 0.0, 1.0E-4};
                        ((GridBagLayout)panel1.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                        //---- linkFieldnameLabel ----
                        linkFieldnameLabel.setFont(UIManager.getFont("Label.font"));
                        linkFieldnameLabel.setText(Localizer.localize("UI", "SegmentSetupLinkFieldnameLabel"));
                        panel1.add(linkFieldnameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- linkIDField ----
                        linkIDField.setFont(UIManager.getFont("TextField.font"));
                        panel1.add(linkIDField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- defaultSegmentLabel ----
                        defaultSegmentLabel.setFont(UIManager.getFont("Label.font"));
                        defaultSegmentLabel.setText(Localizer.localize("UI", "SegmentSetupDefaultSegmentLabel"));
                        panel1.add(defaultSegmentLabel, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- defaultSegmentComboBox ----
                        defaultSegmentComboBox.setModel(new DefaultComboBoxModel(new String[] {
                            "Random"
                        }));
                        defaultSegmentComboBox.setFont(UIManager.getFont("ComboBox.font"));
                        defaultSegmentComboBox.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                defaultSegmentComboBoxActionPerformed(e);
                            }
                        });
                        panel1.add(defaultSegmentComboBox, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    panel2.add(panel1, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //---- linkIDDescriptionLabel ----
                    linkIDDescriptionLabel.setFont(UIManager.getFont("Label.font"));
                    linkIDDescriptionLabel.setText(Localizer.localize("UICDM", "LinkIDDescriptionLabel"));
                    panel2.add(linkIDDescriptionLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //======== segmentsScrollPane ========
                    {
                        segmentsScrollPane.setFocusable(false);

                        //---- segmentsTable ----
                        segmentsTable.setFocusable(false);
                        segmentsTable.setShowHorizontalLines(false);
                        segmentsTable.setShowVerticalLines(false);
                        segmentsTable.setFont(UIManager.getFont("Table.font"));
                        segmentsTable.setShowGrid(false);
                        segmentsTable.getTableHeader().setFont(UIManager.getFont("TableHeader.font"));
                        segmentsScrollPane.setViewportView(segmentsTable);
                    }
                    panel2.add(segmentsScrollPane, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //======== panel3 ========
                    {
                        panel3.setLayout(new GridBagLayout());
                        ((GridBagLayout)panel3.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
                        ((GridBagLayout)panel3.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)panel3.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0, 1.0E-4};
                        ((GridBagLayout)panel3.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                        //---- addSegmentButton ----
                        addSegmentButton.setFont(UIManager.getFont("Button.font"));
                        addSegmentButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/add.png")));
                        addSegmentButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                addSegmentButtonActionPerformed(e);
                            }
                        });
                        addSegmentButton.setText(Localizer.localize("UI", "SegmentSetupAddSegmentButtonText"));
                        panel3.add(addSegmentButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));
                    }
                    panel2.add(panel3, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //======== panel4 ========
                    {
                        panel4.setLayout(new GridBagLayout());
                        ((GridBagLayout)panel4.getLayout()).columnWidths = new int[] {55, 85, 80, 0};
                        ((GridBagLayout)panel4.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)panel4.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 1.0E-4};
                        ((GridBagLayout)panel4.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                        //---- okButton ----
                        okButton.setFont(UIManager.getFont("Button.font"));
                        okButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/accept.png")));
                        okButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                okButtonActionPerformed(e);
                            }
                        });
                        okButton.setText(Localizer.localize("UI", "OKButtonText"));
                        panel4.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- cancelButton ----
                        cancelButton.setFont(UIManager.getFont("Button.font"));
                        cancelButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/cross.png")));
                        cancelButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                cancelButtonActionPerformed(e);
                            }
                        });
                        cancelButton.setText(Localizer.localize("UI", "CancelButtonText"));
                        panel4.add(cancelButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    panel2.add(panel4, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                contentPanel.add(panel2, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(790, 500);
        setLocationRelativeTo(getOwner());
        // //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY
    // //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Roland Quast
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JPanel panel2;
    private JPanel segmentAreaDescriptionPanel;
    private JLabel segmentAreaDescriptionLabel;
    private JHelpLabel segmentAreaDescriptionHelpLabel;
    private JPanel panel1;
    private JLabel linkFieldnameLabel;
    private JTextField linkIDField;
    private JLabel defaultSegmentLabel;
    private JComboBox defaultSegmentComboBox;
    private JLabel linkIDDescriptionLabel;
    private JScrollPane segmentsScrollPane;
    private JTable segmentsTable;
    private JPanel panel3;
    private JButton addSegmentButton;
    private JPanel panel4;
    private JButton okButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration //GEN-END:variables

    /**
     * @return the segmentType
     */
    public static int getSegmentType() {
        return segmentType;
    }

    /**
     * @param segmentType the segmentType to set
     */
    public static void setSegmentType(int segmentType) {
        FigSegmentSetup.segmentType = segmentType;
    }

    /**
     * @return the segmentType
     */
}
