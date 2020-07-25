package com.ebstrada.formreturn.manager.ui.editor.dialog;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.*;
import javax.swing.border.*;
import com.ebstrada.formreturn.manager.ui.component.*;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.svg.SVGDocument;

import com.ebstrada.formreturn.manager.gef.base.Globals;
import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.FigImage;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.persistence.xstream.Document;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.filefilter.FilenameExtensionFilter;

public class FigImageProperties extends JDialog {

    private static final long serialVersionUID = 1L;

    private FigImage selectedElement;

    private int dialogResult = javax.swing.JOptionPane.CANCEL_OPTION;

    private File selectedFile;

    public FigImageProperties(Frame owner, Fig selectedElement) {
        super(owner);
        initComponents();
        this.selectedElement = (FigImage) selectedElement;
        updateSourceURLTextFieldFromFig();
    }

    public FigImageProperties(Dialog owner) {
        super(owner);
        initComponents();
    }

    private void updateSourceURLTextFieldFromFig() {

        if (selectedElement.getImageFileName() != null) {
            selectedFile =
                new File(getWorkingDirName() + "/images/" + selectedElement.getImageFileName());
            if (selectedFile.exists()) {
                imagePreviewLabel.setText("");
                createPreview();
            } else {
                String message = String.format(Localizer.localize("UI", "FileNotFoundMessage"),
                    selectedFile.getName());
                String caption = Localizer.localize("UI", "ReadErrorTitle");
                javax.swing.JOptionPane.showConfirmDialog(this, message, caption,
                    javax.swing.JOptionPane.DEFAULT_OPTION, javax.swing.JOptionPane.ERROR_MESSAGE);
                clear();
            }
        } else {
            return;
        }

    }

    private Document getDocument() {
        return selectedElement.getGraph().getDocument();
    }

    private String getWorkingDirName() {
        return selectedElement.getGraph().getDocumentPackage().getWorkingDirName();
    }

    private void okButtonActionPerformed(ActionEvent e) {
        setDialogResult(JOptionPane.OK_OPTION);

        try {
            if (selectedFile == null) {
                selectedElement.setImageFileName(null);
                Image _image = (new ImageIcon(getClass()
                    .getResource("/com/ebstrada/formreturn/manager/gef/Images/Image.png")))
                    .getImage();
                selectedElement.setImage(_image);
                selectedElement.damage();
            } else {

                Document document = getDocument();
                String imageFileName = document.addImage(selectedFile, getWorkingDirName());

                selectedElement.setImageFileName(imageFileName);
                selectedElement.setImage(null);

                selectedElement.damage();

            }
        } catch (Exception ex) {
            String message = String.format(Localizer.localize("UI", "UnableToReadFileMessage"),
                selectedFile.getName());
            String caption = Localizer.localize("UI", "ReadErrorTitle");
            javax.swing.JOptionPane
                .showConfirmDialog(this, message, caption, javax.swing.JOptionPane.DEFAULT_OPTION,
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            clear();
        }

        dispose();

    }

    private void createPreview() {
        try {
            if (selectedFile.toURI().toString().endsWith("svg") || selectedFile.toURI().toString()
                .endsWith("SVG")) {
                UserAgent userAgent = new UserAgentAdapter();
                DocumentLoader loader = new DocumentLoader(userAgent);
                BridgeContext ctx = new BridgeContext(userAgent, loader);
                ctx.setDynamic(true);
                GVTBuilder builder = new GVTBuilder();
                SVGDocument svgDoc =
                    (SVGDocument) loader.loadDocument(selectedFile.toURI().toString());
                GraphicsNode graphicsNode = builder.build(ctx, svgDoc);
                Rectangle2D SVGbounds = graphicsNode.getBounds();
                BufferedImage bufferedImage =
                    new BufferedImage((int) SVGbounds.getWidth() + (int) SVGbounds.getX(),
                        (int) SVGbounds.getHeight() + (int) SVGbounds.getY(),
                        BufferedImage.TYPE_INT_ARGB);
                Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
                graphics.clearRect(0, 0, (int) SVGbounds.getWidth(), (int) SVGbounds.getHeight());
                graphics.setBackground(Color.WHITE);
                graphics
                    .fill(new Rectangle(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight()));
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                graphicsNode.paint(graphics);
                imagePreviewLabel.setIcon(new ImageIcon(bufferedImage));
            } else {
                imagePreviewLabel.setIcon(new ImageIcon(selectedFile.toURL()));
            }
        } catch (Exception ex) {
            String message = String.format(Localizer.localize("UI", "UnableToReadFileMessage"),
                selectedFile.getName());
            String caption = Localizer.localize("UI", "ReadErrorTitle");
            javax.swing.JOptionPane
                .showConfirmDialog(this, message, caption, javax.swing.JOptionPane.DEFAULT_OPTION,
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            clear();
        }
    }

    private void browseButtonActionPerformed(ActionEvent e) {

        File file = null;
        FilenameExtensionFilter filter = new FilenameExtensionFilter();
        filter.addExtension("png");
        filter.addExtension("jpg");
        filter.addExtension("jpeg");
        filter.addExtension("gif");
        filter.addExtension("tif");
        filter.addExtension("tiff");
        filter.addExtension("svg");

        FileDialog fd =
            new FileDialog(Main.getInstance(), Localizer.localize("UI", "LoadImageDialogTitle"),
                FileDialog.LOAD);
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

        selectedFile = file;
        if (selectedFile != null) {
            imagePreviewLabel.setText("");
        } else {
            imagePreviewLabel
                .setText(Localizer.localize("UI", "ImagePropertiesNoImageLoadedLabel"));
        }
        createPreview();

    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        dispose();
    }

    private void clear() {
        imagePreviewLabel.setIcon(null);
        this.selectedFile = null;
        imagePreviewLabel.setText(Localizer.localize("UI", "ImagePropertiesNoImageLoadedLabel"));
    }

    private void clearButtonActionPerformed(ActionEvent e) {
        clear();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Roland Quast
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        scrollPane1 = new JScrollPane();
        imagePreviewLabel = new JLabel();
        buttonBar = new JPanel();
        helpLabel = new JHelpLabel();
        clearButton = new JButton();
        browseButton = new JButton();
        okButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
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

                //======== scrollPane1 ========
                {
                    scrollPane1.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
                    scrollPane1.setOpaque(false);
                    scrollPane1.setBackground(null);

                    //---- imagePreviewLabel ----
                    imagePreviewLabel.setBackground(Color.gray);
                    imagePreviewLabel.setOpaque(true);
                    imagePreviewLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    imagePreviewLabel.setForeground(Color.white);
                    imagePreviewLabel.setFont(UIManager.getFont("TitledBorder.font"));
                    imagePreviewLabel.setText(Localizer.localize("UI", "ImagePropertiesNoImageLoadedLabel"));
                    scrollPane1.setViewportView(imagePreviewLabel);
                }
                contentPanel.add(scrollPane1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 85, 80};
                ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 0.0, 0.0, 0.0};

                //---- helpLabel ----
                helpLabel.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/help.png")));
                helpLabel.setHelpGUID("image-tool");
                helpLabel.setToolTipText(Localizer.localize("UI", "ClickHereForHelpToolTip"));
                buttonBar.add(helpLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- clearButton ----
                clearButton.setFocusPainted(false);
                clearButton.setFont(UIManager.getFont("Button.font"));
                clearButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        clearButtonActionPerformed(e);
                    }
                });
                clearButton.setText(Localizer.localize("UI", "ImagePropertiesClearButtonImageButtonText"));
                buttonBar.add(clearButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- browseButton ----
                browseButton.setFocusPainted(false);
                browseButton.setFont(UIManager.getFont("Button.font"));
                browseButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        browseButtonActionPerformed(e);
                    }
                });
                browseButton.setText(Localizer.localize("UI", "ImagePropertiesLoadImageButtonText"));
                buttonBar.add(browseButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- okButton ----
                okButton.setFocusPainted(false);
                okButton.setFont(UIManager.getFont("Button.font"));
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        okButtonActionPerformed(e);
                    }
                });
                okButton.setText(Localizer.localize("UI", "OKButtonText"));
                buttonBar.add(okButton, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setFocusPainted(false);
                cancelButton.setFont(UIManager.getFont("Button.font"));
                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cancelButtonActionPerformed(e);
                    }
                });
                cancelButton.setText(Localizer.localize("UI", "CancelButtonText"));
                buttonBar.add(cancelButton, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(735, 485);
        setLocationRelativeTo(null);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Roland Quast
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JScrollPane scrollPane1;
    private JLabel imagePreviewLabel;
    private JPanel buttonBar;
    private JHelpLabel helpLabel;
    private JButton clearButton;
    private JButton browseButton;
    private JButton okButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public int getDialogResult() {
        return dialogResult;
    }

    public void setDialogResult(int dialogResult) {
        this.dialogResult = dialogResult;
    }
}
