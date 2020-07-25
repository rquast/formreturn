package com.ebstrada.formreturn.manager.persistence.jpa;

import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.openjpa.persistence.jdbc.Index;

@Entity @Table(name = "FRAGMENT_OMR") public class FragmentOmr implements Serializable {
    @Id @Column(name = "FRAGMENT_OMR_ID") @GeneratedValue(strategy = IDENTITY) private long
        fragmentOmrId;

    @Version @Column(name = "VERSION") private int version;

    @Column(name = "X2_PERCENT") private double x2Percent;

    @Column(name = "CAPTURED_DATA_FIELD_NAME") private String capturedDataFieldName;

    @Column(name = "AGGREGATION_RULE") private String aggregationRule;

    @Column(name = "X1_PERCENT") private double x1Percent;

    @Column(name = "Y1_PERCENT") private double y1Percent;

    @Column(name = "Y2_PERCENT") private double y2Percent;

    @Index(name = "IDX_FRAGMENT_OMR_ORDER_INDEX") @Column(name = "ORDER_INDEX") private long
        orderIndex;

    @Column(name = "READ_DIRECTION") private short readDirection;

    @Column(name = "INVALIDATED") private short invalidated;

    @Column(name = "ERROR_TYPE") private short errorType;

    // TODO: deprecate characterData field - use CheckBox table instead
    // serialization of this data type is unreadable in other languages
    @Basic(fetch = FetchType.LAZY) @Column(name = "CHARACTER_DATA") private String[][]
        characterData;

    // TODO: deprecate capturedData field - use CheckBox table instead
    @Basic(fetch = FetchType.LAZY) @Column(name = "CAPTURED_DATA") private String[] capturedData;

    @Column(name = "CAPTURED_STRING") private String capturedString;

    @Column(name = "MARK") private double mark;

    @Column(name = "MARK_COLUMN_NAME") private String markColumnName;

    @Column(name = "MARK_ORDER_INDEX") private long markOrderIndex;

    @Column(name = "COMBINE_COLUMN_CHARACTERS") private short combineColumnCharacters;

    @Column(name = "RECONCILIATION_KEY") private short reconciliationKey;

    @Column(name = "BOX_WIDTH") private int boxWidth;

    @Column(name = "BOX_HEIGHT") private int boxHeight;

    @Column(name = "WIDTH_ROUNDNESS") private int widthRoundness;

    @Column(name = "HEIGHT_ROUNDNESS") private int heightRoundness;

    @OneToMany(mappedBy = "fragmentOmrId", cascade = REMOVE) @OrderBy("rowNumber, columnNumber ASC")
    private List<CheckBox> checkBoxCollection;

    @ManyToOne @JoinColumn(name = "SEGMENT_ID") private Segment segmentId;

    private static final long serialVersionUID = 1L;

    public FragmentOmr() {
        super();
    }

    public long getFragmentOmrId() {
        return this.fragmentOmrId;
    }

    public void setFragmentOmrId(long fragmentOmrId) {
        this.fragmentOmrId = fragmentOmrId;
    }

    public double getX2Percent() {
        return this.x2Percent;
    }

    public void setX2Percent(double x2Percent) {
        this.x2Percent = x2Percent;
    }

    public String getCapturedDataFieldName() {
        return this.capturedDataFieldName;
    }

    public void setCapturedDataFieldName(String capturedDataFieldName) {
        this.capturedDataFieldName = capturedDataFieldName;
    }

    public String getAggregationRule() {
        return this.aggregationRule;
    }

    public void setAggregationRule(String aggregationRule) {
        this.aggregationRule = aggregationRule;
    }

    public double getX1Percent() {
        return this.x1Percent;
    }

    public void setX1Percent(double x1Percent) {
        this.x1Percent = x1Percent;
    }

    public double getY1Percent() {
        return this.y1Percent;
    }

    public void setY1Percent(double y1Percent) {
        this.y1Percent = y1Percent;
    }

    public double getY2Percent() {
        return this.y2Percent;
    }

    public void setY2Percent(double y2Percent) {
        this.y2Percent = y2Percent;
    }

    public Segment getSegmentId() {
        return this.segmentId;
    }

    public void setSegmentId(Segment segmentId) {
        this.segmentId = segmentId;
    }

    public void setCharacterData(String[][] characterData) {
        this.characterData = characterData;
    }

    public String[][] getCharacterData() {
        return characterData;
    }

    public void setCapturedData(String[] capturedData) {
        this.capturedData = capturedData;
    }

    public String[] getCapturedData() {
        return capturedData;
    }

    public double getMark() {
        return this.mark;
    }

    public void setMark(double mark) {
        this.mark = mark;
    }

    public void setCombineColumnCharacters(short combineColumnCharacters) {
        this.combineColumnCharacters = combineColumnCharacters;
    }

    public void setReconciliationKey(short reconciliationKey) {
        this.reconciliationKey = reconciliationKey;
    }

    public short getCombineColumnCharacters() {
        return combineColumnCharacters;
    }

    public short getReconciliationKey() {
        return reconciliationKey;
    }

    public String getCapturedString() {
        return capturedString;
    }

    public void setCapturedString(String capturedString) {
        this.capturedString = capturedString;
    }

    public long getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(long orderIndex) {
        this.orderIndex = orderIndex;
    }

    public String getMarkColumnName() {
        if (markColumnName == null || markColumnName.trim().length() <= 0) {
            markColumnName = capturedDataFieldName + "_score";
        }
        return markColumnName;
    }

    public void setMarkColumnName(String markColumnName) {
        this.markColumnName = markColumnName;
    }

    public long getMarkOrderIndex() {
        return markOrderIndex;
    }

    public void setMarkOrderIndex(long markOrderIndex) {
        this.markOrderIndex = markOrderIndex;
    }

    public int getBoxWidth() {
        return boxWidth;
    }

    public void setBoxWidth(int boxWidth) {
        this.boxWidth = boxWidth;
    }

    public int getBoxHeight() {
        return boxHeight;
    }

    public void setBoxHeight(int boxHeight) {
        this.boxHeight = boxHeight;
    }

    public int getWidthRoundness() {
        return widthRoundness;
    }

    public void setWidthRoundness(int widthRoundness) {
        this.widthRoundness = widthRoundness;
    }

    public int getHeightRoundness() {
        return heightRoundness;
    }

    public void setHeightRoundness(int heightRoundness) {
        this.heightRoundness = heightRoundness;
    }

    public List<CheckBox> getCheckBoxCollection() {
        return checkBoxCollection;
    }

    public void setCheckBoxCollection(List<CheckBox> checkBoxCollection) {
        this.checkBoxCollection = checkBoxCollection;
    }

    public int getReadDirection() {
        return readDirection;
    }

    public void setReadDirection(short readDirection) {
        this.readDirection = readDirection;
    }

    public short getInvalidated() {
        return invalidated;
    }

    public void setInvalidated(short invalidated) {
        this.invalidated = invalidated;
    }

    public short getErrorType() {
        return errorType;
    }

    public void setErrorType(short errorType) {
        this.errorType = errorType;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

}
