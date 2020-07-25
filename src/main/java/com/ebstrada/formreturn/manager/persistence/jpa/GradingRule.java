package com.ebstrada.formreturn.manager.persistence.jpa;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity @Table(name = "GRADING_RULE") public class GradingRule implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id @Column(name = "GRADING_RULE_ID") @GeneratedValue(strategy = IDENTITY) private long
        gradingRuleId;

    @Version @Column(name = "VERSION") private int version;

    @ManyToOne @JoinColumn(name = "GRADING_ID") private Grading gradingId;

    @Column(name = "ORDER_INDEX") private int orderIndex;

    @Column(name = "GRADE") private String grade;

    @Column(name = "TRHESHOLD") private double threshold;

    @Column(name = "TRHESHOLD_TYPE") private short thresholdType;

    @Column(name = "QUALIFIER") private short qualifier;

    public GradingRule() {
        super();
    }

    public long getGradingRuleId() {
        return gradingRuleId;
    }

    public void setGradingRuleId(long gradingRuleId) {
        this.gradingRuleId = gradingRuleId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Grading getGradingId() {
        return gradingId;
    }

    public void setGradingId(Grading gradingId) {
        this.gradingId = gradingId;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public short getThresholdType() {
        return thresholdType;
    }

    public void setThresholdType(short thresholdType) {
        this.thresholdType = thresholdType;
    }

    public short getQualifier() {
        return qualifier;
    }

    public void setQualifier(short qualifier) {
        this.qualifier = qualifier;
    }

}
