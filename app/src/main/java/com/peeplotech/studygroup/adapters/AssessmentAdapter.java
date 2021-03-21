package com.peeplotech.studygroup.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.peeplotech.studygroup.R;
import com.peeplotech.studygroup.models.Assessment;
import com.peeplotech.studygroup.student.StudentAssessment;

public class AssessmentAdapter extends RecyclerView.Adapter<AssessmentAdapter.AssessmentViewHolder>{

    private Activity activity;
    private Context ctx;
    private List<Assessment> assessmentList;

    public AssessmentAdapter(Activity activity, Context context, List<Assessment> assessmentList) {
        this.activity = activity;
        this.ctx = context;
        this.assessmentList = assessmentList;
    }

    @NonNull
    @Override
    public AssessmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(ctx).inflate(R.layout.assessment_item, parent, false);

        return new AssessmentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AssessmentViewHolder holder, int position) {

        //get current program
        Assessment currentQuestion = assessmentList.get(position);

        //number
        holder.questionNumber.setText(position + 1 + ". ");

        //update position
        if (ctx instanceof StudentAssessment) {
            ((StudentAssessment)ctx).setCurrentItemPosition(position);
        }

        //question
        holder.question.setText(currentQuestion.getQuestion());

        //options
        holder.radioA.setText(currentQuestion.getOption_a());
        holder.radioB.setText(currentQuestion.getOption_b());
        holder.radioC.setText(currentQuestion.getOption_c());
        holder.radioD.setText(currentQuestion.getOption_d());
        holder.radioE.setText(currentQuestion.getOption_e());

        //button
        holder.nextBtn.setOnClickListener(v -> {

            if (holder.radioA.isChecked()){

                if (currentQuestion.getOption_a().equals(currentQuestion.getAnswer())){

                    if (ctx instanceof StudentAssessment) {
                        ((StudentAssessment)ctx).runTestAlgo(true, false, false, position + 1);
                    }

                } else {

                    if (ctx instanceof StudentAssessment) {
                        ((StudentAssessment)ctx).runTestAlgo(false, true, false, position + 1);
                    }

                }

            } else

            if (holder.radioB.isChecked()){

                if (currentQuestion.getOption_b().equals(currentQuestion.getAnswer())){

                    if (ctx instanceof StudentAssessment) {
                        ((StudentAssessment)ctx).runTestAlgo(true, false, false, position + 1);
                    }

                } else {

                    if (ctx instanceof StudentAssessment) {
                        ((StudentAssessment)ctx).runTestAlgo(false, true, false, position + 1);
                    }

                }

            } else

            if (holder.radioC.isChecked()){

                if (currentQuestion.getOption_c().equals(currentQuestion.getAnswer())){

                    if (ctx instanceof StudentAssessment) {
                        ((StudentAssessment)ctx).runTestAlgo(true, false, false, position + 1);
                    }

                } else {

                    if (ctx instanceof StudentAssessment) {
                        ((StudentAssessment)ctx).runTestAlgo(false, true, false, position + 1);
                    }

                }

            } else

            if (holder.radioD.isChecked()){

                if (currentQuestion.getOption_d().equals(currentQuestion.getAnswer())){

                    if (ctx instanceof StudentAssessment) {
                        ((StudentAssessment)ctx).runTestAlgo(true, false, false, position + 1);
                    }

                } else {

                    if (ctx instanceof StudentAssessment) {
                        ((StudentAssessment)ctx).runTestAlgo(false, true, false, position + 1);
                    }

                }

            } else

            if (holder.radioE.isChecked()){

                if (currentQuestion.getOption_e().equals(currentQuestion.getAnswer())){

                    if (ctx instanceof StudentAssessment) {
                        ((StudentAssessment)ctx).runTestAlgo(true, false, false, position + 1);
                    }

                } else {

                    if (ctx instanceof StudentAssessment) {
                        ((StudentAssessment)ctx).runTestAlgo(false, true, false, position + 1);
                    }

                }

            } else {

                if (ctx instanceof StudentAssessment) {
                    ((StudentAssessment)ctx).runTestAlgo(false, false, true, position + 1);
                }

            }

        });







    }

    @Override
    public int getItemCount() {
        return assessmentList.size();
    }

    public class AssessmentViewHolder extends RecyclerView.ViewHolder {

        //widgets
        public TextView questionNumber, question;
        public RadioGroup answerRadio;
        public RadioButton radioA, radioB, radioC, radioD, radioE;
        public RelativeLayout nextBtn;

        public AssessmentViewHolder(@NonNull View itemView) {
            super(itemView);

            //init widgets
            questionNumber = itemView.findViewById(R.id.questionNumber);
            question = itemView.findViewById(R.id.question);
            answerRadio = itemView.findViewById(R.id.answerRadio);
            radioA = itemView.findViewById(R.id.radioA);
            radioB = itemView.findViewById(R.id.radioB);
            radioC = itemView.findViewById(R.id.radioC);
            radioD = itemView.findViewById(R.id.radioD);
            radioE = itemView.findViewById(R.id.radioE);
            nextBtn = itemView.findViewById(R.id.nextBtn);

        }

    }
}
