package esw.peeplotech.studygroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

import esw.peeplotech.studygroup.util.Common;

public class OnBoardingChoice extends AppCompatActivity {
    
    //widgets
    private ImageView backButton;
    private Button registerBtn, loginBtn;

    //static values
    public static final int PERMISSION_REQUEST_CODE = 234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding_choice);
        
        //widgets
        backButton = findViewById(R.id.backButton);
        registerBtn = findViewById(R.id.registerBtn);
        loginBtn = findViewById(R.id.loginBtn);
        
        //init
        initialize();
        
    }

    private void initialize() {

        //check permission
        checkAppPermissions();

        //back
        backButton.setOnClickListener(v -> {
            onBackPressed();
        });

        //register
        registerBtn.setOnClickListener(v -> {

            startActivity(new Intent(this, Registration.class));
            overridePendingTransition(R.anim.slide_left, R.anim.slide_out_left);

        });

        //login
        loginBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, Login.class));
            overridePendingTransition(R.anim.slide_left, R.anim.slide_out_left);
        });

    }

    private void checkAppPermissions() {

        //check app mobile sdk version
        if (Build.VERSION.SDK_INT >= 23){

            //check permissions
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

                //check file status
                checkFile();

            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

            }

        } else {

            //check file status
            checkFile();

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //check file status
                checkFile();

            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Storage permissions needed for this app to function. Please go to setting to give permissions.");
                builder.setCancelable(false);

                builder.setPositiveButton(
                        "OK",
                        (dialog, id) -> {
                            dialog.cancel();

                            //open permissions setting
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                            finish();
                        });

                builder.setNegativeButton(
                        "NO",
                        (dialog, id) -> {
                            dialog.cancel();
                            finish();
                        });

                AlertDialog alert = builder.create();
                alert.show();

            }
        }

    }

    private void checkFile() {

        File dir = new File(Environment.getExternalStorageDirectory(), Common.BASE_FOLDER);
        if (!dir.exists()) {
            dir.mkdir();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_right, R.anim.slide_out_right);
    }
}