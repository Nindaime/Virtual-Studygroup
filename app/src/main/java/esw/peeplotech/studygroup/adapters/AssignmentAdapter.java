package esw.peeplotech.studygroup.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import esw.peeplotech.studygroup.AssignmentDetail;
import esw.peeplotech.studygroup.ModuleDetail;
import esw.peeplotech.studygroup.R;
import esw.peeplotech.studygroup.interfaces.ItemClickListener;
import esw.peeplotech.studygroup.models.Assignment;
import esw.peeplotech.studygroup.models.Module;
import esw.peeplotech.studygroup.util.Common;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder>{

    private Activity activity;
    private Context ctx;
    private List<Assignment> assignmentList;

    public AssignmentAdapter(Activity activity, Context context, List<Assignment> assignmentList) {
        this.activity = activity;
        this.ctx = context;
        this.assignmentList = assignmentList;
    }

    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(ctx).inflate(R.layout.module_item, parent, false);

        return new AssignmentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {

        //get current module
        Assignment currentAssignment = assignmentList.get(position);


        holder.moduleTitle.setText(currentAssignment.getAssignment_title());
        holder.moduleBrief.setText("Awards: " + currentAssignment.getAssignment_score() + " Mrks  |  Deadline: " + currentAssignment.getAssignment_deadline());

        //click
        holder.setItemClickListener((view, position1, isLongClick) -> {

            Intent courseIntent = new Intent(ctx, AssignmentDetail.class);
            courseIntent.putExtra(Common.INTENT_MODULE, currentAssignment.getAssignment_id());
            ctx.startActivity(courseIntent);
            activity.overridePendingTransition(R.anim.slide_left, R.anim.slide_out_left);

        });

    }

    @Override
    public int getItemCount() {
        return assignmentList.size();
    }

    public class AssignmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        //widgets
        private ItemClickListener itemClickListener;
        public TextView moduleTitle, moduleBrief;

        public AssignmentViewHolder(@NonNull View itemView) {
            super(itemView);

            //init widgets
            moduleTitle = itemView.findViewById(R.id.moduleTitle);
            moduleBrief = itemView.findViewById(R.id.moduleBrief);

            //click
            itemView.setOnClickListener(this);

        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), false);
        }
    }
}
