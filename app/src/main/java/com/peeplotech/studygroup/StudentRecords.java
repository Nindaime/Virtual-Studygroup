package com.peeplotech.studygroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import com.peeplotech.studygroup.adapters.RecordAdapter;
import com.peeplotech.studygroup.databases.Database;
import com.peeplotech.studygroup.models.Analytics;
import com.peeplotech.studygroup.models.AssessmentResult;
import com.peeplotech.studygroup.models.User;
import com.peeplotech.studygroup.util.Common;
import io.paperdb.Paper;

public class StudentRecords extends AppCompatActivity {

    //widgets
    private ImageView backBtn;
    private TextView userId;
    private RoundedImageView userAvatar;
    private TextView userName, registeredCourses, groupContributions, submittedAssignments, assignmentsIgnored, assessmentsTaken, modulesViews;
    private RecyclerView listRecycler;

    //data
    private List<AssessmentResult> resultList = new ArrayList<>();
    private RecordAdapter adapter;

    //values
    private User currentUser;
    private Analytics currentAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_record);

        //currentUser
        currentUser = Paper.book().read(Common.CURRENT_USER);
        currentAnalytics = new Database(this).getStudentAnalytics(currentUser.getUser_id());

        //widgets
        backBtn = findViewById(R.id.backBtn);
        userId = findViewById(R.id.userId);
        userAvatar = findViewById(R.id.userAvatar);
        userName = findViewById(R.id.userName);
        registeredCourses = findViewById(R.id.registeredCourses);
        groupContributions = findViewById(R.id.groupContributions);
        submittedAssignments = findViewById(R.id.submittedAssignments);
        assignmentsIgnored = findViewById(R.id.assignmentsIgnored);
        assessmentsTaken = findViewById(R.id.assessmentsTaken);
        modulesViews = findViewById(R.id.modulesViews);
        listRecycler = findViewById(R.id.listRecycler);

        //init
        initialize();
    }

    private void initialize() {

        //back
        backBtn.setOnClickListener(v -> onBackPressed());

        //user id
        userId.setText(currentUser.getUser_id());

        //avatar
        if (!currentUser.getUser_avatar().isEmpty()){
            Picasso.get()
                    .load(Uri.parse(currentUser.getUser_avatar()))
                    .into(userAvatar);
        }

        //name
        userName.setText(currentUser.getFirst_name() + " " + currentUser.getLast_name());

        registeredCourses.setText("Courses Registered: " + currentAnalytics.getCourses_registered());
        groupContributions.setText("Contributed in group chat " + currentAnalytics.getGroup_contributions() + " time(s)");
        submittedAssignments.setText("Submitted " + currentAnalytics.getAssignments_submitted() + " assignment(s)");
        assignmentsIgnored.setText("Assignments received: " + currentAnalytics.getAssignment_viewed());
        assessmentsTaken.setText("Taken " + currentAnalytics.getAssessments_taken() + " assessment(s) so far");
        modulesViews.setText("Module interaction: " + currentAnalytics.getModules_completed());

        //load results
        loadResults();

    }

    private void loadResults() {

        listRecycler.setHasFixedSize(true);
        listRecycler.setLayoutManager(new LinearLayoutManager(this));

        resultList.addAll(new Database(this).getMyResults(currentUser.getUser_id()));

        adapter = new RecordAdapter(this, this, resultList);
        listRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_right, R.anim.slide_out_right);
    }
}