package com.ebstrada.formreturn.manager.persistence.jpa;

import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity @Table(name = "GRADING") public class Grading implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id @Column(name = "GRADING_ID") @GeneratedValue(strategy = IDENTITY) private long gradingId;

    @Version @Column(name = "VERSION") private int version;

    @Column(name = "TOTAL_POSSIBLE_SCORE") private double totalPossibleScore;

    @ManyToOne @JoinColumn(name = "PUBLICATION_ID") private Publication publicationId;

    @OneToMany(mappedBy = "gradingId", cascade = REMOVE) private List<GradingRule>
        gradingRuleCollection;

    public Grading() {
        super();
    }

    public long getGradingId() {
        return gradingId;
    }

    public void setGradingId(long gradingId) {
        this.gradingId = gradingId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public double getTotalPossibleScore() {
        return totalPossibleScore;
    }

    public void setTotalPossibleScore(double totalPossibleScore) {
        this.totalPossibleScore = totalPossibleScore;
    }

    public List<GradingRule> getGradingRuleCollection() {
        return gradingRuleCollection;
    }

    public void setGradingRuleCollection(List<GradingRule> gradingRuleCollection) {
        this.gradingRuleCollection = gradingRuleCollection;
    }

    public Publication getPublicationId() {
        return publicationId;
    }

    public void setPublicationId(Publication publicationId) {
        this.publicationId = publicationId;
    }

}
