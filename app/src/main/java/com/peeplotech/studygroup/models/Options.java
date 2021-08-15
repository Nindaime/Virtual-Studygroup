package com.peeplotech.studygroup.models;

public class Options {
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String optionE;

    public Options(String optionA, String optionB, String optionC, String optionD, String optionE) {
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.optionE = optionE;
    }

    protected String getOptionA() {
        return optionA;
    }

    protected String getOptionB() {
        return optionB;
    }

    protected String getOptionC() {
        return optionC;
    }

    protected String getOptionD() {
        return optionD;
    }

    protected String getOptionE() {
        return optionE;
    }
}
