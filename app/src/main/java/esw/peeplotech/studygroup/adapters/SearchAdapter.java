package esw.peeplotech.studygroup.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import esw.peeplotech.studygroup.R;
import esw.peeplotech.studygroup.databases.Database;
import esw.peeplotech.studygroup.interfaces.ItemClickListener;
import esw.peeplotech.studygroup.lecturer.LecturerCourse;
import esw.peeplotech.studygroup.models.Course;
import esw.peeplotech.studygroup.models.User;
import esw.peeplotech.studygroup.util.Common;
import io.paperdb.Paper;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder>{

    private Activity activity;
    private Context ctx;
    private List<Course> courseList;

    //loading
    private android.app.AlertDialog theDialog;
    private boolean isDialogShowing = false;

    public SearchAdapter(Activity activity, Context context, List<Course> courseList) {
        this.activity = activity;
        this.ctx = context;
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(ctx).inflate(R.layout.course_item, parent, false);

        return new SearchViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {

        //get current program
        Course currentCourse = courseList.get(position);

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

            //current user
            User currentUser = Paper.book().read(Common.CURRENT_USER);
            String subscriptionString = null;

            //get lecturer name
            User lecturer = new Database(ctx).getUserDetails(currentCourse.getCourse_owner());
            String lecturerName = lecturer.getFirst_name() + " " + lecturer.getLast_name();

            //check if subscribed
            if (new Database(ctx).haveISubscribed(currentUser.getUser_id(), currentCourse.getCourse_id())){

                subscriptionString = Common.IS_SUBSCRIBED;

            } else {

                subscriptionString = Common.NOT_SUBSCRIBED;

            }

            //show dialog
            showSubDialog(
                    currentUser.getUser_id(),
                    currentCourse.getCourse_id(),
                    currentCourse.getCourse_title(),
                    currentCourse.getCourse_sub_title(),
                    lecturerName,
                    subscriptionString,
                    "Cancel");

        });

    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        //widgets
        private ItemClickListener itemClickListener;
        public RoundedImageView courseImage;
        public TextView courseTitle, courseSubtopic;

        public SearchViewHolder(@NonNull View itemView) {
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

    private void showSubDialog(String userId, String courseId, String title, String subTitle, String lecturer, String positive, String negative){

        //change state
        isDialogShowing = true;

        //create dialog
        theDialog = new android.app.AlertDialog.Builder(ctx).create();
        LayoutInflater inflater = activity.getLayoutInflater();
        View viewOptions = inflater.inflate(R.layout.course_subscription_dialog,null);

        //widget
        TextView courseTitle = viewOptions.findViewById(R.id.courseTitle);
        TextView courseSubTitle = viewOptions.findViewById(R.id.courseSubTitle);
        TextView courseLecturer = viewOptions.findViewById(R.id.courseLecturer);
        TextView negativeBtn = viewOptions.findViewById(R.id.negativeBtn);
        TextView positiveBtn = viewOptions.findViewById(R.id.positiveBtn);

        //dialog props
        theDialog.setView(viewOptions);
        theDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        theDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //set dialog listener
        theDialog.setOnCancelListener(dialogInterface -> isDialogShowing = false);
        theDialog.setOnDismissListener(dialogInterface -> isDialogShowing = false);

        //lock dialog
        theDialog.setCancelable(true);
        theDialog.setCanceledOnTouchOutside(true);

        //set message
        courseTitle.setText(title);
        courseSubTitle.setText(subTitle);
        courseLecturer.setText("Lecturer: " + lecturer);
        negativeBtn.setText(negative);

        //positive
        positiveBtn.setText(positive);
        if (positive.equals(Common.NOT_SUBSCRIBED)){

            positiveBtn.setOnClickListener(view -> {

                //subscribe to topic
                new Database(ctx).subscribeToCourse(userId, courseId);

                //show message
                Toast.makeText(ctx, "Subscribed Successfully", Toast.LENGTH_SHORT).show();

                //dismiss dialog
                theDialog.dismiss();

            });

        } else {

            positiveBtn.setOnClickListener(view -> {

                //subscribe to topic
                new Database(ctx).unsubscribeToCourse(userId, courseId);

                //show message
                Toast.makeText(ctx, "Subscribed Successfully", Toast.LENGTH_SHORT).show();

                //dismiss dialog
                theDialog.dismiss();

            });

        }

        //negative
        negativeBtn.setOnClickListener(view -> theDialog.dismiss());

        //show dialog
        theDialog.show();

    }
}
