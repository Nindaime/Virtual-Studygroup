package com.peeplotech.studygroup.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import com.peeplotech.studygroup.R;
import com.peeplotech.studygroup.databases.Database;
import com.peeplotech.studygroup.interfaces.ItemClickListener;
import com.peeplotech.studygroup.models.Course;
import com.peeplotech.studygroup.models.SubscribedCourse;
import com.peeplotech.studygroup.student.StudentCourse;
import com.peeplotech.studygroup.util.Common;

public class SubscribedCourseAdapter extends RecyclerView.Adapter<SubscribedCourseAdapter.SubscribedCourseViewHolder>{

    private Activity activity;
    private Context ctx;
    private List<SubscribedCourse> courseList;

    public SubscribedCourseAdapter(Activity activity, Context context, List<SubscribedCourse> courseList) {
        this.activity = activity;
        this.ctx = context;
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public SubscribedCourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(ctx).inflate(R.layout.course_item, parent, false);

        return new SubscribedCourseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SubscribedCourseViewHolder holder, int position) {

        //get current program
        SubscribedCourse currentSubbedCourse = courseList.get(position);

        //current course
        Course currentCourse = new Database(ctx).getCourseDetails(currentSubbedCourse.getCourse_id());

        //bind data
        if (!TextUtils.isEmpty(currentCourse.getCourse_img())){
            Picasso.get()
                    .load(Uri.parse(currentCourse.getCourse_img()))
                    .config(Bitmap.Config.RGB_565)
                    .fit().centerCrop()
                    .into(holder.courseImage);
        }
        holder.courseTitle.setText(currentCourse.getCourse_title());
        holder.courseSubtopic.setText(currentCourse.getCourse_sub_title());

        //click
        holder.setItemClickListener((view, position1, isLongClick) -> {

            Intent courseIntent = new Intent(ctx, StudentCourse.class);
            courseIntent.putExtra(Common.INTENT_COURSE, currentCourse.getCourse_id());
            ctx.startActivity(courseIntent);
            activity.overridePendingTransition(R.anim.slide_left, R.anim.slide_out_left);


        });

    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public class SubscribedCourseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        //widgets
        private ItemClickListener itemClickListener;
        public RoundedImageView courseImage;
        public TextView courseTitle, courseSubtopic;

        public SubscribedCourseViewHolder(@NonNull View itemView) {
            super(itemView);

            //init widgets
            courseImage = itemView.findViewById(R.id.courseImage);
            courseTitle = itemView.findViewById(R.id.courseTitle);
            courseSubtopic = itemView.findViewById(R.id.courseSubtopic);

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
