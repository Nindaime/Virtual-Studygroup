package com.peeplotech.studygroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
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

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
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

import com.peeplotech.studygroup.databases.Database;
import com.peeplotech.studygroup.lecturer.LecturerDashboard;
import com.peeplotech.studygroup.models.User;
import com.peeplotech.studygroup.student.StudentDashboard;
import com.peeplotech.studygroup.util.Common;
import io.paperdb.Paper;

public class Registration extends AppCompatActivity {

    //widgets
    private ImageView backButton;
    private Spinner userType;
    private EditText userIdentification, firstName, lastName, userPassword, confirmPassword;
    private TextView loginLink, changeAvatar;
    private RoundedImageView userAvatar;

    //button
    private RelativeLayout registerBtn;
    private TextView registerText;
    private AVLoadingIndicatorView registerProgress;

    //values
    private String selectedType;

    //loading
    private android.app.AlertDialog theDialog;
    private boolean isDialogShowing = false;

    //image upload
    private static final int VERIFY_PERMISSIONS_REQUEST = 757;
    private static final int GALLERY_REQUEST_CODE = 665;
    private Uri imageUri;
    private String imageLink = "";

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
        changeAvatar = findViewById(R.id.changeAvatar);
        userAvatar = findViewById(R.id.userAvatar);

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

        //change
        changeAvatar.setOnClickListener(v -> {

            //check permissions
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

                showGallery();

            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, VERIFY_PERMISSIONS_REQUEST);

            }

        });

    }

    private void showGallery() {

        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , GALLERY_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == VERIFY_PERMISSIONS_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                showGallery();

            } else {

                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();

            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){

            if (data.getData() != null) {
                imageUri = data.getData();

                CropImage.activity(imageUri)
                        .setAspectRatio(1, 1)
                        .start(Registration.this);
            }

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            final CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                //get data
                Uri resultUri = result.getUri();





                //avatar folder
                File avatarFolder = new File(Environment.getExternalStorageDirectory(), Common.BASE_FOLDER + "/" + Common.AVATAR_FOLDER);
                if (!avatarFolder.exists()){
                    avatarFolder.mkdir();
                }

                //avatar file
                File avatarFile = new File(avatarFolder.getAbsolutePath(), generateRandomToken() + ".jpg");

                //copy file
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = new FileInputStream(resultUri.getPath());
                    out = new FileOutputStream(avatarFile.getAbsolutePath());

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


                //create image uri
                Uri uri = Uri.fromFile(new File(avatarFile.getAbsolutePath()));
                imageLink = uri.toString();


                //set image
                Picasso.get()
                        .load(Uri.parse(imageLink))
                        .config(Bitmap.Config.RGB_565)
                        .fit().centerCrop()
                        .into(userAvatar);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
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
            new Database(this).registerNewUser(theId, theFirstName, theLastName, thePassword, selectedType, imageLink);

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

            if (!new Database(this).isAnalyticsCreated(userDetails.getUser_id())) {
                //register analytics
                new Database(this).createStudentAnalytics(userDetails.getUser_id(), 0, 0, 0, 0, 0, 0);
            }

            //update ui
//            Intent homeIntent = new Intent(this, StudentDashboard.class);
//            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(homeIntent);
//            finish();
//            overridePendingTransition(R.anim.slide_left, R.anim.slide_out_left);

            Intent questionnaireIntent = new Intent(this, Questionnaire.class);
            questionnaireIntent.putExtra("userId", userDetails.getUser_id());
            questionnaireIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(questionnaireIntent);
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