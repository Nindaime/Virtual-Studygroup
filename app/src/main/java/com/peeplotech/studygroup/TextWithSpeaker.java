package com.peeplotech.studygroup;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Dimension;
import androidx.annotation.RequiresApi;

import com.peeplotech.studygroup.models.User;
import com.peeplotech.studygroup.util.AppPreference;
import com.peeplotech.studygroup.util.Common;

import io.paperdb.Paper;


/**
 * TODO: document your custom view class.
 */
public class TextWithSpeaker extends FrameLayout {
    private String text; // TODO: use a default from R.string...
    private int textColor = Color.RED; // TODO: use a default from R.color...
    private int textStyle = 0;
    private int maxLines = 3;
    private int textSize = 15;

    private TextView textView;
    private TextToSpeechConverter converter;

    private ImageButton speakerButton;

    private void initView() {
        inflate(getContext(), R.layout.text_with_speaker, this);
    }

    public TextWithSpeaker(Context context) {
        super(context);
        init(null, 0);
    }

    public TextWithSpeaker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public TextWithSpeaker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }


    private void init(AttributeSet attrs, int defStyle) {

        initView();

//        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        speakerButton = findViewById(R.id.speakerButton);

        converter = new TextToSpeechConverter(getContext());
        speakerButton.setOnClickListener(v -> {
            converter.speak((String) textView.getText());
        });

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.TextWithSpeaker, defStyle, 0);

        text = a.getString(
                R.styleable.TextWithSpeaker_text);

        textColor = a.getColor(
                R.styleable.TextWithSpeaker_textColor,
                textColor);

        textStyle = a.getInteger(R.styleable.TextWithSpeaker_textStyle, textStyle);
        textSize = a.getDimensionPixelSize(R.styleable.TextWithSpeaker_textSize, textSize);
        maxLines = a.getInteger(R.styleable.TextWithSpeaker_maxLines, maxLines);


        textView.setText(text);
        textView.setTextColor(textColor);
        textView.setTextSize(textSize);
        textView.setMaxLines(maxLines);
        textView.setTypeface(Typeface.DEFAULT, textStyle);
//        speakerButton.setForeground();
        checkTextLength();

        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.


        a.recycle();

        AppPreference preference = new AppPreference((Activity) this.getContext());
        User currentUser = Paper.book().read(Common.CURRENT_USER);

        Log.d("now", String.valueOf(preference.isDyslexic(""+currentUser.getUser_id())));
        if(!preference.isDyslexic(""+currentUser.getUser_id())){
            speakerButton.setVisibility(View.GONE);
        }

    }


    /**
     * Gets the example string attribute value.
     *
     * @return The example string attribute value.
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the view"s example string attribute value. In the example view, this string
     * is the text to draw.
     *
     * @param v The example string attribute value to use.
     */
    public void setText(String v) {

        text = v;

        textView.setText(v);

        checkTextLength();


    }

    private void checkTextLength(){
        int lengthOfText = textView.getText().length();

        if(lengthOfText < 1){
            speakerButton.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Gets the example color attribute value.
     *
     * @return The example color attribute value.
     */
    public int getTextColor() {
        return textColor;
    }

    /**
     * Sets the view"s example color attribute value. In the example view, this color
     * is the font color.
     *
     * @param exampleColor The example color attribute value to use.
     */
    public void setTextColor(int exampleColor) {
        textColor = exampleColor;

    }

    /**
     * Gets the example dimension attribute value.
     *
     * @return The example dimension attribute value.
     */


    /**
     * Sets the view"s example dimension attribute value. In the example view, this dimension
     * is the font size.
     *
     * @param height The example dimension attribute value to use.
     */

}