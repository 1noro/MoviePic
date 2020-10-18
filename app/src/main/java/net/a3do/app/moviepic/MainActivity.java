package net.a3do.app.moviepic;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private int unlockNextLevel = 2;
    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Quitamos la barra del titulo
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        
        // Mostramos el layout
        setContentView(R.layout.activity_main);

        // Mostrar el código del idioma al iniciar
//        Toast.makeText(this, Locale.getDefault().getLanguage(), Toast.LENGTH_SHORT).show();

        loading = GameUtils.createLoading(this);

        try {
            setLevelButton(R.id.buttonLevel0, 0, R.raw.level0, "[]");
            setLevelButton(R.id.buttonLevel1, 1, R.raw.level1, "[0]");
            setLevelButton(R.id.buttonLevel2, 2, R.raw.level2, "[1]");
            setLevelButton(R.id.buttonLevel3, 3, R.raw.level3, "[1]");
            setLevelButton(R.id.buttonLevel4, 4, R.raw.level4, "[2, 3]");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loading.dismiss();
    }

    public void setLevelButton(int buttonObjId, int levelId, int levelFileJSONId, String previousLevelIds) throws JSONException {
        Button buttonObj = findViewById(buttonObjId);
        JSONObject parameters = new JSONObject("{\"levelId\" : " + levelId + ", \"levelFileJSONId\" : " + levelFileJSONId + ", \"previousLevelIds\" : " + previousLevelIds + "}");
        buttonObj.setOnClickListener(new MyOnClickListener(parameters) {
            @Override
            public void onClick(View view) {
                int unlockNextLevel = MainActivity.this.unlockNextLevel;
                try {
                    JSONArray previousLevelIds = this.parameters.getJSONArray("previousLevelIds");
                    if (previousLevelIds.length() > 0) {
                        int previousCorrectAnswers = 0;
                        for (int i = 0; i < previousLevelIds.length(); i++) {
                            JSONArray previousLevelStatus = new JSONArray(GameUtils.readLevelStatusFile(MainActivity.this, "levelStatus" + previousLevelIds.getInt(i) + ".json"));
                            previousCorrectAnswers += previousLevelStatus.length();
                        }
                        if (previousCorrectAnswers >= unlockNextLevel) {
                            loading.show();
                            Intent intentLevel = new Intent(getApplicationContext(), LevelActivity.class);
                            intentLevel.putExtra("levelId", this.parameters.getInt("levelId"));
                            intentLevel.putExtra("levelItemJsonId", this.parameters.getInt("levelFileJSONId"));
                            startActivity(intentLevel);
                        } else {
                            String msg = getResources().getString(R.string.levelNotAccesible1) + " " + (unlockNextLevel - previousCorrectAnswers) + " " + getResources().getString(R.string.levelNotAccesible2);
                            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        loading.show();
                        Intent intentLevel = new Intent(getApplicationContext(), LevelActivity.class);
                        intentLevel.putExtra("levelId", this.parameters.getInt("levelId"));
                        intentLevel.putExtra("levelItemJsonId", this.parameters.getInt("levelFileJSONId"));
                        startActivity(intentLevel);
                    }
                } catch (JSONException e) {e.printStackTrace();}
            }
        });
    }

}