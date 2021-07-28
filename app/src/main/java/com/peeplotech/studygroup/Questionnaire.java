package com.peeplotech.studygroup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.peeplotech.studygroup.databases.Database;
import com.peeplotech.studygroup.student.StudentDashboard;

public class Questionnaire extends AppCompatActivity {

    //button
    private RelativeLayout submitBtn;

    //radio button group
    private RadioGroup radioGroup1;
    private RadioGroup radioGroup2;
    private RadioGroup radioGroup3;
    private RadioGroup radioGroup4;
    private RadioGroup radioGroup5;

    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

        System.out.println("User id: " + userId);
        submitBtn = findViewById(R.id.submitBtn);
        radioGroup1 = findViewById(R.id.radioGroup1);
        radioGroup2 = findViewById(R.id.radioGroup2);
        radioGroup3 = findViewById(R.id.radioGroup3);
        radioGroup4 = findViewById(R.id.radioGroup4);
        radioGroup5 = findViewById(R.id.radioGroup5);
        initialize();
    }

    private void initialize() {

        submitBtn.setOnClickListener(v -> scoreStudent());
    }

    private void scoreStudent() {

        String[] answers = getAnswers();
        String[] selectedAnswer = getSelectedAnswers();
        float score = 0;
        for (int i = 0; i < answers.length; i++) {
            System.out.println("i: " + selectedAnswer[i]);
            if (answers[i].equals(selectedAnswer[i])) {
                score++;
            }
        }


        saveRecordToDatabase(userId, score);
        navigateToStudentDashboard(score);
    }

    private String[] getAnswers() {

        String answerOne = getResources().getString(R.string.firstQuestionSecondOption);
        String answerTwo = getResources().getString(R.string.secondQuestionSecondOption);
        String answerThree = getResources().getString(R.string.thirdQuestionSecondOption);
        String answerFour = getResources().getString(R.string.fourthQuestionSecondOption);
        String answerFive = getResources().getString(R.string.fifthQuestionSecondOption);
        String[] answers = {answerOne, answerTwo, answerThree, answerFour, answerFive};
        return answers;
    }


    private String[] getSelectedAnswers() {

        String answerOne = getSelectedText(radioGroup1);
        String answerTwo = getSelectedText(radioGroup2);
        String answerThree = getSelectedText(radioGroup3);
        String answerFour = getSelectedText(radioGroup4);
        String answerFive = getSelectedText(radioGroup5);
        String[] answers = {answerOne, answerTwo, answerThree, answerFour, answerFive};
        return answers;
    }

    private String getSelectedText(RadioGroup radioGroup) {
        int selectedItemId = radioGroup.getCheckedRadioButtonId();
        return (String) ((RadioButton) findViewById(selectedItemId)).getText();
    }

    private void saveRecordToDatabase(String userIdentity, float dyslexicScore) {

        new Database(this).updateUserDyslexicDetail(userIdentity, dyslexicScore);

    }

    public void navigateToStudentDashboard(float score) {
        Intent homeIntent = new Intent(this, StudentDashboard.class);
        homeIntent.putExtra("score", score);
        homeIntent.putExtra("newUser",true);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);
        finish();
        overridePendingTransition(R.anim.slide_left, R.anim.slide_out_left);
    }

}
