package com.ebstrada.formreturn.manager.logic.export.stats;

import java.util.ArrayList;

import com.ebstrada.formreturn.manager.util.NoObfuscation;

public class Statistic implements NoObfuscation {

    private ArrayList<String> answers = new ArrayList<String>();

    private ArrayList<Integer> frequencies = new ArrayList<Integer>();

    private double correct = 0;

    private double incorrect = 0;

    private double totalReponses = 0;

    public double getPercentageCorrect() {
        return (this.correct / this.totalReponses) * 100.0d;
    }

    public double getPercentageIncorrect() {
        return (this.incorrect / this.totalReponses) * 100.0d;
    }

    public void addAnswer(String answer, double score) {

        if (this.answers.contains(answer)) {
            int index = this.answers.indexOf(answer);
            Integer frequency = this.frequencies.get(index);
            this.frequencies.set(index, (frequency + 1));
        } else {
            this.answers.add(answer);
            this.frequencies.add(new Integer(1));
        }

        if (score > 0) {
            this.correct++;
        } else {
            this.incorrect++;
        }

        this.totalReponses++;

    }

    public void addAnswers(ArrayList<String> newAnswers, double score) {

        for (String answer : newAnswers) {
            if (this.answers.contains(answer)) {
                int index = this.answers.indexOf(answer);
                Integer frequency = this.frequencies.get(index);
                this.frequencies.set(index, (frequency + 1));
            } else {
                this.answers.add(answer);
                this.frequencies.add(new Integer(1));
            }
        }

        if (score > 0) {
            this.correct++;
        } else {
            this.incorrect++;
        }

        this.totalReponses++;

    }

    public ArrayList<String> getAnswers() {
        return answers;
    }

    public ArrayList<Integer> getFrequencies() {
        return frequencies;
    }

    public double getTotalReponses() {
        return totalReponses;
    }

}
