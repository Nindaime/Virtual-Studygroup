package com.peeplotech.studygroup.lecturer;

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
import android.widget.RelativeLayout;
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
import java.util.Random;

import com.peeplotech.studygroup.R;
import com.peeplotech.studygroup.databases.Database;
import com.peeplotech.studygroup.models.User;
import com.peeplotech.studygroup.util.Common;
import io.paperdb.Paper;

public class AddCourse extends AppCompatActivity {

    //widgets
    private ImageView backBtn;
    private RoundedImageView courseImg;
    private TextView changeThumbnail;
    private EditText courseTitle, courseSubTitle, courseBrief;
    private RelativeLayout createBtn;
    private TextView createText;
    private AVLoadingIndicatorView createProgress;
    
    //value
    private User currentUser;
    private String courseToken = "";

    //image upload
    private static final int VERIFY_PERMISSIONS_REQUEST = 757;
    private static final int GALLERY_REQUEST_CODE = 665;
    private Uri imageUri;
    private String imageLink = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);
        
        //value
        currentUser = Paper.book().read(Common.CURRENT_USER);

        //widgets
        backBtn = findViewById(R.id.backBtn);
        courseImg = findViewById(R.id.courseImg);
        changeThumbnail = findViewById(R.id.changeThumbnail);
        courseTitle = findViewById(R.id.courseTitle);
        courseSubTitle = findViewById(R.id.courseSubTitle);
        courseBrief = findViewById(R.id.courseBrief);
        createBtn = findViewById(R.id.createBtn);
        createText = findViewById(R.id.createText);
        createProgress = findViewById(R.id.createProgress);
        
        //init
        initialize();

    }

    private void initialize() {
        
        //generate course token
        generateCourseToken();

        //back
        backBtn.setOnClickListener(v -> onBackPressed());

        //change thumbnail
        changeThumbnail.setOnClickListener(v -> {
            //check permissions
            if (ContextCompat.checkSelfPermission(AddCourse.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(AddCourse.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(AddCourse.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

                showGallery();

            } else {

                ActivityCompat.requestPermissions(AddCourse.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, VERIFY_PERMISSIONS_REQUEST);

            }
        });

        //create course
        createBtn.setOnClickListener(v -> validateParams());
    }

    private void generateCourseToken() {

        //set token
        String tempToken = generateRandomToken();

        //get token
        if (!new Database(this).isCourseIdInUse(tempToken)){

            courseToken = tempToken;

        } else {

            generateCourseToken();

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
                        .start(AddCourse.this);
            }

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            final CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                //get data
                Uri resultUri = result.getUri();





                //avatar folder
                File coursFolder = new File(Environment.getExternalStorageDirectory(), Common.BASE_FOLDER + "/" + Common.COURSE_THUMB_FOLDER);
                if (!coursFolder.exists()){
                    coursFolder.mkdir();
                }

                //avatar file
                File thumbFile = new File(coursFolder.getAbsolutePath(), courseToken + ".jpg");

                //copy file
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = new FileInputStream(resultUri.getPath());
                    out = new FileOutputStream(thumbFile.getAbsolutePath());

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
                Uri uri = Uri.fromFile(new File(thumbFile.getAbsolutePath()));
                imageLink = uri.toString();


                //set image
                Picasso.get()
                        .load(Uri.parse(imageLink))
                        .config(Bitmap.Config.RGB_565)
                        .fit().centerCrop()
                        .into(courseImg);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    private void validateParams() {

        String theTitle = courseTitle.getText().toString().trim();
        String theSubTitle = courseSubTitle.getText().toString().trim();
        String theBrief = courseBrief.getText().toString().trim();

        //validate
        if (TextUtils.isEmpty(theTitle)){

            courseTitle.requestFocus();
            courseTitle.setError("Required");

        } else

        if (TextUtils.isEmpty(theSubTitle)){

            courseSubTitle.requestFocus();
            courseSubTitle.setError("Required");

        } else

        if (TextUtils.isEmpty(theBrief)){

            courseBrief.requestFocus();
            courseBrief.setError("Required");

        } else

        if (TextUtils.isEmpty(courseToken)){

            Toast.makeText(this, "Token Error, Please exit try again later", Toast.LENGTH_SHORT).show();

        } else {

            createCourse(theTitle, theSubTitle, theBrief);

        }

    }

    private void createCourse(String theTitle, String theSubTitle, String theBrief) {

        //start loading
        createBtn.setEnabled(false);
        changeThumbnail.setEnabled(false);
        courseTitle.setEnabled(false);
        courseSubTitle.setEnabled(false);
        courseBrief.setEnabled(false);
        createText.setVisibility(View.GONE);
        createProgress.setVisibility(View.VISIBLE);

        //create
        new Database(this).createNewCourse(courseToken, currentUser.getUser_id(), theTitle, theSubTitle, theBrief, imageLink);

        //finish
        onBackPressed();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_right, R.anim.slide_out_right);
    }
}