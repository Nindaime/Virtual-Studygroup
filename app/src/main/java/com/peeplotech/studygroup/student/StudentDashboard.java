package com.peeplotech.studygroup.student;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import com.peeplotech.studygroup.Profile;
import com.peeplotech.studygroup.R;
import com.peeplotech.studygroup.adapters.SubscribedCourseAdapter;
import com.peeplotech.studygroup.databases.Database;
import com.peeplotech.studygroup.models.SubscribedCourse;
import com.peeplotech.studygroup.models.User;
import com.peeplotech.studygroup.util.Common;

import io.paperdb.Paper;

public class StudentDashboard extends AppCompatActivity {

    //widgets
    private TextView greetUser, userId;
    private RoundedImageView userAvatar;
    private RecyclerView courseRecycler;
    private CardView addCourse;
    private TextView dyslexicScore;

    //data
    private SubscribedCourseAdapter adapter;
    private List<SubscribedCourse> courseList = new ArrayList<>();

    //values
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);
        Intent intent = getIntent();
        Boolean isNewUser = intent.getBooleanExtra("newUser", false);


        //value
        currentUser = Paper.book().read(Common.CURRENT_USER);

        //widgets
        greetUser = findViewById(R.id.greetUser);
        userId = findViewById(R.id.userId);
        userAvatar = findViewById(R.id.userAvatar);
        courseRecycler = findViewById(R.id.courseRecycler);
        addCourse = findViewById(R.id.addCourse);
        dyslexicScore = findViewById(R.id.dyslexicScore);

        if (isNewUser == true) {
            dyslexicScore.setText(String.valueOf(intent.getFloatExtra("score", 0)));
            dyslexicScore.setVisibility(View.VISIBLE);
        }

        //init
        initialize();
    }

    private void initialize() {

        //user profile
        initUserProfile();

        //load courses
        loadCourses();

        //add course
        addCourse.setOnClickListener(v -> {
            startActivity(new Intent(this, SubscribeToCourse.class));
            overridePendingTransition(R.anim.slide_left, R.anim.slide_out_left);
        });

    }

    private void initUserProfile() {

        //set data
        greetUser.setText("Welcome " + currentUser.getFirst_name());
        userId.setText("@" + currentUser.getUser_id());


        if (!TextUtils.isEmpty(currentUser.getUser_avatar())) {

            Picasso.get()
                    .load(Uri.parse(currentUser.getUser_avatar()))
                    .config(Bitmap.Config.RGB_565)
                    .fit().centerCrop()
                    .into(userAvatar);

        }

        //click
        greetUser.setOnClickListener(v -> openProfile());
        userAvatar.setOnClickListener(v -> openProfile());


    }

    private void openProfile() {

        startActivity(new Intent(this, Profile.class));
        overridePendingTransition(R.anim.slide_left, R.anim.slide_out_left);

    }

    @Override
    protected void onResume() {
        super.onResume();
        currentUser = Paper.book().read(Common.CURRENT_USER);
        initUserProfile();
        loadCourses();
    }

    private void loadCourses() {

        //clear list
        courseList.clear();

        //init recycler
        courseRecycler.setHasFixedSize(true);
        courseRecycler.setLayoutManager(new LinearLayoutManager(this));

        //init list
        courseList = new Database(this).getAllSubscribedCourses(currentUser.getUser_id());

        //adapter
        adapter = new SubscribedCourseAdapter(this, this, courseList);
        courseRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
}