package com.peeplotech.studygroup;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

public class Questionaire extends AppCompatActivity {

    //button
    private RelativeLayout submitBtn;

    //radio button group
    private RadioGroup radioGroup1;
    private RadioGroup radioGroup2;
    private RadioGroup radioGroup3;
    private RadioGroup radioGroup4;
    private RadioGroup radioGroup5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionaire);
        initialize();
    }

    private  void initialize(){

        submitBtn.setOnClickListener( v->scoreStudent());
    }

    private void scoreStudent(){

        String [] answers = getAnswers();
        String [] selectedAnswer = getSelectedAnswers();
        int score = 0;
        for ( int i= 0; i < answers.length; i++){
            System.out.println("i: "+selectedAnswer[i]);
            if(answers[i].equals(selectedAnswer[i])){ score++;}
        }


        saveRecordToDatabase();
    }

    private String [] getAnswers(){

        String answerOne = getResources().getString(R.string.firstQuestionSecondOption);
        String answerTwo = getResources().getString(R.string.secondQuestionSecondOption);
        String answerThree = getResources().getString(R.string.thirdQuestionSecondOption);
        String answerFour = getResources().getString(R.string.fourthQuestionSecondOption);
        String answerFive = getResources().getString(R.string.fifthQuestionSecondOption);
        String [] answers = {answerOne, answerTwo, answerThree, answerFour, answerFive};
        return answers;
    }


    private String [] getSelectedAnswers(){

        String answerOne = getSelectedText(radioGroup1);
        String answerTwo = getSelectedText(radioGroup2);
        String answerThree = getSelectedText(radioGroup3);
        String answerFour = getSelectedText(radioGroup4);
        String answerFive = getSelectedText(radioGroup5);
        String [] answers = {answerOne, answerTwo, answerThree, answerFour, answerFive};
        return answers;
    }

    private String getSelectedText(RadioGroup radioGroup){
        int selectedItemId = radioGroup.getCheckedRadioButtonId();
        return (String) ((RadioButton)findViewById(selectedItemId)).getText();
    }

    private void saveRecordToDatabase(){}
}