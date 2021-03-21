package com.peeplotech.studygroup.lecturer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVReader;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.peeplotech.studygroup.R;
import com.peeplotech.studygroup.adapters.AssignmentAdapter;
import com.peeplotech.studygroup.adapters.ModuleAdapter;
import com.peeplotech.studygroup.adapters.ResultAdapter;
import com.peeplotech.studygroup.databases.Database;
import com.peeplotech.studygroup.models.AssessmentResult;
import com.peeplotech.studygroup.models.Assignment;
import com.peeplotech.studygroup.models.Course;
import com.peeplotech.studygroup.models.Module;
import com.peeplotech.studygroup.models.User;
import com.peeplotech.studygroup.util.Common;
import io.paperdb.Paper;

public class LecturerCourse extends AppCompatActivity {

    //widgets
    private ImageView backBtn;
    private TextView courseTitle, moduleSummary, assignmentSummary, assessmentSummary;
    private TextView viewModulesDets, viewAssignmentDets, viewResultsDets;
    private CardView addModule, addAssignment, addAssessment;

    //value
    private String courseId;
    private User currentUser;
    private Course currentCourse;
    private String idToken = "";

    //image upload
    private static final int VERIFY_PERMISSIONS_REQUEST = 757;
    private static final int MODULE_REQUEST_CODE = 665;
    private static final int ASSESSMENT_REQUEST_CODE = 666;
    private static final int ASSIGNMENT_REQUEST_CODE = 667;
    private Uri moduleUri, assignmentUri, assessmentUri;
    private String moduleUrl = "";
    private String assignmentUrl = "";
    private String assessmentUrl = "";

    //dialogs
    private AlertDialog addModuleDialog, listModulesDialog;
    private AlertDialog addAssignmentDialog, listAssignmentsDialog;
    private AlertDialog addAssessmentDialog, listResultsDialog;

    //module dialog
    private ImageView fileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer_course);

        //values
        courseId = getIntent().getStringExtra(Common.INTENT_COURSE);
        currentUser = Paper.book().read(Common.CURRENT_USER);
        currentCourse = new Database(this).getCourseDetails(courseId);

        //widgets
        backBtn = findViewById(R.id.backBtn);
        courseTitle = findViewById(R.id.courseTitle);
        moduleSummary = findViewById(R.id.moduleSummary);
        assignmentSummary = findViewById(R.id.assignmentSummary);
        assessmentSummary = findViewById(R.id.assessmentSummary);
        viewModulesDets = findViewById(R.id.viewModulesDets);
        viewAssignmentDets = findViewById(R.id.viewAssignmentDets);
        addModule = findViewById(R.id.addModule);
        addAssignment = findViewById(R.id.addAssignment);
        addAssessment = findViewById(R.id.addAssessment);
        viewResultsDets = findViewById(R.id.viewResultsDets);

        //init
        initialize();

    }

    private void initialize() {

        //request permission
        requestPermission();

        //back
        backBtn.setOnClickListener(v -> onBackPressed());

        //course title
        courseTitle.setText(currentCourse.getCourse_title());

        //init modules
        initializeModules();

        //init assignments
        initializeAssignments();

        //initialize assessment
        initializeAssessment();

    }

    private void requestPermission() {

        //check permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, VERIFY_PERMISSIONS_REQUEST);

                }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == VERIFY_PERMISSIONS_REQUEST) {
            if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();

            }
        }

    }

    @SuppressLint("SetTextI18n")
    private void initializeModules() {

        //summary
        int totalModules = new Database(this).getAllModules(courseId).size();
        moduleSummary.setText("You have currently created only " + totalModules + " lecture module(s) for this course. Click on details below to explore.");

        //add module
        addModule.setOnClickListener(v -> {
            openAddModuleDialog();
        });

        //view all modules
        viewModulesDets.setOnClickListener(v -> {
            openModuleListDialog();
        });

    }

    private void openAddModuleDialog() {

        addModuleDialog = new AlertDialog.Builder(this, R.style.DialogTheme).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View viewOptions = inflater.inflate(R.layout.add_module_layout,null);

        //widgets
        EditText moduleTitle = viewOptions.findViewById(R.id.moduleTitle);
        EditText moduleDescription = viewOptions.findViewById(R.id.moduleDescription);
        fileImage = viewOptions.findViewById(R.id.fileImage);
        RelativeLayout addBtn = viewOptions.findViewById(R.id.addBtn);
        TextView addText = viewOptions.findViewById(R.id.addText);
        AVLoadingIndicatorView addProgress = viewOptions.findViewById(R.id.addProgress);

        //add view properties
        addModuleDialog.setView(viewOptions);
        addModuleDialog.getWindow().getAttributes().windowAnimations = R.style.SlideDialogAnimation;
        addModuleDialog.getWindow().setGravity(Gravity.BOTTOM);
        addModuleDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //add windows properties
        WindowManager.LayoutParams layoutParams = addModuleDialog.getWindow().getAttributes();
        addModuleDialog.getWindow().setAttributes(layoutParams);

        //show dialog
        addModuleDialog.show();

        //set token
        generateModuleToken();

        //select video
        fileImage.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent , MODULE_REQUEST_CODE);

        });

        //add
        addBtn.setOnClickListener(v -> {

            //validate
            if (TextUtils.isEmpty(moduleTitle.getText().toString().trim())){

                moduleTitle.requestFocus();
                moduleTitle.setError("Required");

            } else

            if (TextUtils.isEmpty(moduleDescription.getText().toString().trim())){

                moduleDescription.requestFocus();
                moduleDescription.setError("Required");

            } else

            if (moduleUri == null){

                Toast.makeText(this, "Select module video", Toast.LENGTH_SHORT).show();

            } else {

                //start loading
                addBtn.setEnabled(false);
                moduleTitle.setEnabled(false);
                moduleDescription.setEnabled(false);
                fileImage.setEnabled(false);
                addText.setVisibility(View.GONE);
                addProgress.setVisibility(View.VISIBLE);

                //avatar folder
                File moduleFolder = new File(Environment.getExternalStorageDirectory(), Common.BASE_FOLDER + "/" + Common.MODULE_FOLDER);
                if (!moduleFolder.exists()){
                    moduleFolder.mkdir();
                }

                //module file
                File thumbFile = new File(moduleFolder.getAbsolutePath(), idToken + ".mp4");



                //copy file
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = new FileInputStream(getPath(moduleUri));
                    out = new FileOutputStream(thumbFile.getAbsolutePath());

                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }
                    in.close();
                    in = null;

                    // write the output file (You have now copied the file)
                    out.flush();
                    out.close();
                    out = null;

                } catch (FileNotFoundException fileError) {
                    Log.d("FileError", "File Error: " + fileError.getMessage());
                } catch (Exception e) {
                    Log.d("FileError", "Process Error: " + e.getMessage());
                }


                //create file uri
                Uri uri = Uri.fromFile(new File(thumbFile.getAbsolutePath()));
                moduleUrl = uri.toString();

                //extract strings
                String theTitle = moduleTitle.getText().toString().trim();
                String theDesc = moduleDescription.getText().toString().trim();

                //create new module
                new Database(this).createNewModule(idToken, courseId, theTitle, theDesc, "", moduleUrl);

                //create chat room for module
                String chatTable = idToken + Common.MODULE_CHAT;
                new Database(this).createChatTable(chatTable);

                //clean action
                moduleUri = null;
                moduleUrl = "";
                idToken = "";

                //refresh
                initializeModules();

                //clean
                addModuleDialog.dismiss();

            }

        });

    }

    private void openModuleListDialog() {

        listModulesDialog = new AlertDialog.Builder(this, R.style.DialogTheme).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View viewOptions = inflater.inflate(R.layout.show_module_layout,null);

        //widgets
        ImageView backBtn = viewOptions.findViewById(R.id.backBtn);
        RecyclerView listRecycler = viewOptions.findViewById(R.id.listRecycler);

        //add view properties
        listModulesDialog.setView(viewOptions);
        listModulesDialog.getWindow().getAttributes().windowAnimations = R.style.SlideDialogAnimation;
        listModulesDialog.getWindow().setGravity(Gravity.BOTTOM);
        listModulesDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //add windows properties
        WindowManager.LayoutParams layoutParams = listModulesDialog.getWindow().getAttributes();
        listModulesDialog.getWindow().setAttributes(layoutParams);

        //show dialog
        listModulesDialog.show();

        //back
        backBtn.setOnClickListener(v -> listModulesDialog.dismiss());

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
        assignmentSummary.setText("You have added only " + totalModules + " assignment(s) to this course. Click on details below to explore.");

        //add module
        addAssignment.setOnClickListener(v -> {
            openAddAssignmentDialog();
        });

        //view all modules
        viewAssignmentDets.setOnClickListener(v -> {
            openAssignmentListDialog();
        });

    }

    private void openAddAssignmentDialog() {

        addAssignmentDialog = new AlertDialog.Builder(this, R.style.DialogTheme).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View viewOptions = inflater.inflate(R.layout.add_assignment_layout,null);

        //widgets
        EditText assignmentTitle = viewOptions.findViewById(R.id.assignmentTitle);
        EditText assignmentScore = viewOptions.findViewById(R.id.assignmentScore);
        EditText assignmentDeadline = viewOptions.findViewById(R.id.assignmentDeadline);
        fileImage = viewOptions.findViewById(R.id.fileImage);
        RelativeLayout addBtn = viewOptions.findViewById(R.id.addBtn);
        TextView addText = viewOptions.findViewById(R.id.addText);
        AVLoadingIndicatorView addProgress = viewOptions.findViewById(R.id.addProgress);

        //add view properties
        addAssignmentDialog.setView(viewOptions);
        addAssignmentDialog.getWindow().getAttributes().windowAnimations = R.style.SlideDialogAnimation;
        addAssignmentDialog.getWindow().setGravity(Gravity.BOTTOM);
        addAssignmentDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //add windows properties
        WindowManager.LayoutParams layoutParams = addAssignmentDialog.getWindow().getAttributes();
        addAssignmentDialog.getWindow().setAttributes(layoutParams);

        //show dialog
        addAssignmentDialog.show();

        //set token
        generateAssignmentToken();

        //select video
        fileImage.setOnClickListener(v -> {

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

            try {
                startActivityForResult(Intent.createChooser(intent, "Select Your .pdf File"), ASSIGNMENT_REQUEST_CODE);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "Please Install a File Manager",Toast.LENGTH_SHORT).show();
            }

        });

        //add
        addBtn.setOnClickListener(v -> {

            //validate
            if (TextUtils.isEmpty(assignmentTitle.getText().toString().trim())){

                assignmentTitle.requestFocus();
                assignmentTitle.setError("Required");

            } else

            if (TextUtils.isEmpty(assignmentScore.getText().toString().trim())){

                assignmentScore.requestFocus();
                assignmentScore.setError("Required");

            } else

            if (TextUtils.isEmpty(assignmentDeadline.getText().toString().trim())){

                assignmentDeadline.requestFocus();
                assignmentDeadline.setError("Required");

            } else

            if (assignmentUri == null){

                Toast.makeText(this, "Select assignment file", Toast.LENGTH_SHORT).show();

            } else {

                //start loading
                addBtn.setEnabled(false);
                assignmentTitle.setEnabled(false);
                assignmentScore.setEnabled(false);
                assignmentDeadline.setEnabled(false);
                fileImage.setEnabled(false);
                addText.setVisibility(View.GONE);
                addProgress.setVisibility(View.VISIBLE);

                //avatar folder
                File assignmentFolder = new File(Environment.getExternalStorageDirectory(), Common.BASE_FOLDER + "/" + Common.ASSIGNMENT_FOLDER);
                if (!assignmentFolder.exists()){
                    assignmentFolder.mkdir();
                }

                //module file
                File thumbFile = new File(assignmentFolder.getAbsolutePath(), idToken + ".pdf");

                File file = new File(assignmentUri.getPath());//create path from uri
                final String[] split = file.getPath().split(":");//split the path.
                Log.d("FileError", "File Url: " + Environment.getExternalStorageDirectory() + "/" + split[1]);

                String theFileUrl = null;
                if (assignmentUri.getPath().contains("home:")){
                    theFileUrl = Environment.getExternalStorageDirectory() + "/Documents/" + split[1];
                } else {
                    theFileUrl = Environment.getExternalStorageDirectory() + "/" + split[1];
                }


                //copy file
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = new FileInputStream(theFileUrl);
                    out = new FileOutputStream(thumbFile.getAbsolutePath());

                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }
                    in.close();
                    in = null;

                    // write the output file (You have now copied the file)
                    out.flush();
                    out.close();
                    out = null;

                } catch (FileNotFoundException fileError) {
                    Log.d("FileError", "File Error: " + fileError.getMessage());
                } catch (Exception e) {
                    Log.d("FileError", "Process Error: " + e.getMessage());
                }


                //create file uri
                Uri uri = Uri.fromFile(new File(thumbFile.getAbsolutePath()));
                assignmentUrl = uri.toString();

                //extract strings
                String theTitle = assignmentTitle.getText().toString().trim();
                String theScore = assignmentScore.getText().toString().trim();
                String theDeadline = assignmentDeadline.getText().toString().trim();

                //create new module
                new Database(this).createNewAssignment(idToken, theTitle, theScore, theDeadline, "", assignmentUrl, courseId);

                //create chat room for assignment
                String chatTable = idToken + Common.ASSIGNMENT_CHAT;
                new Database(this).createChatTable(chatTable);

                //create chat room for assignment
                String assignmentSubmissionTable = idToken + Common.SUBMITTED_ASSIGNMENT;
                new Database(this).createAssignmentSubmissionTable(assignmentSubmissionTable);

                //clean action
                assignmentUri = null;
                assignmentUrl = "";
                idToken = "";

                //refresh
                initializeAssignments();

                //clean
                addAssignmentDialog.dismiss();

            }

        });

    }

    private void openAssignmentListDialog() {

        listAssignmentsDialog = new AlertDialog.Builder(this, R.style.DialogTheme).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View viewOptions = inflater.inflate(R.layout.show_assigments_layout,null);

        //widgets
        ImageView backBtn = viewOptions.findViewById(R.id.backBtn);
        RecyclerView listRecycler = viewOptions.findViewById(R.id.listRecycler);

        //add view properties
        listAssignmentsDialog.setView(viewOptions);
        listAssignmentsDialog.getWindow().getAttributes().windowAnimations = R.style.SlideDialogAnimation;
        listAssignmentsDialog.getWindow().setGravity(Gravity.BOTTOM);
        listAssignmentsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //add windows properties
        WindowManager.LayoutParams layoutParams = listAssignmentsDialog.getWindow().getAttributes();
        listAssignmentsDialog.getWindow().setAttributes(layoutParams);

        //show dialog
        listAssignmentsDialog.show();

        //back
        backBtn.setOnClickListener(v -> listAssignmentsDialog.dismiss());

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
        int totalQuestions = new Database(this).getAllAssessment(courseId).size();
        assessmentSummary.setText("You have set a total of " + totalQuestions + " question(s) for this course assessment. Click on the button below to add assessment file.");

        //add questions
        addAssessment.setOnClickListener(v -> {
            openAddAssessmentDialog();
        });

        //view results
        viewResultsDets.setOnClickListener(v -> {
            openResultListDialog();
        });

    }

    private void openAddAssessmentDialog() {

        addAssessmentDialog = new AlertDialog.Builder(this, R.style.DialogTheme).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View viewOptions = inflater.inflate(R.layout.add_assessment_layout,null);

        //widgets
        fileImage = viewOptions.findViewById(R.id.fileImage);
        RelativeLayout addBtn = viewOptions.findViewById(R.id.addBtn);
        TextView addText = viewOptions.findViewById(R.id.addText);
        AVLoadingIndicatorView addProgress = viewOptions.findViewById(R.id.addProgress);

        //add view properties
        addAssessmentDialog.setView(viewOptions);
        addAssessmentDialog.getWindow().getAttributes().windowAnimations = R.style.SlideDialogAnimation;
        addAssessmentDialog.getWindow().setGravity(Gravity.BOTTOM);
        addAssessmentDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //add windows properties
        WindowManager.LayoutParams layoutParams = addAssessmentDialog.getWindow().getAttributes();
        addAssessmentDialog.getWindow().setAttributes(layoutParams);

        //show dialog
        addAssessmentDialog.show();

        //set token
        generateAssignmentToken();

        //select video
        fileImage.setOnClickListener(v -> {

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

            try {
                startActivityForResult(Intent.createChooser(intent, "Select Your .csv File"), ASSESSMENT_REQUEST_CODE);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "Please Install a File Manager",Toast.LENGTH_SHORT).show();
            }

        });

        //add
        addBtn.setOnClickListener(v -> {

            //validate
            if (assessmentUri == null){

                Toast.makeText(this, "Select assessment file", Toast.LENGTH_SHORT).show();

            } else {

                //start loading
                addBtn.setEnabled(false);
                fileImage.setEnabled(false);
                addText.setVisibility(View.GONE);
                addProgress.setVisibility(View.VISIBLE);

                File file = new File(assessmentUri.getPath());//create path from uri
                final String[] split = file.getPath().split(":");//split the path.
                Log.d("FileError", "File Url: " + Environment.getExternalStorageDirectory() + "/" + split[1]);

                if (split[1].endsWith(".csv")) {

                    String theFileUrl = null;
                    if (assessmentUri.getPath().contains("home:")) {
                        theFileUrl = Environment.getExternalStorageDirectory() + "/Documents/" + split[1];
                    } else {
                        theFileUrl = Environment.getExternalStorageDirectory() + "/" + split[1];
                    }


                    //read csv
                    try {
                        CSVReader reader = new CSVReader(new FileReader(theFileUrl));
                        String[] nextLine;
                        int count = 0;
                        reader.readNext();

                        while ((nextLine = reader.readNext()) != null) {
                            // nextLine[] is an array of values from the line
                            count++;

                            if (nextLine.length == 7) {
                                String assessmentId = generateRandomToken();

                                if (!new Database(this).isQuestionAlreadySet(courseId, nextLine[0])) {

                                    new Database(this).addNewAssessmentQuestion(assessmentId, courseId, currentUser.getUser_id(), nextLine[0], nextLine[1], nextLine[2], nextLine[3], nextLine[4], nextLine[5], nextLine[6]);

                                }
                            }

                        }

                    } catch (IOException e) {
                    }

                    //clean action
                    assessmentUri = null;

                    //refresh
                    initializeAssessment();

                    //clean
                    addAssessmentDialog.dismiss();

                } else {

                    Toast.makeText(this, "Select the correct file type", Toast.LENGTH_SHORT).show();

                    //start loading
                    addBtn.setEnabled(true);
                    fileImage.setEnabled(true);
                    addProgress.setVisibility(View.GONE);
                    addText.setVisibility(View.VISIBLE);

                    //clean action
                    assessmentUri = null;

                }

            }

        });

    }

    private void openResultListDialog() {

        listResultsDialog = new AlertDialog.Builder(this, R.style.DialogTheme).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View viewOptions = inflater.inflate(R.layout.show_results_layout,null);

        //widgets
        ImageView backBtn = viewOptions.findViewById(R.id.backBtn);
        RecyclerView listRecycler = viewOptions.findViewById(R.id.listRecycler);

        //add view properties
        listResultsDialog.setView(viewOptions);
        listResultsDialog.getWindow().getAttributes().windowAnimations = R.style.SlideDialogAnimation;
        listResultsDialog.getWindow().setGravity(Gravity.BOTTOM);
        listResultsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //add windows properties
        WindowManager.LayoutParams layoutParams = listResultsDialog.getWindow().getAttributes();
        listResultsDialog.getWindow().setAttributes(layoutParams);

        //show dialog
        listResultsDialog.show();

        //back
        backBtn.setOnClickListener(v -> listResultsDialog.dismiss());

        //set recycler
        listRecycler.setHasFixedSize(true);
        listRecycler.setLayoutManager(new LinearLayoutManager(this));

        //data
        List<AssessmentResult> resultList = new ArrayList<>();
        resultList.addAll(new Database(this).getStudentAssessmentResult(courseId));
        listRecycler.setAdapter(new ResultAdapter(this, this, resultList));

    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Video.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MODULE_REQUEST_CODE && resultCode == RESULT_OK){

            if (data.getData() != null) {

                moduleUri = data.getData();
                fileImage.setImageResource(R.drawable.ic_file_selected);

            }

        }

        if (requestCode == ASSIGNMENT_REQUEST_CODE && resultCode == RESULT_OK){

            if (data.getData() != null) {

                assignmentUri = data.getData();
                fileImage.setImageResource(R.drawable.ic_file_selected);
                Log.d("FileError", "File Uri: " + assignmentUri);
                Log.d("FileError", "File Url: " + assignmentUri.getPath());

                File file = new File(assignmentUri.getPath());//create path from uri
                final String[] split = file.getPath().split(":");//split the path.
                Log.d("FileError", "File Url: " + Environment.getExternalStorageDirectory() + "/" + split[1]);

            }

        }

        if (requestCode == ASSESSMENT_REQUEST_CODE && resultCode == RESULT_OK){

            if (data.getData() != null) {

                assessmentUri = data.getData();
                fileImage.setImageResource(R.drawable.ic_file_selected);

            }

        }

    }

    private void generateModuleToken() {

        //set token
        String tempToken = generateRandomToken();

        //get token
        if (!new Database(this).isModuleIdInUse(tempToken)){

            idToken = tempToken;

        } else {

            generateModuleToken();

        }


    }

    private void generateAssignmentToken() {

        //set token
        String tempToken = generateRandomToken();

        //get token
        if (!new Database(this).isAssignmentIdInUse(tempToken)){

            idToken = tempToken;

        } else {

            generateAssignmentToken();

        }


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
}