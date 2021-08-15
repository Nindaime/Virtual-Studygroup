package com.peeplotech.studygroup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.peeplotech.studygroup.databases.Database;
import com.peeplotech.studygroup.models.Options;
import com.peeplotech.studygroup.student.StudentDashboard;
import com.peeplotech.studygroup.models.Question;
import com.peeplotech.studygroup.util.AppPreference;

import java.util.ArrayList;
import java.util.HashMap;

public class Questionnaire extends AppCompatActivity {

    //button
    private RelativeLayout submitBtn;
    private RelativeLayout prevBtn;
    private RelativeLayout nextBtn;

    //radio button group
    private RadioGroup radioGroup;

    //radio button
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private RadioButton radioButton3;
    private RadioButton radioButton4;
    private RadioButton radioButton5;
    private TextView questionText;

    private String userId;
    private int currentIndex = 0;
    private static boolean onScreenChange = false;

    private ArrayList<Question> questions = new ArrayList<>();
    private HashMap<Integer, String> selectedAnswers = new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

        System.out.println("User id: " + userId);
        questionText = findViewById(R.id.question);
        submitBtn = findViewById(R.id.submitBtn);
        prevBtn = findViewById(R.id.prevBtn);
        nextBtn = findViewById(R.id.nextBtn);
        radioGroup = findViewById(R.id.radioGroup);
        radioButton1 = findViewById(R.id.radioButton1);
        radioButton2 = findViewById(R.id.radioButton2);
        radioButton3 = findViewById(R.id.radioButton3);
        radioButton4 = findViewById(R.id.radioButton4);
        radioButton5 = findViewById(R.id.radioButton5);
        initializeQuestions();
        initialize();


        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // This will get the radiobutton that has changed in its check state
            RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);


            if (checkedRadioButton == null) {
                return;
            }
            String selectedText = selectedAnswers.get(currentIndex);

            boolean isChecked = checkedRadioButton.isChecked();


            if (!onScreenChange) {


                checkedRadioButton.setChecked(true);

                selectedAnswers.put(currentIndex, ""+checkedRadioButton.getText());
                onScreenChange = false;
            }




        });
    }

    private void initializeQuestions() {

        Question firstQuestion = getFirstQuestion();
        Question secondQuestion = getSecondQuestion();
        Question thirdQuestion = getThirdQuestion();
        Question fourthQuestion = getFourthQuestion();
        Question fifthQuestion = getFifthQuestion();

        questions.add(firstQuestion);
        questions.add(secondQuestion);
        questions.add(thirdQuestion);
        questions.add(fourthQuestion);
        questions.add(fifthQuestion);
    }

    public Question getFirstQuestion() {
        Options options = new Options(
                getResources().getString(R.string.firstQuestionFirstOption),
                getResources().getString(R.string.firstQuestionSecondOption),
                getResources().getString(R.string.firstQuestionThirdOption),
                getResources().getString(R.string.firstQuestionFourthOption),
                getResources().getString(R.string.firstQuestionFifthOption)
        );

        return new Question(getResources().getString(R.string.firstQuestion), options, getResources().getString(R.string.firstQuestionFirstOption));

    }

    public Question getSecondQuestion() {
        Options options = new Options(
                getResources().getString(R.string.secondQuestionFirstOption),
                getResources().getString(R.string.secondQuestionSecondOption),
                getResources().getString(R.string.secondQuestionThirdOption),
                getResources().getString(R.string.secondQuestionFourthOption),
                getResources().getString(R.string.secondQuestionFifthOption)
        );

        return new Question(getResources().getString(R.string.secondQuestion), options, getResources().getString(R.string.secondQuestionFirstOption));

    }

    public Question getThirdQuestion() {
        Options options = new Options(
                getResources().getString(R.string.thirdQuestionFirstOption),
                getResources().getString(R.string.thirdQuestionSecondOption),
                getResources().getString(R.string.thirdQuestionThirdOption),
                getResources().getString(R.string.thirdQuestionFourthOption),
                getResources().getString(R.string.thirdQuestionFifthOption)
        );

        return new Question(getResources().getString(R.string.thirdQuestion), options, getResources().getString(R.string.thirdQuestionFirstOption));

    }

    public Question getFourthQuestion() {
        Options options = new Options(
                getResources().getString(R.string.fourthQuestionFirstOption),
                getResources().getString(R.string.fourthQuestionSecondOption),
                getResources().getString(R.string.fourthQuestionThirdOption),
                getResources().getString(R.string.fourthQuestionFourthOption),
                getResources().getString(R.string.fourthQuestionFifthOption)
        );

        return new Question(getResources().getString(R.string.fourthQuestion), options, getResources().getString(R.string.fourthQuestionFirstOption));

    }

    public Question getFifthQuestion() {
        Options options = new Options(
                getResources().getString(R.string.fifthQuestionFirstOption),
                getResources().getString(R.string.fifthQuestionSecondOption),
                getResources().getString(R.string.fifthQuestionThirdOption),
                getResources().getString(R.string.fifthQuestionFourthOption),
                getResources().getString(R.string.fifthQuestionFifthOption)
        );

        return new Question(getResources().getString(R.string.fifthQuestion), options, getResources().getString(R.string.fifthQuestionFirstOption));

    }


    private void initialize() {

        submitBtn.setOnClickListener(v -> scoreStudent());
        prevBtn.setOnClickListener(v -> setPreviousPage());
        nextBtn.setOnClickListener(v -> setNextPage());
    }

    private void scoreStudent() {

        if(selectedAnswers.size() < questions.size()){
            //TODO: propmt user
            Toast.makeText(getApplicationContext(), "Oops!!! Unanswered questions.", Toast.LENGTH_SHORT).show();

            return;
        }

        float score = 0;
        for (int i = 0; i < questions.size(); i++) {

            if (selectedAnswers.get(i).equals(questions.get(i).getCorrectOption())) {
                score++;
            }
        }



        saveRecordToDatabase(userId, score);
        saveUserDyslexicStateToSharedPreference(userId, score);
        navigateToStudentDashboard(score);
    }

    private void setNextPage() {

        if (++currentIndex >= questions.size() - 1) {
            currentIndex = questions.size() - 1;
            nextBtn.setClickable(false);

        }

        prevBtn.setClickable(true);


        Question question = questions.get(currentIndex);


        onScreenChange = true;
        setScreenDetail(question);

    }

    private void setScreenDetail(Question question) {


        questionText.setText(question.getContent());
        radioButton1.setText(question.getOptionA());
        radioButton2.setText(question.getOptionB());
        radioButton3.setText(question.getOptionC());
        radioButton4.setText(question.getOptionD());
        radioButton5.setText(question.getOptionE());


        try {
            String selectedText = selectedAnswers.get(currentIndex);



            if (selectedText != null) {
                onScreenChange = false;

                if (selectedText.equals(question.getOptionA())) {


                    radioButton1.setChecked(true);

                } else if (selectedText.equals(question.getOptionB())) {

                    radioButton2.setChecked(true);
                } else if (selectedText.equals(question.getOptionC())) {
                    radioButton3.setChecked(true);
                } else if (selectedText.equals(question.getOptionD())) {
                    radioButton4.setChecked(true);
                } else if (selectedText.equals(question.getOptionE())) {
                    radioButton5.setChecked(true);
                }

                onScreenChange = true;
            } else {


                radioButton1.setChecked(false);
                radioButton2.setChecked(false);
                radioButton3.setChecked(false);
                radioButton4.setChecked(false);
                radioButton5.setChecked(false);


            }


        } catch (Exception ex) {

            radioGroup.clearCheck();
        }


        if (currentIndex < 0) {

        }

        onScreenChange = false;
    }

    private void setPreviousPage() {



        if (currentIndex <= 0) {
            currentIndex = 0;
            prevBtn.setClickable(false);

        } else {
            Question question = questions.get(--currentIndex);
            onScreenChange = true;
            setScreenDetail(question);
        }

        nextBtn.setClickable(true);


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


    private String getSelectedText(RadioGroup radioGroup) {
        int selectedItemId = radioGroup.getCheckedRadioButtonId();
        return (String) ((RadioButton) findViewById(selectedItemId)).getText();
    }

    private void saveRecordToDatabase(String userIdentity, float dyslexicScore) {

        new Database(this).updateUserDyslexicDetail(userIdentity, dyslexicScore);

    }


    public void saveUserDyslexicStateToSharedPreference(String userIdentity, float dyslexicScore) {
        AppPreference preference = new AppPreference(this);

        preference.setUserDylexicScore(userIdentity, dyslexicScore);
    }

    public void navigateToStudentDashboard(float score) {
        Intent homeIntent = new Intent(this, StudentDashboard.class);
        homeIntent.putExtra("score", score);
        homeIntent.putExtra("newUser", true);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);
        finish();
        overridePendingTransition(R.anim.slide_left, R.anim.slide_out_left);
    }

}
