package com.peeplotech.studygroup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.speech.SpeechRecognizer;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.peeplotech.studygroup.models.User;
import com.peeplotech.studygroup.util.AppPreference;
import com.peeplotech.studygroup.util.Common;

import io.paperdb.Paper;

/**
 * TODO: document your custom view class.
 */
public class EditTextWithSpeaker extends FrameLayout {
    private String text; // TODO: use a default from R.string...




    private ImageView imageView;
    private EditText editText;
    private SpeechToTextConverter converter;


    private void initView() {
        inflate(getContext(), R.layout.edit_text_with_speaker, this);
    }

    public EditTextWithSpeaker(Context context) {
        super(context);
        init(null, 0);
    }

    public EditTextWithSpeaker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);

    }

    public EditTextWithSpeaker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

        initView();
        imageView = findViewById(R.id.imageView);
        editText = findViewById(R.id.editText);


        converter = new SpeechToTextConverter(getContext(), editText);
        SpeechRecognizer recognizer = converter.getSpeechRecognizer();
        Intent speechRecognizerIntent = converter.getSpeechRecognizerIntent();

        this.setOnTouchListener((view, motionEvent) -> {

            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                Log.d("stop","Listening");
                recognizer.stopListening();

            }
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                Log.d("start","Listening");
                recognizer.startListening(speechRecognizerIntent);
            }
            return false;
        });


        Log.d("image view", imageView.toString());
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.TextWithSpeaker, defStyle, 0);

        text = a.getString(
                R.styleable.TextWithSpeaker_text);


        editText.setText(text);

        AppPreference preference = new AppPreference((Activity) this.getContext());

        User currentUser = Paper.book().read(Common.CURRENT_USER);
        if(!preference.isDyslexic(""+currentUser.getUser_id())){
            imageView.setVisibility(View.GONE);
        }

        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.


        a.recycle();



    }


    /**
     * Gets the example string attribute value.
     *
     * @return The example string attribute value.
     */
    public String getText() {


        return editText.getText().toString();
    }

    /**
     * Sets the view"s example string attribute value. In the example view, this string
     * is the text to draw.
     *
     * @param v The example string attribute value to use.
     */
    public void setText(String v) {

        text = v;

    }


}