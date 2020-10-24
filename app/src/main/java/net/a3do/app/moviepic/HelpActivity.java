package net.a3do.app.moviepic;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;



public class HelpActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideTitleBar();
        setContentView(R.layout.activity_help);
    }

    public void hideTitleBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
    }

    public void deleteProgress(View view) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        GameUtils.resetAllLevelStatus(HelpActivity.this);
                        Toast.makeText(HelpActivity.this, HelpActivity.this.getResources().getString(R.string.resetProgressToast), Toast.LENGTH_SHORT).show();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(this.getResources().getString(R.string.resetProgressMsg));
        builder.setPositiveButton(this.getResources().getString(R.string.yes), dialogClickListener);
        builder.setNegativeButton(this.getResources().getString(R.string.no), dialogClickListener);
        builder.show();
    }
}
