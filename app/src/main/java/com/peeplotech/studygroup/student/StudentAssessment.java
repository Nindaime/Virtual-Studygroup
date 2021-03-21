package com.peeplotech.studygroup.student;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import com.peeplotech.studygroup.R;
import com.peeplotech.studygroup.adapters.AssessmentAdapter;
import com.peeplotech.studygroup.databases.Database;
import com.peeplotech.studygroup.models.Assessment;
import com.peeplotech.studygroup.models.Course;
import com.peeplotech.studygroup.models.User;
import com.peeplotech.studygroup.util.Common;
import io.paperdb.Paper;

public class StudentAssessment extends AppCompatActivity {

    //widgets
    private ImageView backBtn;
    private TextView courseTitle, countDownTimer;
    private ViewPager2 questionViewPager;

    //data
    private List<Assessment> questionList = new ArrayList<>();

    //values
    private String courseId;
    private User currentUser;
    private Course currentCourse;
    private int passed = 0;
    private int failed = 0;
    private int attempted = 0;
    private int total = 0;
    public int currentPosition = 0;

    //timer
    private CountDownTimer mCountDownTimer;
    private long mTimeLeftInMillis = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_assessment);

        //values
        courseId = getIntent().getStringExtra(Common.INTENT_COURSE);
        currentUser = Paper.book().read(Common.CURRENT_USER);
        currentCourse = new Database(this).getCourseDetails(courseId);

        //widgets
        backBtn = findViewById(R.id.backBtn);
        courseTitle = findViewById(R.id.courseTitle);
        countDownTimer = findViewById(R.id.countDownTimer);
        questionViewPager = findViewById(R.id.questionViewPager);

        //init
        initialize();
    }

    private void initialize() {

        //back
        backBtn.setOnClickListener(v -> onBackPressed());

        //course title
        courseTitle.setText(currentCourse.getCourse_title());

        //init timer
        initTimer();

        //load questions
        loadQuestions();

    }

    private void initTimer() {

        //get sync time
        mTimeLeftInMillis = 20 * 1000;


        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;

                //update text view
                int hours = (int) mTimeLeftInMillis / (1000 * 60 * 60) % 24;
                int minutes = (int) mTimeLeftInMillis / (60 * 1000) % 60;
                int seconds = (int) mTimeLeftInMillis / 1000 % 60;

                String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);

                countDownTimer.setText(timeLeftFormatted);
            }

            @Override
            public void onFinish() {

                mCountDownTimer.cancel();

                //next
                runTestAlgo(false, false, false, currentPosition);
            }
        }.start();

    }

    private void loadQuestions() {

        questionList.addAll(new Database(this).getStudentAssessment(courseId));
        total = questionList.size();

        questionViewPager.setOffscreenPageLimit(1);
        questionViewPager.setAdapter(new AssessmentAdapter(this, this, questionList));
        questionViewPager.setUserInputEnabled(false);

    }

    public void runTestAlgo(boolean isPassed, boolean isFailed, boolean isIgnored, int currentItem){

        if (!isIgnored) {
            attempted++;
        }

        if (isPassed) {
            passed++;
        }

        if (isFailed) {
            failed++;
        }

        if (attempted == total) {
            finishTest();
        } else {
            questionViewPager.setCurrentItem(currentItem, true);

            if (mCountDownTimer != null)
                mCountDownTimer.cancel();

            initTimer();

        }


    }

    public void setCurrentItemPosition(int position){
        currentPosition = position;
    }

    private void finishTest() {

        new Database(this).addAssessmentResult(generateRandomToken(), courseId, currentUser.getUser_id(), String.valueOf(attempted), String.valueOf(passed), String.valueOf(failed), String.valueOf(total));

        if (currentUser.getUser_type().equals(Common.USER_TYPE_STUDENT))
            new Database(this).updateAssessmentEngagement(currentUser.getUser_id());

        //finish
        onBackPressed();

    }

    private String generateRandomToken(){
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 9;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        String generatedString = buffer.toString();
        return generatedString;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_right, R.anim.slide_out_right);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mCountDownTimer != null)
            mCountDownTimer.cancel();

    }

    public void onDestroy() {
        super.onDestroy();

        if (mCountDownTimer != null)
            mCountDownTimer.cancel();
    }

    public void onPause() {
        super.onPause();

        if (mCountDownTimer != null)
            mCountDownTimer.cancel();

    }
}