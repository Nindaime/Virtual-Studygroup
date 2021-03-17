package esw.peeplotech.studygroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.List;

import esw.peeplotech.studygroup.adapters.ChatAdapter;
import esw.peeplotech.studygroup.databases.Database;
import esw.peeplotech.studygroup.models.Chat;
import esw.peeplotech.studygroup.models.Module;
import esw.peeplotech.studygroup.models.User;
import esw.peeplotech.studygroup.util.Common;
import io.paperdb.Paper;

public class ModuleDetail extends AppCompatActivity {

    //widgets
    private ImageView backBtn;
    private VideoView videoPlayer;
    private TextView moduleTitle, moduleDescription, readMoreText;
    private RecyclerView discussionRecycler;
    private EditText chatEdt;
    private ImageView sendBtn;

    //values
    private String moduleId;
    private Module currentModule;
    private User currentUser;

    //data
    private List<Chat> chatList = new ArrayList<>();
    private ChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_detail);

        //values
        moduleId = getIntent().getStringExtra(Common.INTENT_MODULE);
        currentModule = new Database(this).getModuleDetails(moduleId);
        currentUser = Paper.book().read(Common.CURRENT_USER);

        //widgets
        backBtn = findViewById(R.id.backBtn);
        videoPlayer = findViewById(R.id.videoPlayer);
        moduleTitle = findViewById(R.id.moduleTitle);
        moduleDescription = findViewById(R.id.moduleDescription);
        readMoreText = findViewById(R.id.readMoreText);
        discussionRecycler = findViewById(R.id.discussionRecycler);
        chatEdt = findViewById(R.id.chatEdt);
        sendBtn = findViewById(R.id.sendBtn);

        //initialize
        initialize();
    }

    private void initialize() {

        //back
        backBtn.setOnClickListener(v -> onBackPressed());

        //video
        initVideoLecture();

        //module data
        moduleTitle.setText(currentModule.getModule_title());
        moduleDescription.setText(currentModule.getModule_desc());
        readMoreText.setOnClickListener(v -> {
            if (readMoreText.getText().toString().equals("Read More")){

                readMoreText.setText("Read Less");
                moduleDescription.setMaxLines(Integer.MAX_VALUE);
                moduleDescription.setEllipsize(null);

            } else {

                readMoreText.setText("Read More");
                moduleDescription.setMaxLines(3);
                moduleDescription.setEllipsize(TextUtils.TruncateAt.END);

            }
        });

        //load messages
        loadDiscussion();

        //send discussion
        sendBtn.setOnClickListener(v -> sendDiscussion());

    }

    private void initVideoLecture() {

        videoPlayer.setMediaController(new MediaController(this));
        videoPlayer.setOnCompletionListener(mp -> {

        });

        videoPlayer.setVideoURI(Uri.parse(currentModule.getModule_file()));
        videoPlayer.start();

    }

    private void loadDiscussion() {

        //chat id
        String chatTable = moduleId + Common.MODULE_CHAT;

        //add all
        chatList.addAll(new Database(this).getMessages(chatTable));

        //init recycler
        discussionRecycler.setHasFixedSize(true);
        discussionRecycler.setLayoutManager(new LinearLayoutManager(this));

        //disable default animator
        ((SimpleItemAnimator) discussionRecycler.getItemAnimator()).setSupportsChangeAnimations(false);

        //adapter
        adapter = new ChatAdapter(this, this, chatList, chatTable);
        discussionRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //scroll to bottom
        discussionRecycler.scrollToPosition(chatList.size() - 1);

    }

    private void sendDiscussion() {

        //chat id
        String chatTable = moduleId + Common.MODULE_CHAT;

        //extract string
        String theMsg = chatEdt.getText().toString().trim();

        if (!TextUtils.isEmpty(theMsg)){

            //int msg id
            int msgId = chatList.size() + 1;

            //add message
            new Database(this).sendMessage(chatTable, currentUser.getUser_id(), theMsg, Common.NOT_APPROVED);

            //add to list
            chatList.add(new Chat(msgId, currentUser.getUser_id(), theMsg, Common.NOT_APPROVED));
            adapter.notifyDataSetChanged();

            //sroll to
            discussionRecycler.scrollToPosition(chatList.size() - 1);

            //clear edt
            chatEdt.setText("");

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        videoPlayer.pause();
        videoPlayer.stopPlayback();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_right, R.anim.slide_out_right);
    }
}