package esw.peeplotech.studygroup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

import esw.peeplotech.studygroup.databases.Database;
import esw.peeplotech.studygroup.lecturer.LecturerDashboard;
import esw.peeplotech.studygroup.models.User;
import esw.peeplotech.studygroup.student.StudentDashboard;
import esw.peeplotech.studygroup.util.Common;
import io.paperdb.Paper;

public class Login extends AppCompatActivity {

    //widgets
    private ImageView backButton;
    private EditText userIdentification, userPassword;
    private TextView registerLink;

    //button
    private RelativeLayout loginBtn;
    private TextView loginText;
    private AVLoadingIndicatorView loginProgress;

    //loading
    private android.app.AlertDialog theDialog;
    private boolean isDialogShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //widgets
        backButton = findViewById(R.id.backButton);
        userIdentification = findViewById(R.id.userIdentification);
        userPassword = findViewById(R.id.userPassword);
        registerLink = findViewById(R.id.registerLink);
        loginBtn = findViewById(R.id.loginBtn);
        loginText = findViewById(R.id.loginText);
        loginProgress = findViewById(R.id.loginProgress);

        //init
        initialize();

    }

    private void initialize() {

        //back
        backButton.setOnClickListener(v -> {
            onBackPressed();
            overridePendingTransition(R.anim.slide_right, R.anim.slide_out_right);
        });

        //register link
        registerLink.setOnClickListener(v -> {
            startActivity(new Intent(this, Registration.class));
            finish();
            overridePendingTransition(R.anim.slide_left, R.anim.slide_out_left);
        });

        //login
        loginBtn.setOnClickListener(v -> validateParams());

    }

    private void validateParams() {

        String theId = userIdentification.getText().toString().trim();
        String thePassword = userPassword.getText().toString().trim();

        //validate
        if (TextUtils.isEmpty(theId)){

            userIdentification.requestFocus();
            userIdentification.setError("Required");

        } else

        if (TextUtils.isEmpty(thePassword)){

            userPassword.requestFocus();
            userPassword.setError("Required");

        } else {

            loginUser(theId, thePassword);

        }
    }

    private void loginUser(String theId, String thePassword) {

        //start loading
        loginBtn.setEnabled(false);
        userIdentification.setEnabled(false);
        userPassword.setEnabled(false);
        registerLink.setEnabled(false);
        loginText.setVisibility(View.GONE);
        loginProgress.setVisibility(View.VISIBLE);

        //check if user exists
        if (!new Database(this).userExists(theId)){

            //stop loading
            loginBtn.setEnabled(true);
            userIdentification.setEnabled(true);
            userPassword.setEnabled(true);
            registerLink.setEnabled(true);
            loginProgress.setVisibility(View.GONE);
            loginText.setVisibility(View.VISIBLE);

            //show error
            showInfoDialog("Error", "User with id \" " + theId + " \" does not exist in database. Please, register.");

        } else {

            //login
            if (new Database(this).loginUser(theId, thePassword)){
                //sign user in
                checkUserType(new Database(this).getUserDetails(theId));
            } else {
                //stop loading
                loginBtn.setEnabled(true);
                userIdentification.setEnabled(true);
                userPassword.setEnabled(true);
                registerLink.setEnabled(true);
                loginProgress.setVisibility(View.GONE);
                loginText.setVisibility(View.VISIBLE);

                //show error
                showInfoDialog("Wrong Password", "Password you provided is wrong. Enter correct one and try again.");
            }

        }

    }

    private void checkUserType(User userDetails) {

        //stop loading
        loginBtn.setEnabled(true);
        userIdentification.setEnabled(true);
        userPassword.setEnabled(true);
        registerLink.setEnabled(true);
        loginProgress.setVisibility(View.GONE);
        loginText.setVisibility(View.VISIBLE);

        //check user type
        if (userDetails.getUser_type().equals(Common.USER_TYPE_LECTURER)){

            //store local details
            Paper.book().write(Common.USER_ID, userDetails.getUser_id());
            Paper.book().write(Common.USER_TYPE, userDetails.getUser_type());
            Paper.book().write(Common.CURRENT_USER, userDetails);

            //update ui
            Intent homeIntent = new Intent(this, LecturerDashboard.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
            finish();
            overridePendingTransition(R.anim.slide_left, R.anim.slide_out_left);

        } else {

            //store local details
            Paper.book().write(Common.USER_ID, userDetails.getUser_id());
            Paper.book().write(Common.USER_TYPE, userDetails.getUser_type());
            Paper.book().write(Common.CURRENT_USER, userDetails);

            //update ui
            Intent homeIntent = new Intent(this, StudentDashboard.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
            finish();
            overridePendingTransition(R.anim.slide_left, R.anim.slide_out_left);

        }

    }

    private void showInfoDialog(String title, String message){

        //change state
        isDialogShowing = true;

        //create dialog
        theDialog = new android.app.AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View viewOptions = inflater.inflate(R.layout.general_info_dialog,null);

        //widget
        TextView dialogTitle = viewOptions.findViewById(R.id.dialogTitle);
        TextView dialogText = viewOptions.findViewById(R.id.dialogText);
        TextView okayBtn = viewOptions.findViewById(R.id.okayBtn);

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
        dialogTitle.setText(title);
        dialogText.setText(message);

        //okay
        okayBtn.setOnClickListener(view -> theDialog.dismiss());

        //show dialog
        theDialog.show();

    }

    private void showChoiceDialog(String title, String message, String positive, String negative){

        //change state
        isDialogShowing = true;

        //create dialog
        theDialog = new android.app.AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View viewOptions = inflater.inflate(R.layout.general_choice_dialog,null);

        //widget
        TextView dialogTitle = viewOptions.findViewById(R.id.dialogTitle);
        TextView dialogText = viewOptions.findViewById(R.id.dialogText);
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
        dialogTitle.setText(title);
        dialogText.setText(message);
        negativeBtn.setText(negative);
        positiveBtn.setText(positive);

        //okay
        negativeBtn.setOnClickListener(view -> theDialog.dismiss());
        positiveBtn.setOnClickListener(view -> theDialog.dismiss());

        //show dialog
        theDialog.show();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_right, R.anim.slide_out_right);
    }
}