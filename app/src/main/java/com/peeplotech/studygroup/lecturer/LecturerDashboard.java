package com.peeplotech.studygroup.lecturer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import com.peeplotech.studygroup.Profile;
import com.peeplotech.studygroup.R;
import com.peeplotech.studygroup.adapters.CourseAdapter;
import com.peeplotech.studygroup.databases.Database;
import com.peeplotech.studygroup.models.Course;
import com.peeplotech.studygroup.models.User;
import com.peeplotech.studygroup.util.Common;
import io.paperdb.Paper;

public class LecturerDashboard extends AppCompatActivity {

    //widgets
    private TextView greetUser, userId;
    private RoundedImageView userAvatar;
    private RecyclerView courseRecycler;
    private CardView addCourse;

    //data
    private CourseAdapter adapter;
    private List<Course> courseList = new ArrayList<>();

    //values
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer_dashboard);

        //value
        currentUser = Paper.book().read(Common.CURRENT_USER);

        //widgets
        greetUser = findViewById(R.id.greetUser);
        userId = findViewById(R.id.userId);
        userAvatar = findViewById(R.id.userAvatar);
        courseRecycler = findViewById(R.id.courseRecycler);
        addCourse = findViewById(R.id.addCourse);

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
            startActivity(new Intent(this, AddCourse.class));
            overridePendingTransition(R.anim.slide_left, R.anim.slide_out_left);
        });

    }

    private void initUserProfile() {

        //set data
        greetUser.setText("Welcome " + currentUser.getFirst_name());
        userId.setText("@" + currentUser.getUser_id());


        if (!TextUtils.isEmpty(currentUser.getUser_avatar())){

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

    private void openProfile(){

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
        courseList = new Database(this).getAllCreatedCourses(currentUser.getUser_id());

        //adapter
        adapter = new CourseAdapter(this, this, courseList);
        courseRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
}