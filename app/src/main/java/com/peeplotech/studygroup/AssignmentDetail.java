package com.peeplotech.studygroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.Manifest;
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

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.peeplotech.studygroup.adapters.ChatAdapter;
import com.peeplotech.studygroup.adapters.SubmittedAdapter;
import com.peeplotech.studygroup.databases.Database;
import com.peeplotech.studygroup.models.Assignment;
import com.peeplotech.studygroup.models.Chat;
import com.peeplotech.studygroup.models.SubmittedAssignment;
import com.peeplotech.studygroup.models.User;
import com.peeplotech.studygroup.util.Common;
import io.paperdb.Paper;

public class AssignmentDetail extends AppCompatActivity {

    //widgets
    private ImageView backBtn;
    private TextView assTitle, assScore, assDeadline;
    private TextView viewAss, viewSubmissions, submitAssignment;
    private RecyclerView discussionRecycler;
    private EditTextWithSpeaker chatEdt;
    private ImageView sendBtn;

    //values
    private String assignmentId;
    private Assignment currentAssignment;
    private User currentUser;
    private String idToken = "";
    private String submissionTable;

    //data
    private List<Chat> chatList = new ArrayList<>();
    private ChatAdapter adapter;

    //image upload
    private static final int VERIFY_PERMISSIONS_REQUEST = 757;
    private static final int ASSIGNMENT_REQUEST_CODE = 667;
    private Uri assignmentUri;
    private String assignmentUrl = "";

    //dialogs
    private AlertDialog viewAssignmentDialog, viewSubmittedDialog, submitAssignmentDialog;

    //submit dialog
    private ImageView fileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_detail);

        //values
        assignmentId = getIntent().getStringExtra(Common.INTENT_ASSIGNMENT);
        currentAssignment = new Database(this).getAssignmentDetails(assignmentId);
        currentUser = Paper.book().read(Common.CURRENT_USER);
        submissionTable = assignmentId + Common.SUBMITTED_ASSIGNMENT;

        //widgets
        backBtn = findViewById(R.id.backBtn);
        assTitle = findViewById(R.id.assTitle);
        assScore = findViewById(R.id.assScore);
        assDeadline = findViewById(R.id.assDeadline);
        viewAss = findViewById(R.id.viewAss);
        viewSubmissions = findViewById(R.id.viewSubmissions);
        submitAssignment = findViewById(R.id.submitAssignment);
        discussionRecycler = findViewById(R.id.discussionRecycler);
        chatEdt = findViewById(R.id.chatEdt);
        sendBtn = findViewById(R.id.sendBtn);

        //initialize
        initialize();

    }

    private void initialize() {

        //back
        backBtn.setOnClickListener(v -> onBackPressed());

        //assignment data
        assTitle.setText(currentAssignment.getAssignment_title());
        assScore.setText("Awards " + currentAssignment.getAssignment_score() + " Mrks");
        assDeadline.setText("Deadline: " + currentAssignment.getAssignment_deadline());

        //view
        viewAss.setOnClickListener(v -> {
            openAssignmentDialog();
        });

        //check type
        if (currentUser.getUser_type().equals(Common.USER_TYPE_LECTURER)){
            viewSubmissions.setVisibility(View.VISIBLE);
        } else {
            if (!new Database(this).haveSubmitted("", currentUser.getUser_id())) {
                submitAssignment.setVisibility(View.VISIBLE);
            }
        }

        //view submission
        viewSubmissions.setOnClickListener(v -> {
            openSubmissionListDialog();
        });

        //submit
        submitAssignment.setOnClickListener(v -> {
            openSubmissionDialog();
        });

        //load messages
        loadDiscussion();

        //send discussion
        sendBtn.setOnClickListener(v -> sendDiscussion());

        //request permission
        requestPermission();

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

    private void openAssignmentDialog() {

        viewAssignmentDialog = new AlertDialog.Builder(this, R.style.DialogTheme).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View viewOptions = inflater.inflate(R.layout.view_assignment_layout,null);

        //widgets
        PDFView pdfView = viewOptions.findViewById(R.id.pdfView);
        RelativeLayout closeBtn = viewOptions.findViewById(R.id.closeBtn);

        //add view properties
        viewAssignmentDialog.setView(viewOptions);
        viewAssignmentDialog.getWindow().getAttributes().windowAnimations = R.style.SlideDialogAnimation;
        viewAssignmentDialog.getWindow().setGravity(Gravity.BOTTOM);
        viewAssignmentDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //add windows properties
        WindowManager.LayoutParams layoutParams = viewAssignmentDialog.getWindow().getAttributes();
        viewAssignmentDialog.getWindow().setAttributes(layoutParams);

        //show dialog
        viewAssignmentDialog.show();

        if (currentUser.getUser_type().equals(Common.USER_TYPE_STUDENT))
            new Database(this).updateAssignmentEngagement(currentUser.getUser_id());

        //render pdf
        pdfView.fromUri(Uri.parse(currentAssignment.getAssignment_file()))
                .defaultPage(0)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableAnnotationRendering(true)
                .scrollHandle(new DefaultScrollHandle(this))
                .load();

        //close
        closeBtn.setOnClickListener(v -> {

            viewAssignmentDialog.dismiss();

        });

    }

    private void openSubmissionDialog() {

        submitAssignmentDialog = new AlertDialog.Builder(this, R.style.DialogTheme).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View viewOptions = inflater.inflate(R.layout.submit_assignment_layout,null);

        //widgets
        fileImage = viewOptions.findViewById(R.id.fileImage);
        RelativeLayout submitBtn = viewOptions.findViewById(R.id.submitBtn);
        TextView submitText = viewOptions.findViewById(R.id.submitText);
        AVLoadingIndicatorView submitProgress = viewOptions.findViewById(R.id.submitProgress);

        //add view properties
        submitAssignmentDialog.setView(viewOptions);
        submitAssignmentDialog.getWindow().getAttributes().windowAnimations = R.style.SlideDialogAnimation;
        submitAssignmentDialog.getWindow().setGravity(Gravity.BOTTOM);
        submitAssignmentDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //add windows properties
        WindowManager.LayoutParams layoutParams = submitAssignmentDialog.getWindow().getAttributes();
        submitAssignmentDialog.getWindow().setAttributes(layoutParams);

        //show dialog
        submitAssignmentDialog.show();

        //generate assignment Token
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
        submitBtn.setOnClickListener(v -> {

            //validate
            if (assignmentUri == null){

                Toast.makeText(this, "Select assignment file", Toast.LENGTH_SHORT).show();

            } else {

                //start loading
                submitBtn.setEnabled(false);
                fileImage.setEnabled(false);
                submitText.setVisibility(View.GONE);
                submitProgress.setVisibility(View.VISIBLE);

                //avatar folder
                File assignmentFolder = new File(Environment.getExternalStorageDirectory(), Common.BASE_FOLDER + "/" + Common.SUBMITTED_ASSIGNMENT_FOLDER);
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

                //create new submission
                new Database(this).submitAssignment(submissionTable, idToken, currentUser.getUser_id(), "", currentAssignment.getCourse_id(), assignmentUrl);

                if (currentUser.getUser_type().equals(Common.USER_TYPE_STUDENT))
                    new Database(this).updateSubmittedEngagement(currentUser.getUser_id());

                //clean action
                assignmentUri = null;
                assignmentUrl = "";
                idToken = "";

                //refresh
                submitAssignment.setVisibility(View.GONE);

                //clean
                submitAssignmentDialog.dismiss();

            }

        });

    }

    private void openSubmissionListDialog() {

        viewSubmittedDialog = new AlertDialog.Builder(this, R.style.DialogTheme).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View viewOptions = inflater.inflate(R.layout.show_submissions_layout,null);

        //widgets
        ImageView backBtn = viewOptions.findViewById(R.id.backBtn);
        RecyclerView listRecycler = viewOptions.findViewById(R.id.listRecycler);

        //add view properties
        viewSubmittedDialog.setView(viewOptions);
        viewSubmittedDialog.getWindow().getAttributes().windowAnimations = R.style.SlideDialogAnimation;
        viewSubmittedDialog.getWindow().setGravity(Gravity.BOTTOM);
        viewSubmittedDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //add windows properties
        WindowManager.LayoutParams layoutParams = viewSubmittedDialog.getWindow().getAttributes();
        viewSubmittedDialog.getWindow().setAttributes(layoutParams);

        //show dialog
        viewSubmittedDialog.show();

        //back
        backBtn.setOnClickListener(v -> viewSubmittedDialog.dismiss());

        //set recycler
        listRecycler.setHasFixedSize(true);
        listRecycler.setLayoutManager(new LinearLayoutManager(this));

        //data
        List<SubmittedAssignment> assignmentList = new ArrayList<>();
        assignmentList.addAll(new Database(this).getAllSubmittedAssignments(submissionTable));
        listRecycler.setAdapter(new SubmittedAdapter(this, this, assignmentList, assignmentId));

    }

    private void generateAssignmentToken() {

        //set token
        String tempToken = generateRandomToken();

        //get token
        if (!new Database(this).isSubmittedAssignmentIdInUse(submissionTable, tempToken)){

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ASSIGNMENT_REQUEST_CODE && resultCode == RESULT_OK){

            if (data.getData() != null) {

                assignmentUri = data.getData();
                fileImage.setImageResource(R.drawable.ic_file_selected);

            }

        }

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

    private void loadDiscussion() {

        //chat id
        String chatTable = assignmentId + Common.ASSIGNMENT_CHAT;

        //add all
        chatList.addAll(new Database(this).getMessages(chatTable));

        //init recycler
        discussionRecycler.setHasFixedSize(true);
        discussionRecycler.setLayoutManager(new LinearLayoutManager(this));

        //disable default animator
        ((SimpleItemAnimator) discussionRecycler.getItemAnimator()).setSupportsChangeAnimations(false);

        //adapter
        adapter = new ChatAdapter(this, this, chatList, chatTable);
        discussionRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //scroll to bottom
        discussionRecycler.scrollToPosition(chatList.size() - 1);

    }

    private void sendDiscussion() {

        //chat id
        String chatTable = assignmentId + Common.ASSIGNMENT_CHAT;

        //extract string
        String theMsg = chatEdt.getText().toString().trim();

        if (!TextUtils.isEmpty(theMsg)){

            //int msg id
            int msgId = chatList.size() + 1;

            //add message
            new Database(this).sendMessage(chatTable, currentUser.getUser_id(), theMsg, Common.NOT_APPROVED);

            if (currentUser.getUser_type().equals(Common.USER_TYPE_STUDENT))
                new Database(this).updateGroupEngagement(currentUser.getUser_id());

            //add to list
            chatList.add(new Chat(msgId, currentUser.getUser_id(), theMsg, Common.NOT_APPROVED));
            adapter.notifyDataSetChanged();

            //sroll to
            discussionRecycler.scrollToPosition(chatList.size() - 1);

            //clear edt
            chatEdt.setText("");

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_right, R.anim.slide_out_right);
    }
}