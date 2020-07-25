package com.ebstrada.formreturn.manager.logic.export.stats;

import java.util.ArrayList;

import com.ebstrada.formreturn.manager.util.NoObfuscation;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("statisticsMap") public class StatisticMap implements NoObfuscation {

    private ArrayList<String> fieldNames = new ArrayList<String>();

    private ArrayList<Statistic> statistics = new ArrayList<Statistic>();

    public StatisticMap() {

    }

    public void addAnswers(String fieldName, ArrayList<String> answers, double mark) {
        if (this.fieldNames.contains(fieldName)) {
            Statistic stat = this.statistics.get(this.fieldNames.indexOf(fieldName));
            stat.addAnswers(answers, mark);
        } else {
            Statistic stat = new Statistic();
            this.fieldNames.add(fieldName);
            this.statistics.add(stat);
            stat.addAnswers(answers, mark);
        }
    }

    public ArrayList<String> getFieldNames() {
        return fieldNames;
    }

    public ArrayList<Statistic> getStatistics() {
        return statistics;
    }

}
