package esw.peeplotech.studygroup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import esw.peeplotech.studygroup.databases.Database;
import esw.peeplotech.studygroup.lecturer.LecturerDashboard;
import esw.peeplotech.studygroup.models.User;
import esw.peeplotech.studygroup.student.StudentDashboard;
import esw.peeplotech.studygroup.util.Common;
import io.paperdb.Paper;

public class Registration extends AppCompatActivity {

    //widgets
    private ImageView backButton;
    private Spinner userType;
    private EditText userIdentification, firstName, lastName, userPassword, confirmPassword;
    private TextView loginLink;

    //button
    private RelativeLayout registerBtn;
    private TextView registerText;
    private AVLoadingIndicatorView registerProgress;

    //values
    private String selectedType;

    //loading
    private android.app.AlertDialog theDialog;
    private boolean isDialogShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //widgets
        backButton = findViewById(R.id.backButton);
        userType = findViewById(R.id.userType);
        userIdentification = findViewById(R.id.userIdentification);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        userPassword = findViewById(R.id.userPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        loginLink = findViewById(R.id.loginLink);
        registerBtn = findViewById(R.id.registerBtn);
        registerText = findViewById(R.id.registerText);
        registerProgress = findViewById(R.id.registerProgress);

        //init
        initialize();
    }

    private void initialize() {

        //back
        backButton.setOnClickListener(v -> {
            onBackPressed();
        });

        //init user types
        populateTypeSpinner();

        //login link
        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(this, Login.class));
            finish();
            overridePendingTransition(R.anim.slide_right, R.anim.slide_out_right);
        });

        //register
        registerBtn.setOnClickListener(v -> validateParams());

    }

    private void populateTypeSpinner() {

        //list
        final List<String> userList = new ArrayList<>();
        userList.add(0, "User Type");
        userList.add(1, Common.USER_TYPE_LECTURER);
        userList.add(2, Common.USER_TYPE_STUDENT);

        //adapter
        final ArrayAdapter<String> dataAdapterUser;
        dataAdapterUser = new ArrayAdapter(this, R.layout.custom_spinner_list_item, userList);
        dataAdapterUser.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);

        //set adapter
        userType.setAdapter(dataAdapterUser);
        dataAdapterUser.notifyDataSetChanged();

        //selector
        userType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (!parent.getItemAtPosition(position).toString().equals("User Type")) {

                    selectedType = parent.getItemAtPosition(position).toString();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void validateParams() {

        String theId = userIdentification.getText().toString().trim();
        String theFirstName = firstName.getText().toString().trim();
        String theLastName = lastName.getText().toString().trim();
        String thePassword = userPassword.getText().toString().trim();
        String thePassConf = confirmPassword.getText().toString().trim();

        //validate
        if (TextUtils.isEmpty(theId)){

            userIdentification.requestFocus();
            userIdentification.setError("Required");

        } else

        if (TextUtils.isEmpty(theFirstName)){

            firstName.requestFocus();
            firstName.setError("Required");

        } else

        if (TextUtils.isEmpty(theLastName)){

            lastName.requestFocus();
            lastName.setError("Required");

        } else

        if (TextUtils.isEmpty(selectedType)){

            Toast.makeText(this, "Select Type", Toast.LENGTH_SHORT).show();

        } else

        if (TextUtils.isEmpty(thePassword)){

            userPassword.requestFocus();
            userPassword.setError("Required");

        } else

        if (thePassword.length() < 6){

            userPassword.requestFocus();
            userPassword.setError("Too weak");

        } else

        if (!thePassConf.equals(thePassword)){

            confirmPassword.requestFocus();
            confirmPassword.setError("Mismatch");

        } else {

            registerUser(theId, theFirstName, theLastName, thePassword);

        }
    }

    private void registerUser(String theId, String theFirstName, String theLastName, String thePassword) {

        //start loading
        registerBtn.setEnabled(false);
        userIdentification.setEnabled(false);
        firstName.setEnabled(false);
        lastName.setEnabled(false);
        userPassword.setEnabled(false);
        confirmPassword.setEnabled(false);
        userType.setEnabled(false);
        loginLink.setEnabled(false);
        registerText.setVisibility(View.GONE);
        registerProgress.setVisibility(View.VISIBLE);

        //check if user exists
        if (new Database(this).userExists(theId)){

            //stop loading
            registerBtn.setEnabled(true);
            userIdentification.setEnabled(true);
            firstName.setEnabled(true);
            lastName.setEnabled(true);
            userPassword.setEnabled(true);
            confirmPassword.setEnabled(true);
            userType.setEnabled(true);
            loginLink.setEnabled(true);
            registerProgress.setVisibility(View.GONE);
            registerText.setVisibility(View.VISIBLE);

            //show error
            showInfoDialog("Error", "A user with id \" " + theId + " \" already exists, please chose a new one.");

        } else {

            //register
            new Database(this).registerNewUser(theId, theFirstName, theLastName, thePassword, selectedType, "");

            //sign user in
            checkUserType(new Database(this).getUserDetails(theId));

        }

    }

    private void checkUserType(User userDetails) {

        //stop loading
        registerBtn.setEnabled(true);
        userIdentification.setEnabled(true);
        firstName.setEnabled(true);
        lastName.setEnabled(true);
        userPassword.setEnabled(true);
        confirmPassword.setEnabled(true);
        userType.setEnabled(true);
        loginLink.setEnabled(true);
        registerProgress.setVisibility(View.GONE);
        registerText.setVisibility(View.VISIBLE);

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