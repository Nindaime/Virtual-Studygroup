package esw.peeplotech.virtualstudy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import esw.peeplotech.virtualstudy.lecturer.LecturerDashboard;
import esw.peeplotech.virtualstudy.student.StudentDashboard;
import esw.peeplotech.virtualstudy.util.Common;
import io.paperdb.Paper;

public class Splash extends AppCompatActivity {

    //value
    private String userId, userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //get value
        userId = Paper.book().read(Common.USER_ID);
        userType = Paper.book().read(Common.USER_TYPE);

        //check
        if (userId != null && userType != null){

            //check user type
            if (userType.equals(Common.USER_TYPE_LECTURER)){

                //update ui
                Intent homeIntent = new Intent(this, LecturerDashboard.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(homeIntent);
                finish();
                overridePendingTransition(R.anim.slide_left, R.anim.slide_out_left);

            } else {

                //update ui
                Intent homeIntent = new Intent(this, StudentDashboard.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(homeIntent);
                finish();
                overridePendingTransition(R.anim.slide_left, R.anim.slide_out_left);

            }

        } else {

            //go to on board page
            startActivity(new Intent(this, OnBoardingChoice.class));
            finish();
            overridePendingTransition(R.anim.slide_left, R.anim.slide_out_left);

        }
    }
}