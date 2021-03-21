package com.peeplotech.studygroup.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;

import com.peeplotech.studygroup.R;
import com.peeplotech.studygroup.databases.Database;
import com.peeplotech.studygroup.models.Assignment;
import com.peeplotech.studygroup.models.SubmittedAssignment;
import com.peeplotech.studygroup.models.User;
import com.peeplotech.studygroup.util.Common;

public class SubmittedAdapter extends RecyclerView.Adapter<SubmittedAdapter.AssignmentViewHolder>{

    private Activity activity;
    private Context ctx;
    private List<SubmittedAssignment> assignmentList;
    private String assignmentId;

    //dialogs
    private AlertDialog viewAssignmentDialog;

    public SubmittedAdapter(Activity activity, Context context, List<SubmittedAssignment> assignmentList, String assignmentId) {
        this.activity = activity;
        this.ctx = context;
        this.assignmentList = assignmentList;
        this.assignmentId = assignmentId;
    }

    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(ctx).inflate(R.layout.submission_item_item, parent, false);

        return new AssignmentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {

        //get all details
        SubmittedAssignment currentAssignment = assignmentList.get(position);
        User currentStudent = new Database(ctx).getUserDetails(currentAssignment.getStudent_id());
        String studentName = currentStudent.getFirst_name() + " " + currentStudent.getLast_name() + " (@" + currentStudent.getUser_id() + ")";
        Assignment currentMainAssignment = new Database(ctx).getAssignmentDetails(assignmentId);
        String overallScore = currentMainAssignment.getAssignment_score();


        holder.studentName.setText(studentName);
        holder.studentScore.setText("Scored: " + currentAssignment.getAssignment_score() + " of " + overallScore);

        if (currentAssignment.getAssignment_score().isEmpty()){

            holder.studentScore.setVisibility(View.GONE);
            holder.viewSubmission.setVisibility(View.VISIBLE);

            //click
            holder.viewSubmission.setOnClickListener(v -> {

                openSubmissionDocumentDialog(currentAssignment.getSubmission_id(), currentAssignment.getAssignment_file());

            });

        } else {

            holder.studentScore.setVisibility(View.VISIBLE);
            holder.viewSubmission.setVisibility(View.GONE);

        }

    }

    @Override
    public int getItemCount() {
        return assignmentList.size();
    }

    public class AssignmentViewHolder extends RecyclerView.ViewHolder{

        //widgets
        public TextView studentName, studentScore, viewSubmission;

        public AssignmentViewHolder(@NonNull View itemView) {
            super(itemView);

            //init widgets
            studentName = itemView.findViewById(R.id.studentName);
            studentScore = itemView.findViewById(R.id.studentScore);
            viewSubmission = itemView.findViewById(R.id.viewSubmission);


        }

    }

    private void openSubmissionDocumentDialog(String theId, String theAssignment) {

        viewAssignmentDialog = new AlertDialog.Builder(ctx, R.style.DialogTheme).create();
        LayoutInflater inflater = activity.getLayoutInflater();
        View viewOptions = inflater.inflate(R.layout.view_submission_layout,null);

        //widgets
        PDFView pdfView = viewOptions.findViewById(R.id.pdfView);
        EditText assignmentScore = viewOptions.findViewById(R.id.assignmentScore);
        RelativeLayout scoreBtn = viewOptions.findViewById(R.id.scoreBtn);
        TextView scoreText = viewOptions.findViewById(R.id.scoreText);
        AVLoadingIndicatorView scoreProgress = viewOptions.findViewById(R.id.scoreProgress);

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

        //render pdf
        //render pdf
        pdfView.fromUri(Uri.parse(theAssignment))
                .defaultPage(1)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableAnnotationRendering(true)
                .scrollHandle(new DefaultScrollHandle(ctx))
                .load();

        //add
        scoreBtn.setOnClickListener(v -> {

            //validate
            if (assignmentScore.getText().toString().trim().isEmpty()){

                assignmentScore.requestFocus();
                assignmentScore.setError("Required");

            } else {

                //start loading
                scoreBtn.setEnabled(false);
                assignmentScore.setEnabled(false);
                scoreText.setVisibility(View.GONE);
                scoreProgress.setVisibility(View.VISIBLE);

                //table
                String tableId = assignmentId + Common.SUBMITTED_ASSIGNMENT;

                //score
                new Database(ctx).scoreAssignment(tableId, theId, assignmentScore.getText().toString().trim());

                //dismiss
                viewAssignmentDialog.dismiss();

            }

        });

    }
}
