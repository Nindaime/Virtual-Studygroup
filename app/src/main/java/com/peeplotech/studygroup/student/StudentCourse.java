package com.peeplotech.studygroup.student;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.peeplotech.studygroup.R;
import com.peeplotech.studygroup.StudentRecords;
import com.peeplotech.studygroup.adapters.AssignmentAdapter;
import com.peeplotech.studygroup.adapters.ModuleAdapter;
import com.peeplotech.studygroup.databases.Database;
import com.peeplotech.studygroup.models.Assignment;
import com.peeplotech.studygroup.models.Course;
import com.peeplotech.studygroup.models.Module;
import com.peeplotech.studygroup.models.User;
import com.peeplotech.studygroup.util.Common;
import io.paperdb.Paper;

public class StudentCourse extends AppCompatActivity {

    //widgets
    private ImageView backBtn;
    private TextView courseTitle, moduleSummary, assignmentSummary;
    private TextView viewModulesDets, viewAssignmentDets, viewAssessmentDets, viewRecords;

    //value
    private String courseId;
    private User currentUser;
    private Course currentCourse;

    //dialogs
    private AlertDialog moduleListDialog, assignmentListDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_course);

        //values
        courseId = getIntent().getStringExtra(Common.INTENT_COURSE);
        currentUser = Paper.book().read(Common.CURRENT_USER);
        currentCourse = new Database(this).getCourseDetails(courseId);

        //widgets
        backBtn = findViewById(R.id.backBtn);
        courseTitle = findViewById(R.id.courseTitle);
        moduleSummary = findViewById(R.id.moduleSummary);
        assignmentSummary = findViewById(R.id.assignmentSummary);
        viewModulesDets = findViewById(R.id.viewModulesDets);
        viewAssignmentDets = findViewById(R.id.viewAssignmentDets);
        viewAssessmentDets = findViewById(R.id.viewAssessmentDets);
        viewRecords = findViewById(R.id.viewRecords);

        //init
        initialize();

    }

    private void initialize() {

        //back
        backBtn.setOnClickListener(v -> onBackPressed());

        //course title
        courseTitle.setText(currentCourse.getCourse_title());

        //init modules
        initializeModules();

        //init assignments
        initializeAssignments();

        //init assessment
        initializeAssessment();

        //record
        viewRecords.setOnClickListener(v -> {
            startActivity(new Intent(this, StudentRecords.class));
            overridePendingTransition(R.anim.slide_left, R.anim.slide_out_left);
        });

    }

    @SuppressLint("SetTextI18n")
    private void initializeModules() {

        //summary
        int totalModules = new Database(this).getAllModules(courseId).size();
        moduleSummary.setText("Lecturer has only created " + totalModules + " lecture module(s) for this course. Click on details below to explore.");

        //view all modules
        viewModulesDets.setOnClickListener(v -> {
            openModuleListDialog();
        });

    }

    private void openModuleListDialog() {

        moduleListDialog = new AlertDialog.Builder(this, R.style.DialogTheme).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View viewOptions = inflater.inflate(R.layout.show_module_layout,null);

        //widgets
        ImageView backBtn = viewOptions.findViewById(R.id.backBtn);
        RecyclerView listRecycler = viewOptions.findViewById(R.id.listRecycler);

        //add view properties
        moduleListDialog.setView(viewOptions);
        moduleListDialog.getWindow().getAttributes().windowAnimations = R.style.SlideDialogAnimation;
        moduleListDialog.getWindow().setGravity(Gravity.BOTTOM);
        moduleListDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //add windows properties
        WindowManager.LayoutParams layoutParams = moduleListDialog.getWindow().getAttributes();
        moduleListDialog.getWindow().setAttributes(layoutParams);

        //show dialog
        moduleListDialog.show();

        //back
        backBtn.setOnClickListener(v -> moduleListDialog.dismiss());

        //set recycler
        listRecycler.setHasFixedSize(true);
        listRecycler.setLayoutManager(new LinearLayoutManager(this));

        //data
        List<Module> moduleList = new ArrayList<>();
        moduleList.addAll(new Database(this).getAllModules(courseId));
        listRecycler.setAdapter(new ModuleAdapter(this, this, moduleList));

    }

    @SuppressLint("SetTextI18n")
    private void initializeAssignments() {

        //summary
        int totalModules = new Database(this).getAllAssignments(courseId).size();
        assignmentSummary.setText("Lecturer has only created " + totalModules + " assignment(s) for this course. Click on details below to explore.");

        //view all modules
        viewAssignmentDets.setOnClickListener(v -> {
            openAssignmentListDialog();
        });

    }

    private void openAssignmentListDialog() {

        assignmentListDialog = new AlertDialog.Builder(this, R.style.DialogTheme).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View viewOptions = inflater.inflate(R.layout.show_assigments_layout,null);

        //widgets
        ImageView backBtn = viewOptions.findViewById(R.id.backBtn);
        RecyclerView listRecycler = viewOptions.findViewById(R.id.listRecycler);

        //add view properties
        assignmentListDialog.setView(viewOptions);
        assignmentListDialog.getWindow().getAttributes().windowAnimations = R.style.SlideDialogAnimation;
        assignmentListDialog.getWindow().setGravity(Gravity.BOTTOM);
        assignmentListDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //add windows properties
        WindowManager.LayoutParams layoutParams = assignmentListDialog.getWindow().getAttributes();
        assignmentListDialog.getWindow().setAttributes(layoutParams);

        //show dialog
        assignmentListDialog.show();

        //back
        backBtn.setOnClickListener(v -> assignmentListDialog.dismiss());

        //set recycler
        listRecycler.setHasFixedSize(true);
        listRecycler.setLayoutManager(new LinearLayoutManager(this));

        //data
        List<Assignment> assignmentList = new ArrayList<>();
        assignmentList.addAll(new Database(this).getAllAssignments(courseId));
        listRecycler.setAdapter(new AssignmentAdapter(this, this, assignmentList));

    }

    @SuppressLint("SetTextI18n")
    private void initializeAssessment() {

        //summary
        int totalAssessment = new Database(this).getAllAssessment(courseId).size();

        //view all modules
        viewAssessmentDets.setOnClickListener(v -> {

            if (totalAssessment > 0){

                if (!new Database(this).hasStudentSubmitted(currentUser.getUser_id(), courseId)) {

                    Intent assessmentIntent = new Intent(this, StudentAssessment.class);
                    assessmentIntent.putExtra(Common.INTENT_COURSE, courseId);
                    startActivity(assessmentIntent);
                    overridePendingTransition(R.anim.slide_left, R.anim.slide_out_left);
                } else {

                    Toast.makeText(this, "Already taken assessment", Toast.LENGTH_SHORT).show();

                }

            } else {

                Toast.makeText(this, "Your lecturer has not set your assessment questions", Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_right, R.anim.slide_out_right);
    }
}