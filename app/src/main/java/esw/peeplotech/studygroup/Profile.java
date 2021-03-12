package esw.peeplotech.studygroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import esw.peeplotech.studygroup.databases.Database;
import esw.peeplotech.studygroup.models.User;
import esw.peeplotech.studygroup.util.Common;
import io.paperdb.Paper;

public class Profile extends AppCompatActivity {

    //widgets
    private ImageView backBtn;
    private TextView userId;
    private RoundedImageView userAvatar;
    private TextView changeAvatar;
    private EditText firstName, lastName;
    private RelativeLayout updateBtn, logoutBtn;
    private TextView updateText;
    private AVLoadingIndicatorView updateProgress;

    //values
    private User currentUser;

    //image upload
    private static final int VERIFY_PERMISSIONS_REQUEST = 757;
    private static final int GALLERY_REQUEST_CODE = 665;
    private Uri imageUri;
    private String imageLink = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //value
        currentUser = Paper.book().read(Common.CURRENT_USER);

        //widgets
        backBtn = findViewById(R.id.backBtn);
        userId = findViewById(R.id.userId);
        userAvatar = findViewById(R.id.userAvatar);
        changeAvatar = findViewById(R.id.changeAvatar);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        updateBtn = findViewById(R.id.updateBtn);
        updateText = findViewById(R.id.updateText);
        updateProgress = findViewById(R.id.updateProgress);
        logoutBtn = findViewById(R.id.logoutBtn);

        //init
        initialize();

    }

    private void initialize() {

        //set profile
        setProfile();

        //back
        backBtn.setOnClickListener(v -> {
            onBackPressed();
        });

        //change
        changeAvatar.setOnClickListener(v -> {

            //check permissions
            if (ContextCompat.checkSelfPermission(Profile.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(Profile.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(Profile.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

                showGallery();

            } else {

                ActivityCompat.requestPermissions(Profile.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, VERIFY_PERMISSIONS_REQUEST);

            }
            
        });

        //save update
        updateBtn.setOnClickListener(v -> updateProfile());

        //logout
        logoutBtn.setOnClickListener(v -> {

            //destroy local db
            Paper.book().destroy();

            //go to sign in
            Intent logoutIntent = new Intent(this, OnBoardingChoice.class);
            logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(logoutIntent);
            finish();
            overridePendingTransition(R.anim.slide_right, R.anim.slide_out_right);

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
                        .start(Profile.this);
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
                File avatarFile = new File(avatarFolder.getAbsolutePath(), currentUser.getUser_id() + ".jpg");

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

    private void updateProfile() {

        //strings
        String theFirst = firstName.getText().toString().trim();
        String theLast = lastName.getText().toString().trim();

        //start loading
        updateBtn.setEnabled(false);
        firstName.setEnabled(false);
        lastName.setEnabled(false);
        changeAvatar.setEnabled(false);
        updateText.setVisibility(View.GONE);
        updateProgress.setVisibility(View.VISIBLE);

        //update user
        new Database(this).updateUserProfile(currentUser.getUser_id(), theFirst, theLast, imageLink);

        //get latest details
        getUpdate(new Database(this).getUserDetails(currentUser.getUser_id()));


    }

    private void getUpdate(User userDetails) {

        //stop loading
        updateBtn.setEnabled(true);
        firstName.setEnabled(true);
        lastName.setEnabled(true);
        changeAvatar.setEnabled(true);
        updateProgress.setVisibility(View.GONE);
        updateText.setVisibility(View.VISIBLE);

        //store local details
        Paper.book().write(Common.CURRENT_USER, userDetails);

        //finish
        onBackPressed();

    }

    private void setProfile() {

        //set data
        userId.setText("@" + currentUser.getUser_id());
        firstName.setText(currentUser.getFirst_name());
        lastName.setText(currentUser.getLast_name());


        if (!TextUtils.isEmpty(currentUser.getUser_avatar())){
            imageLink = currentUser.getUser_avatar();

            Picasso.get()
                    .load(imageLink)
                    .config(Bitmap.Config.RGB_565)
                    .fit().centerCrop()
                    .into(userAvatar);

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_right, R.anim.slide_out_right);
    }
}