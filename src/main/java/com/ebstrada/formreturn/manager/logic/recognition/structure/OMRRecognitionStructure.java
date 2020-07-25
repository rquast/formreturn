package com.ebstrada.formreturn.manager.logic.recognition.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.persistence.EntityManager;

import org.apache.commons.lang.ArrayUtils;

import com.ebstrada.aggregation.exception.ErrorFlagException;
import com.ebstrada.aggregation.exception.InvalidRulePartException;
import com.ebstrada.aggregation.exception.NoMatchException;
import com.ebstrada.formreturn.manager.persistence.jpa.CheckBox;
import com.ebstrada.formreturn.manager.persistence.jpa.FragmentOmr;
import com.ebstrada.formreturn.manager.util.Misc;

public class OMRRecognitionStructure extends FragmentRecognitionStructure {

    private String[][] characterData;
    private String aggregationRule;

    private int rowCount;
    private int columnCount;

    private int readDirection = OMRMatrix.READ_STRING_LEFT_TO_RIGHT;

    private ArrayList<CheckBoxRecognitionStructure> checkBoxRecognitionStructures;

    private boolean combineColumnCharacters = false;
    private boolean reconciliationKey = false;

    private boolean invalidated = false;

    public OMRRecognitionStructure() {
    }

    public OMRRecognitionStructure(FragmentOmr fragmentOmr) {

        setCheckBoxRecognitionStructures(fragmentOmr);

        setReadDirection(fragmentOmr.getReadDirection());
        setMarkFieldName(fragmentOmr.getMarkColumnName());
        setMarkOrderIndex((int) fragmentOmr.getMarkOrderIndex());
        setOrderIndex((int) fragmentOmr.getOrderIndex());
        setPercentX1(fragmentOmr.getX1Percent());
        setPercentX2(fragmentOmr.getX2Percent());
        setPercentY1(fragmentOmr.getY1Percent());
        setPercentY2(fragmentOmr.getY2Percent());
        setFieldName(fragmentOmr.getCapturedDataFieldName());
        setInvalidated(fragmentOmr.getInvalidated() > 0 ? true : false);
        setCombineColumnCharacters((fragmentOmr.getCombineColumnCharacters() == 1) ? true : false);
        setReconciliationKey((fragmentOmr.getReconciliationKey() == 1) ? true : false);
        setAggregationRule(fragmentOmr.getAggregationRule());

    }

    public void setCheckBoxRecognitionStructures(FragmentOmr fragmentOmr) {

        List<CheckBox> cbc = fragmentOmr.getCheckBoxCollection();

        if (cbc.size() > 0) {

            ArrayList<CheckBoxRecognitionStructure> structures =
                new ArrayList<CheckBoxRecognitionStructure>();

            rowCount = 0;
            columnCount = 0;

            for (CheckBox cb : cbc) {

                CheckBoxRecognitionStructure cbrs = new CheckBoxRecognitionStructure();

                cbrs.setFragmentXRatio(cb.getFragmentXRatio());
                cbrs.setFragmentYRatio(cb.getFragmentYRatio());
                cbrs.setRow(cb.getRowNumber());
                if (rowCount < (cb.getRowNumber() + 1)) {
                    rowCount = (cb.getRowNumber() + 1);
                }

                cbrs.setColumn(cb.getColumnNumber());
                if (columnCount < (cb.getColumnNumber() + 1)) {
                    columnCount = (cb.getColumnNumber() + 1);
                }

                cbrs.setCheckBoxValue(cb.getCheckBoxValue());
                cbrs.setCheckBoxMarked((cb.getCheckBoxMarked() > 0) ? true : false);

                structures.add(cbrs);

            }

            setCheckBoxRecognitionStructures(structures);
            setRowCount(rowCount);
            setColumnCount(columnCount);

        } else {

            // backward compatibility - versions 1.0.7 and lower

            setCharacterData(fragmentOmr.getCharacterData());

            String[][] characterData = getCharacterData();
            String[] capturedData = fragmentOmr.getCapturedData();

            ArrayList<CheckBoxRecognitionStructure> structures =
                new ArrayList<CheckBoxRecognitionStructure>();

            rowCount = characterData.length;
            columnCount = characterData[0].length;

            for (int i = 0; i < characterData.length; i++) {
                for (int j = 0; j < characterData[0].length; j++) {
                    CheckBoxRecognitionStructure cbrs = new CheckBoxRecognitionStructure();
                    cbrs.setRow((short) i);
                    cbrs.setColumn((short) j);
                    cbrs.setCheckBoxValue(characterData[i][j]);
                    if (ArrayUtils.contains(capturedData, characterData[i][j])) {
                        cbrs.setCheckBoxMarked(true);
                    } else {
                        cbrs.setCheckBoxMarked(false);
                    }
                    structures.add(cbrs);
                }
            }

            setCheckBoxRecognitionStructures(structures);

        }

    }

    public void setCharacterData(String[][] _characterData) {
        characterData = _characterData;
    }

    public String[][] getCharacterData() {
        return characterData;
    }

    public int getNumberOfRows() {
        if (characterData == null) {
            return rowCount;
        }
        return characterData.length;
    }

    public int getNumberOfColumns() {
        if (characterData == null) {
            return columnCount;
        }

        if (characterData[0] != null) {
            return characterData[0].length;
        } else {
            return 0;
        }
    }

    public String getAggregationRule() {
        return aggregationRule;
    }

    public void setAggregationRule(String aggregationRule) {
        this.aggregationRule = aggregationRule;
    }

    public boolean isCombineColumnCharacters() {
        return combineColumnCharacters;
    }

    public void setCombineColumnCharacters(boolean combineColumnCharacters) {
        this.combineColumnCharacters = combineColumnCharacters;
    }

    public boolean isReconciliationKey() {
        return reconciliationKey;
    }

    public void setReconciliationKey(boolean reconciliationKey) {
        this.reconciliationKey = reconciliationKey;
    }

    public ArrayList<CheckBoxRecognitionStructure> getCheckBoxRecognitionStructures() {
        return checkBoxRecognitionStructures;
    }

    public void setCheckBoxRecognitionStructures(
        ArrayList<CheckBoxRecognitionStructure> checkBoxRecognitionStructures) {
        this.checkBoxRecognitionStructures = checkBoxRecognitionStructures;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    public int getReadDirection() {
        return readDirection;
    }

    public void setReadDirection(int readDirection) {
        this.readDirection = readDirection;
    }

    public String getCapturedString(String[][] capturedData, int direction) {
        return getCapturedString(capturedData, direction, null);
    }

    public String getCapturedString(String[][] capturedData, int direction, String delimiter) {

        String markString = "";

        Vector<String> values = new Vector<String>();

        if (capturedData != null) {

            switch (direction) {

                case OMRMatrix.READ_STRING_LEFT_TO_RIGHT:

                    for (int i = 0; i < capturedData[0].length; i++) {
                        for (int j = 0; j < capturedData.length; j++) {

                            if (capturedData[j][i] == null) {
                                continue;
                            }

                            if (delimiter == null) {
                                markString += capturedData[j][i];
                            } else {
                                values.add(capturedData[j][i]);
                            }

                        }
                    }

                    break;

                case OMRMatrix.READ_STRING_TOP_TO_BOTTOM:

                    for (int i = 0; i < capturedData.length; i++) {
                        for (int j = 0; j < capturedData[0].length; j++) {

                            if (capturedData[i][j] == null) {
                                continue;
                            }

                            if (delimiter == null) {
                                markString += capturedData[i][j];
                            } else {
                                values.add(capturedData[i][j]);
                            }

                        }
                    }

                    break;

            }

            if (delimiter != null) {
                markString = Misc.implode(values, delimiter);
            }

        }

        return markString;

    }

    public String[][] getCapturedData() {

        String[][] capturedData = new String[getNumberOfRows()][getNumberOfColumns()];

        if (getCheckBoxRecognitionStructures() != null
            && getCheckBoxRecognitionStructures().size() > 0) {
            for (CheckBoxRecognitionStructure cbrs : getCheckBoxRecognitionStructures()) {
                if (cbrs.isCheckBoxMarked()) {
                    capturedData[cbrs.getRow()][cbrs.getColumn()] = cbrs.getCheckBoxValue();
                }
            }
        }

        return capturedData;

    }

    public String toString() {

        String str = "";
        String[][] capturedData = getCapturedData();

        if (isReconciliationKey() || isCombineColumnCharacters()) {
            str = getCapturedString(capturedData, getReadDirection());
        } else {
            str = getCapturedString(capturedData, getReadDirection(), ",");
        }

        return str;
    }

    public void setCapturedData(OMRBox[][] omrBoxMatrix) {
        if (getCheckBoxRecognitionStructures() != null
            && getCheckBoxRecognitionStructures().size() > 0) {
            for (CheckBoxRecognitionStructure cbrs : getCheckBoxRecognitionStructures()) {
                if (omrBoxMatrix[cbrs.getRow()][cbrs.getColumn()].isMarked()) {
                    cbrs.setCheckBoxMarked(true);
                } else {
                    cbrs.setCheckBoxMarked(false);
                }
            }
        }
    }

    public void persistToFragmentOmr(EntityManager entityManager, FragmentOmr fragmentOmr)
        throws InvalidRulePartException, ErrorFlagException {

        if (fragmentOmr.getCheckBoxCollection() == null
            || fragmentOmr.getCheckBoxCollection().size() <= 0) {
            for (CheckBoxRecognitionStructure cbrs : getCheckBoxRecognitionStructures()) {
                CheckBox cb = new CheckBox();
                cb.setCheckBoxValue(cbrs.getCheckBoxValue());
                cb.setColumnNumber(cbrs.getColumn());
                cb.setRowNumber(cbrs.getRow());
                cb.setFragmentXRatio(cbrs.getFragmentXRatio());
                cb.setFragmentYRatio(cbrs.getFragmentYRatio());
                cb.setCheckBoxMarked(cbrs.isCheckBoxMarked() ? (short) 1 : (short) 0);
                cb.setFragmentOmrId(fragmentOmr);
                entityManager.persist(cb);
                entityManager.flush();
            }
        } else {
            for (CheckBoxRecognitionStructure cbrs : getCheckBoxRecognitionStructures()) {
                for (CheckBox cb : fragmentOmr.getCheckBoxCollection()) {
                    if (cb.getRowNumber() == cbrs.getRow() && cb.getColumnNumber() == cbrs
                        .getColumn()) {
                        cb.setCheckBoxValue(cbrs.getCheckBoxValue());
                        cb.setColumnNumber(cbrs.getColumn());
                        cb.setRowNumber(cbrs.getRow());
                        cb.setFragmentXRatio(cbrs.getFragmentXRatio());
                        cb.setFragmentYRatio(cbrs.getFragmentYRatio());
                        cb.setCheckBoxMarked(cbrs.isCheckBoxMarked() ? (short) 1 : (short) 0);
                        cb.setFragmentOmrId(fragmentOmr);
                        entityManager.persist(cb);
                    }
                }
            }
        }

        fragmentOmr.setCapturedData(null);

        double mark = 0.0d;

        if (isReconciliationKey() || isCombineColumnCharacters()) {
            String capturedString = getCapturedString(getCapturedData(), getReadDirection());
            fragmentOmr.setCapturedString(capturedString);
            try {
                mark = Misc.aggregate(0.0d, new String[] {capturedString},
                    fragmentOmr.getAggregationRule());
            } catch (NoMatchException e) {
                mark = 0.0d;
            }
        } else {
            try {
                mark = Misc.aggregate(0.0d, getCapturedData(), fragmentOmr.getAggregationRule());
            } catch (NoMatchException e) {
                mark = 0.0d;
            }
        }

        fragmentOmr.setMark(mark);

        entityManager.persist(fragmentOmr);
        entityManager.flush();

    }

    public boolean isInvalidated() {
        return invalidated;
    }

    public void setInvalidated(boolean containsError) {
        this.invalidated = containsError;
    }

}
