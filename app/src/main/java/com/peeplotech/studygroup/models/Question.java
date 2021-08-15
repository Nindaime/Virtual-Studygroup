package com.peeplotech.studygroup.models;

public class Question {

    private String content;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String optionE;
    private String correctOption;


    public Question(String content, Options options, String answer) {

        this.content = content;
        optionA = options.getOptionA();
        optionB = options.getOptionB();
        optionC = options.getOptionC();
        optionD = options.getOptionD();
        optionE = options.getOptionE();
        correctOption = answer;

    }

    public String getOptionA() {
        return optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public String getOptionE() {
        return optionE;
    }

    public String getCorrectOption() {
        return correctOption;
    }

    public String getContent() {
        return content;
    }

}
