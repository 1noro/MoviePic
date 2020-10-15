package net.a3do.app.moviepic;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;

import java.util.Locale;
import java.util.Objects;

public class LevelActivity extends AppCompatActivity {

    Level levelObj;
    ViewPager mViewPager;
    EditText titleAnswerBox;
    TextView titleAnswered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // para que android permita cargas desde webs en el main thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        hideTitleBar();

        // Obtenemos la información del nivel desde el MainActivity
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        levelObj = new Level(this, bundle.getInt("levelId", 0), bundle.getInt("levelItemJsonId", 0));

        final FloatingActionButton buttonAnswer = findViewById(R.id.buttonAnswer);
        titleAnswerBox = findViewById(R.id.titleAnswerBox);
        titleAnswerBox.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    buttonAnswer.performClick();
                    return true;
                }
                return false;
            }
        });

        titleAnswered = findViewById(R.id.titleAnswered);
        titleAnswered.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        titleAnswered.setSelected(true);
        titleAnswered.setSingleLine(true);

        generateViewPager();
        try {
            changeAnswerUIIfFrameIsAnswered();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void hideTitleBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_level);
    }

    public void setFABAnswered() {
        FloatingActionButton buttonAnswer = findViewById(R.id.buttonAnswer);
        buttonAnswer.setImageResource(R.drawable.check);
        buttonAnswer.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.green)));
    }

    public void setFABNotAnswered() {
        FloatingActionButton buttonAnswer = findViewById(R.id.buttonAnswer);
        buttonAnswer.setImageResource(R.drawable.question);
        buttonAnswer.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.red)));
    }

    public void changeAnswerUIIfFrameIsAnswered() throws JSONException {
        boolean isFrameAnswered = levelObj.checkFrameAnswered(mViewPager.getCurrentItem());
        if (isFrameAnswered) {
            titleAnswered.setText(levelObj.getFrameTitleByLang(mViewPager.getCurrentItem(), Locale.getDefault().getLanguage()));
            setFABAnswered();
            titleAnswered.setVisibility(View.VISIBLE);
            titleAnswerBox.setVisibility(View.GONE);
        } else {
            titleAnswerBox.clearFocus();
            String lastFailedAnswer = levelObj.getLastFailedAnswer(mViewPager.getCurrentItem());
            titleAnswerBox.setText(lastFailedAnswer);
            if (lastFailedAnswer.equals("")) {
                titleAnswerBox.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.textColor));
            } else {
                titleAnswerBox.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
            }
            setFABNotAnswered();
            titleAnswerBox.setVisibility(View.VISIBLE);
            titleAnswered.setVisibility(View.GONE);
        }
    }

    public void generateViewPager() {
        mViewPager = findViewById(R.id.viewPagerMain);
        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(LevelActivity.this, levelObj.getFrameList());
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            public void onPageSelected(int position) {
                hideKeyboard(LevelActivity.this);
                try {
                    changeAnswerUIIfFrameIsAnswered();
                } catch (JSONException e) {e.printStackTrace();}
            }
        });
    }

    public void answerFAB(View view) throws JSONException {
        boolean isFrameAnswered = levelObj.checkFrameAnswered(mViewPager.getCurrentItem());
        if (!isFrameAnswered) {
            String titleToCheck = titleAnswerBox.getText().toString();
            boolean isThereAMatch = levelObj.checkTitle(mViewPager, titleToCheck);
            if (isThereAMatch) {
                titleAnswered.setText(levelObj.getFrameTitleByLang(mViewPager.getCurrentItem(), Locale.getDefault().getLanguage()));
                titleAnswerBox.clearFocus();
                hideKeyboard(this);
                setFABAnswered();
                titleAnswered.setVisibility(View.VISIBLE);
                titleAnswerBox.setVisibility(View.GONE);
            } else {
                levelObj.setLastFailedAnswer(mViewPager.getCurrentItem(), titleToCheck);
                titleAnswerBox.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
            }
        }
    }

}
