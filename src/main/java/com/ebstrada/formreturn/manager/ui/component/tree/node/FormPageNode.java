package com.ebstrada.formreturn.manager.ui.component.tree.node;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.component.tree.RecordTree;
import com.ebstrada.formreturn.manager.ui.component.tree.RecordTreeNode;
import com.ebstrada.formreturn.manager.ui.component.tree.node.menu.FormPagePopupMenu;

public class FormPageNode extends RecordNode {

    private long pageNumber;
    private long errorCount;
    private Timestamp processedTime;
    private double aggregateMark;
    private String formPassword;

    @Override public void loadChildren() throws Exception {
        // FormPages have no children
    }

    @Override public String toString() {

        String scoreString = "";

        String processedTimeString = "";
        if (processedTime != null) {

            scoreString += " " + String
                .format(Localizer.localize("UI", "RecordTreeFormPageNodePageScoreLabel"),
                    getAggregateMark() + "");

            if (getErrorCount() > 0) {
                processedTimeString += " " + String
                    .format(Localizer.localize("UI", "RecordTreeFormPageNodeErrorCountLabel"),
                        getErrorCount() + "");
            }
            processedTimeString += " " + String
                .format(Localizer.localize("UI", "RecordTreeFormPageNodeProcessedTimeLabel"),
                    new SimpleDateFormat().format(processedTime.getTime()));
        }

        return String
            .format(Localizer.localize("UI", "RecordTreeFormPageNodeLabel"), getRecordId() + "",
                getFormPassword() + "", getPageNumber() + scoreString + processedTimeString);
    }

    @Override public Icon getIcon() {
        return new ImageIcon(getClass().getResource(
            "/com/ebstrada/formreturn/manager/ui/icons/tree/form_page" + getFlagString() + ".png"));
    }

    public String getFlagString() {
        if (errorCount > 0) {
            return "_red";
        }
        if (processedTime != null) {
            return "_green";
        }
        return "";
    }

    public void setPageNumber(long formPageNumber) {
        this.pageNumber = formPageNumber;
    }

    public long getPageNumber() {
        return pageNumber;
    }

    public void setErrorCount(long errorCount) {
        this.errorCount = errorCount;
    }

    public long getErrorCount() {
        return errorCount;
    }

    public void setProcessedTime(Timestamp processedTime) {
        this.processedTime = processedTime;
    }

    public Timestamp getProcessedTime() {
        return processedTime;
    }

    public void setAggregateMark(double aggregateMark) {
        this.aggregateMark = aggregateMark;
    }

    public double getAggregateMark() {
        return aggregateMark;
    }

    public void setFormPassword(String formPassword) {
        this.formPassword = formPassword;
    }

    public String getFormPassword() {
        return formPassword;
    }

    @Override public JPopupMenu getPopupMenu(RecordTree tree, RecordTreeNode node) {
        return new FormPagePopupMenu(this, tree, node);
    }

}
