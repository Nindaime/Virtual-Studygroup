package com.peeplotech.studygroup.student;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.peeplotech.studygroup.R;
import com.peeplotech.studygroup.adapters.SearchAdapter;
import com.peeplotech.studygroup.databases.Database;
import com.peeplotech.studygroup.models.Course;
import com.peeplotech.studygroup.models.User;
import com.peeplotech.studygroup.util.Common;
import io.paperdb.Paper;

public class SubscribeToCourse extends AppCompatActivity {

    //widgets
    private ImageView backBtn;
    private EditText searchEdt;
    private RecyclerView courseRecycler;

    //data
    private SearchAdapter adapter;
    private List<Course> courseList = new ArrayList<>();

    //values
    private User currentUser;

    //timer
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe_to_course);

        //value
        currentUser = Paper.book().read(Common.CURRENT_USER);

        //widgets
        backBtn = findViewById(R.id.backBtn);
        searchEdt = findViewById(R.id.searchEdt);
        courseRecycler = findViewById(R.id.courseRecycler);

        //init
        initialize();
    }

    private void initialize() {

        //back
        backBtn.setOnClickListener(v -> onBackPressed());
        
        //load all courses
        loadAllCourses();

        //attach listener to text
        searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (timer != null){
                    timer.cancel();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().trim().isEmpty()){
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            new Handler(Looper.getMainLooper()).post(() -> {

                                searchQueriedCourse(s.toString());

                            });
                        }
                    }, 1000);
                } else {
                    loadAllCourses();
                }
            }
        });

    }

    private void searchQueriedCourse(String query) {

        //clear list
        courseList.clear();

        //init recycler
        courseRecycler.setHasFixedSize(true);
        courseRecycler.setLayoutManager(new LinearLayoutManager(this));

        //init list
        courseList = new Database(this).getQueriedCourses(query);

        //adapter
        adapter = new SearchAdapter(this, this, courseList);
        courseRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    private void loadAllCourses() {

        //clear list
        courseList.clear();

        //init recycler
        courseRecycler.setHasFixedSize(true);
        courseRecycler.setLayoutManager(new LinearLayoutManager(this));

        //init list
        courseList = new Database(this).getAllCourses();

        //adapter
        adapter = new SearchAdapter(this, this, courseList);
        courseRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_right, R.anim.slide_out_right);
    }
}