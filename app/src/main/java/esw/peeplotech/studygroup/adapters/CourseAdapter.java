package esw.peeplotech.studygroup.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import esw.peeplotech.studygroup.R;
import esw.peeplotech.studygroup.interfaces.ItemClickListener;
import esw.peeplotech.studygroup.lecturer.LecturerCourse;
import esw.peeplotech.studygroup.models.Course;
import esw.peeplotech.studygroup.student.StudentCourse;
import esw.peeplotech.studygroup.util.Common;
import io.paperdb.Paper;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder>{

    private Activity activity;
    private Context ctx;
    private List<Course> courseList;

    public CourseAdapter(Activity activity, Context context, List<Course> courseList) {
        this.activity = activity;
        this.ctx = context;
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(ctx).inflate(R.layout.course_item, parent, false);

        return new CourseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {

        //get current program
        Course currentCourse = courseList.get(position);

        //current user type
        String userType = Paper.book().read(Common.USER_TYPE);

        //bind data
        if (!TextUtils.isEmpty(currentCourse.getCourse_img())){
            Picasso.get()
                    .load(currentCourse.getCourse_img())
                    .config(Bitmap.Config.RGB_565)
                    .fit().centerCrop()
                    .into(holder.courseImage);
        }
        holder.courseTitle.setText(currentCourse.getCourse_title());
        holder.courseSubtopic.setText(currentCourse.getCourse_sub_title());

        //click
        holder.setItemClickListener((view, position1, isLongClick) -> {

            if (userType.equals(Common.USER_TYPE_LECTURER)){

                Intent courseIntent = new Intent(ctx, LecturerCourse.class);
                courseIntent.putExtra(Common.INTENT_COURSE, currentCourse.getCourse_id());
                ctx.startActivity(courseIntent);
                activity.overridePendingTransition(R.anim.slide_left, R.anim.slide_out_left);

            } else {

                Intent courseIntent = new Intent(ctx, StudentCourse.class);
                courseIntent.putExtra(Common.INTENT_COURSE, currentCourse.getCourse_id());
                ctx.startActivity(courseIntent);
                activity.overridePendingTransition(R.anim.slide_left, R.anim.slide_out_left);

            }


        });

    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        //widgets
        private ItemClickListener itemClickListener;
        public RoundedImageView courseImage;
        public TextView courseTitle, courseSubtopic;

        public CourseViewHolder(@NonNull View itemView) {
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
