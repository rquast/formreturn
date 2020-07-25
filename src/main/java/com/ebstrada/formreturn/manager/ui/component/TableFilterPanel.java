package com.ebstrada.formreturn.manager.ui.component;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboPopup;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.persistence.model.AbstractDataModel;
import com.ebstrada.formreturn.manager.persistence.viewer.GenericDataViewer;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.dialog.FilterLimitResultsDialog;
import com.ebstrada.formreturn.manager.ui.dialog.FilterSearchResultsDialog;
import com.ebstrada.formreturn.manager.ui.dialog.FilterSortResultsDialog;

public class TableFilterPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private AbstractDataModel tableModel;

    private GenericDataViewer tableViewer;

    private BasicComboPopup pageSelectionPopup;

    private JComboBox pageSelectionList;

    public TableFilterPanel() {
        initComponents();
    }

    public AbstractDataModel getTableModel() {
        return tableModel;
    }

    public void setTableModel(AbstractDataModel tableModel) {
        this.tableModel = tableModel;
    }

    public GenericDataViewer getTableViewer() {
        return tableViewer;
    }

    public void setTableViewer(GenericDataViewer tableViewer) {
        this.tableViewer = tableViewer;
    }

    private void limitButtonActionPerformed(ActionEvent e) {

        FilterLimitResultsDialog limitDialog = new FilterLimitResultsDialog(Main.getInstance());
        limitDialog.setTableModel(this.tableModel);
        limitDialog.setModal(true);
        limitDialog.setVisible(true);

        if (limitDialog.getDialogResult() == JOptionPane.OK_OPTION) {
            updatePageNumbers();
            tableViewer.refresh(true, this);
        }

    }


    private void sortButtonActionPerformed(ActionEvent e) {

        FilterSortResultsDialog sortDialog = new FilterSortResultsDialog(Main.getInstance());
        sortDialog.setTableModel(this.tableModel);
        sortDialog.setModal(true);
        sortDialog.setVisible(true);

        if (sortDialog.getDialogResult() == JOptionPane.OK_OPTION) {
            updatePageNumbers();
            tableViewer.refresh(true, this);
        }

    }

    private void filterButtonActionPerformed(ActionEvent e) {

        FilterSearchResultsDialog searchDialog = new FilterSearchResultsDialog(Main.getInstance());
        searchDialog.setTableModel(this.tableModel);
        searchDialog.setModal(true);
        searchDialog.setVisible(true);

        if (searchDialog.getDialogResult() == JOptionPane.OK_OPTION) {
            updatePageNumbers();
            tableViewer.refresh(true, this);
        }

    }

    private void changeToPageNumber(int pageNumber) {
        if (pageNumber > 0) {
            tableModel.setOffset(getCurrentOffsetFromPageNumber(pageNumber));
        } else {
            tableModel.setOffset(0);
        }
        tableViewer.refresh(false, this);
    }

    private void previousPageButtonActionPerformed(ActionEvent e) {
        int currentPageNumber = getCurrentPageNumber();
        int nextPageNumber = currentPageNumber - 1;
        if (nextPageNumber > 0) {
            changeToPageNumber(nextPageNumber);
            updatePageNumbers();
        }
    }

    private void nextPageButtonActionPerformed(ActionEvent e) {
        int currentPageNumber = getCurrentPageNumber();
        int totalNumberOfPages = getTotalNumberOfPages();
        int nextPageNumber = currentPageNumber + 1;
        if (nextPageNumber <= totalNumberOfPages) {
            changeToPageNumber(nextPageNumber);
            updatePageNumbers();
        }
    }

    private void pageNumberLabelMouseClicked(MouseEvent e) {

        int numberOfPages = getTotalNumberOfPages();
        int currentPageNumber = getCurrentPageNumber();

        String[] pageNumberStrings = new String[numberOfPages];
        for (int i = 0; i < numberOfPages; i++) {
            pageNumberStrings[i] = "" + (i + 1);
        }
        pageSelectionList = new JComboBox(pageNumberStrings);
        pageSelectionList.setSelectedItem("" + currentPageNumber);
        pageSelectionList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int nextPageNumber = Integer.parseInt((String) pageSelectionList.getSelectedItem());
                pageSelectionPopup.hide();
                changeToPageNumber(nextPageNumber);
                updatePageNumbers();
            }
        });
        pageSelectionPopup = new BasicComboPopup(pageSelectionList);
        pageSelectionPopup.setPopupSize(100, 100);
        pageSelectionPopup.show(pageNumberLabel, e.getX(), e.getY());
    }

    public int getCurrentOffsetFromPageNumber(int pageNumber) {
        long limit = tableModel.getLimit();
        return (int) (limit * (pageNumber - 1));
    }

    public int getCurrentPageNumber() {
        long limit = tableModel.getLimit();
        long offset = tableModel.getOffset();
        return (int) ((offset / limit) + 1);
    }

    public int getTotalNumberOfPages() {
        long size = tableModel.getSize();
        long limit = tableModel.getLimit();
        return (int) Math.ceil(new Double(size) / new Double(limit));
    }

    public void updatePageNumbers() {
        int currentPageNumber = getCurrentPageNumber();
        int totalPages = getTotalNumberOfPages();
        pageNumberLabel.setText(String
            .format(Localizer.localize("UI", "FormFramePageNumberLabel"), currentPageNumber + "",
                totalPages + ""));
        recordCountLabel.setText(
            String.format(Localizer.localize("UI", "TotalLabelText"), tableModel.getSize() + ""));
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        bottomPanel = new JPanel();
        previousPageButton = new JButton();
        pageNumberLabel = new JLabel();
        nextPageButton = new JButton();
        recordCountLabel = new JLabel();
        limitButton = new JButton();
        sortButton = new JButton();
        searchButton = new JButton();

        //======== this ========
        setOpaque(false);
        setLayout(new GridBagLayout());
        ((GridBagLayout) getLayout()).columnWidths = new int[] {0, 0};
        ((GridBagLayout) getLayout()).rowHeights = new int[] {0, 0};
        ((GridBagLayout) getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
        ((GridBagLayout) getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

        //======== bottomPanel ========
        {
            bottomPanel.setOpaque(false);
            bottomPanel.setLayout(new GridBagLayout());
            ((GridBagLayout) bottomPanel.getLayout()).columnWidths =
                new int[] {0, 0, 0, 0, 15, 0, 15, 0, 0, 0, 0, 0};
            ((GridBagLayout) bottomPanel.getLayout()).rowHeights = new int[] {0, 0};
            ((GridBagLayout) bottomPanel.getLayout()).columnWeights =
                new double[] {1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0E-4};
            ((GridBagLayout) bottomPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

            //---- previousPageButton ----
            previousPageButton.setIcon(new ImageIcon(getClass().getResource(
                "/com/ebstrada/formreturn/manager/ui/icons/editor/page_previous.png")));
            previousPageButton.setBorderPainted(false);
            previousPageButton.setFocusPainted(false);
            previousPageButton.setMargin(new Insets(0, 0, 0, 0));
            previousPageButton.setContentAreaFilled(false);
            previousPageButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    previousPageButtonActionPerformed(e);
                }
            });
            previousPageButton
                .setToolTipText(Localizer.localize("UI", "FormFramePreviewPageToolTipText"));
            bottomPanel.add(previousPageButton,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //---- pageNumberLabel ----
            pageNumberLabel.setFont(UIManager.getFont("Label.font"));
            pageNumberLabel.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    pageNumberLabelMouseClicked(e);
                }
            });
            pageNumberLabel.setText(Localizer.localize("UI", "FormFrameDefaultPageNumber"));
            pageNumberLabel
                .setToolTipText(Localizer.localize("UI", "FormFrameShowAllPageNumbersToolTipText"));
            bottomPanel.add(pageNumberLabel,
                new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //---- nextPageButton ----
            nextPageButton.setIcon(new ImageIcon(getClass()
                .getResource("/com/ebstrada/formreturn/manager/ui/icons/editor/page_next.png")));
            nextPageButton.setBorderPainted(false);
            nextPageButton.setFocusPainted(false);
            nextPageButton.setMargin(new Insets(0, 0, 0, 0));
            nextPageButton.setContentAreaFilled(false);
            nextPageButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    nextPageButtonActionPerformed(e);
                }
            });
            nextPageButton.setToolTipText(Localizer.localize("UI", "FormFrameNextPageToolTipText"));
            bottomPanel.add(nextPageButton,
                new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //---- recordCountLabel ----
            recordCountLabel.setText("Total: ?");
            recordCountLabel.setFont(UIManager.getFont("Label.font"));
            bottomPanel.add(recordCountLabel,
                new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //---- limitButton ----
            limitButton.setIcon(new ImageIcon(getClass()
                .getResource("/com/ebstrada/formreturn/manager/ui/icons/table_lock.png")));
            limitButton.setFont(UIManager.getFont("Button.font"));
            limitButton.setFocusable(false);
            limitButton.setFocusPainted(false);
            limitButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    limitButtonActionPerformed(e);
                }
            });
            limitButton.setText(Localizer.localize("UI", "LimitButtonText"));
            bottomPanel.add(limitButton,
                new GridBagConstraints(7, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //---- sortButton ----
            sortButton.setIcon(new ImageIcon(getClass()
                .getResource("/com/ebstrada/formreturn/manager/ui/icons/table_sort.png")));
            sortButton.setFont(UIManager.getFont("Button.font"));
            sortButton.setFocusable(false);
            sortButton.setFocusPainted(false);
            sortButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    sortButtonActionPerformed(e);
                }
            });
            sortButton.setText(Localizer.localize("UI", "SortButtonText"));
            bottomPanel.add(sortButton,
                new GridBagConstraints(8, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //---- searchButton ----
            searchButton.setIcon(new ImageIcon(getClass()
                .getResource("/com/ebstrada/formreturn/manager/ui/icons/table_filter.png")));
            searchButton.setFont(UIManager.getFont("Button.font"));
            searchButton.setFocusable(false);
            searchButton.setFocusPainted(false);
            searchButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    filterButtonActionPerformed(e);
                }
            });
            searchButton.setText(Localizer.localize("UI", "SearchButtonText"));
            bottomPanel.add(searchButton,
                new GridBagConstraints(9, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));
        }
        add(bottomPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel bottomPanel;
    private JButton previousPageButton;
    private JLabel pageNumberLabel;
    private JButton nextPageButton;
    private JLabel recordCountLabel;
    private JButton limitButton;
    private JButton sortButton;
    private JButton searchButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
