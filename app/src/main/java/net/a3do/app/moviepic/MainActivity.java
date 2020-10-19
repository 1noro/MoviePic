package net.a3do.app.moviepic;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int levelConfigVersion = 1;
        String defaultPreferences = "{\"levelConfigVersion\" : " + levelConfigVersion + "}";

        super.onCreate(savedInstanceState);

        // Quitamos la barra del titulo
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Mostramos el layout
        setContentView(R.layout.activity_main);

        // Mostrar el código del idioma al iniciar
//        Toast.makeText(this, Locale.getDefault().getLanguage(), Toast.LENGTH_SHORT).show();

        try {
            setLevelButton(R.id.buttonLevel0, 0, R.raw.level0, "[]");
            setLevelButton(R.id.buttonLevel1, 1, R.raw.level1, "[0]");
            setLevelButton(R.id.buttonLevel2, 2, R.raw.level2, "[1]");
            setLevelButton(R.id.buttonLevel3, 3, R.raw.level3, "[1]");
            setLevelButton(R.id.buttonLevel4, 4, R.raw.level4, "[2, 3]");

            JSONObject preferences = GameUtils.readAppPreferences(this, defaultPreferences);
            // si ha cambiado la disposición de los niveles, se borra el progreso
            if (preferences.getInt("levelConfigVersion") != levelConfigVersion) {
                GameUtils.resetAllLevelStatus(this);
                preferences.remove("levelConfigVersion");
                preferences.put("levelConfigVersion", levelConfigVersion);
                GameUtils.saveAppPreferences(this, preferences);
                showResetProgressAlert();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void showResetProgressAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("¡Cambio importante!");
        alertDialog.setMessage("Lo sentimos mucho. Nos vemos en la necesidad de reiniciar tu progreso, ya que la disposición de los niveles ha cambiado.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void downloadAndLoadLevel(int levelId, int levelFileJSONId) {
        LoadLevelThread loading = new LoadLevelThread("loading1", this, levelId, levelFileJSONId);
        loading.start();
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
                            MainActivity.this.downloadAndLoadLevel(this.parameters.getInt("levelId"), this.parameters.getInt("levelFileJSONId"));
                        } else {
                            String msg = getResources().getString(R.string.levelNotAccesible1) + " " + (unlockNextLevel - previousCorrectAnswers) + " " + getResources().getString(R.string.levelNotAccesible2);
                            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        MainActivity.this.downloadAndLoadLevel(this.parameters.getInt("levelId"), this.parameters.getInt("levelFileJSONId"));
                    }
                } catch (JSONException e) {e.printStackTrace();}
            }
        });
    }

}