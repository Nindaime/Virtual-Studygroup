package com.peeplotech.studygroup.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.peeplotech.studygroup.R;
import com.peeplotech.studygroup.databases.Database;
import com.peeplotech.studygroup.models.AssessmentResult;
import com.peeplotech.studygroup.models.User;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder>{

    private Activity activity;
    private Context ctx;
    private List<AssessmentResult> resultList;

    public ResultAdapter(Activity activity, Context context, List<AssessmentResult> resultList) {
        this.activity = activity;
        this.ctx = context;
        this.resultList = resultList;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(ctx).inflate(R.layout.result_item, parent, false);

        return new ResultViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {

        //get current module
        AssessmentResult currentResult = resultList.get(position);
        User currentUser = new Database(ctx).getUserDetails(currentResult.getStudent_id());
        String name = currentUser.getFirst_name() + " " + currentUser.getLast_name();


        holder.studentName.setText(name);
        holder.attempted.setText("Attempted " + currentResult.getAttempted() + " questions of " + currentResult.getTotal());
        holder.passed.setText("Passed " + currentResult.getPassed());
        holder.failed.setText("Failed " + currentResult.getFailed());

        //grade
        int passed = Integer.parseInt(currentResult.getPassed());
        int total = Integer.parseInt(currentResult.getTotal());
        int grade = (int) ((passed * 100) / total);

        if (grade > 70){

            holder.grade.setText("Result: " + grade + " (A)");

        } else

        if (grade < 70 && grade > 59){

            holder.grade.setText("Result: " + grade + " (B)");

        } else

        if (grade < 60 && grade > 49){

            holder.grade.setText("Result: " + grade + " (C)");

        } else

        if (grade < 50 && grade > 44){

            holder.grade.setText("Result: " + grade + " (D)");

        } else

        if (grade < 45 && grade > 39){

            holder.grade.setText("Result: " + grade + " (E)");

        } else

        if (grade < 40){

            holder.grade.setText("Result: " + grade + " (F)");

        }

    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public class ResultViewHolder extends RecyclerView.ViewHolder {

        //widgets
        public TextView studentName, attempted, passed, failed, grade;

        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);

            //init widgets
            studentName = itemView.findViewById(R.id.studentName);
            attempted = itemView.findViewById(R.id.attempted);
            passed = itemView.findViewById(R.id.passed);
            failed = itemView.findViewById(R.id.failed);
            grade = itemView.findViewById(R.id.grade);


        }
    }
}
