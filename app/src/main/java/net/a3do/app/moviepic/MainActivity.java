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

    private int unlockNextLevel = 1;
    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Quitamos la barra del titulo
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_main);

        // Mostrar el código del idioma al iniciar
//        Toast.makeText(this, Locale.getDefault().getLanguage(), Toast.LENGTH_SHORT).show();

        loading = GameUtils.createLoading(this);

        try {
            JSONObject parameters = new JSONObject("{\"levelId\" : 0, \"levelFileJSONId\" : " + R.raw.level0 + "}");
            Button level0 = findViewById(R.id.buttonLevel0);
            level0.setOnClickListener(new MyOnClickListener(parameters) {
                @Override
                public void onClick(View view) {
                    try {
                        loading.show();
                        Intent intentLevel = new Intent(getApplicationContext(), LevelActivity.class);
                        intentLevel.putExtra("levelId", this.parameters.getInt("levelId"));
                        intentLevel.putExtra("levelItemJsonId", this.parameters.getInt("levelFileJSONId"));
                        startActivity(intentLevel);
                    } catch (JSONException e) {e.printStackTrace();}
                }
            });

//            parameters = new JSONObject("{\"levelId\" : 1, \"levelFileJSONId\" : " + R.raw.level1 + "}");
//            Button level1 = findViewById(R.id.buttonLevel1);
//            level1.setOnClickListener(new MyOnClickListener(parameters) {
//                @Override
//                public void onClick(View view) {
//                    try {
//                        JSONArray previousLevelStatus = new JSONArray(GameUtils.readLevelStatusFile(MainActivity.this, "levelStatus0.json"));
//                        if (previousLevelStatus.length() >= 20) {
//                            loading.show();
//                            Intent intentLevel = new Intent(getApplicationContext(), LevelActivity.class);
//                            intentLevel.putExtra("levelId", this.parameters.getInt("levelId"));
//                            intentLevel.putExtra("levelItemJsonId", this.parameters.getInt("levelFileJSONId"));
//                            startActivity(intentLevel);
//                        } else {
//                            String msg = getResources().getString(R.string.levelNotAccesible1) + " " + (20 - previousLevelStatus.length()) + " " + getResources().getString(R.string.levelNotAccesible2);
//                            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
//                        }
//                    } catch (JSONException e) {e.printStackTrace();}
//                }
//            });

            setLevelButton((Button) findViewById(R.id.buttonLevel1), 1, R.raw.level1, 0);

//            parameters = new JSONObject("{\"levelId\" : 2, \"levelFileJSONId\" : " + R.raw.level2 + "}");
//            Button level2 = findViewById(R.id.buttonLevel2);
//            level2.setOnClickListener(new MyOnClickListener(parameters) {
//                @Override
//                public void onClick(View view) {
//                    try {
//                        JSONArray previousLevelStatus = new JSONArray(GameUtils.readLevelStatusFile(MainActivity.this, "levelStatus1.json"));
//                        if (previousLevelStatus.length() >= 20) {
//                            loading.show();
//                            Intent intentLevel = new Intent(getApplicationContext(), LevelActivity.class);
//                            intentLevel.putExtra("levelId", this.parameters.getInt("levelId"));
//                            intentLevel.putExtra("levelItemJsonId", this.parameters.getInt("levelFileJSONId"));
//                            startActivity(intentLevel);
//                        } else {
//                            String msg = getResources().getString(R.string.levelNotAccesible1) + " " + (20 - previousLevelStatus.length()) + " " + getResources().getString(R.string.levelNotAccesible2);
//                            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
//                        }
//                    } catch (JSONException e) {e.printStackTrace();}
//                }
//            });

            setLevelButton((Button) findViewById(R.id.buttonLevel2), 2, R.raw.level2, 1);

//            parameters = new JSONObject("{\"levelId\" : 3, \"levelFileJSONId\" : " + R.raw.level3 + "}");
//            Button level3 = findViewById(R.id.buttonLevel3);
//            level3.setOnClickListener(new MyOnClickListener(parameters) {
//                @Override
//                public void onClick(View view) {
//                    try {
//                        JSONArray previousLevelStatus = new JSONArray(GameUtils.readLevelStatusFile(MainActivity.this, "levelStatus1.json"));
//                        if (previousLevelStatus.length() >= 20) {
//                            loading.show();
//                            Intent intentLevel = new Intent(getApplicationContext(), LevelActivity.class);
//                            intentLevel.putExtra("levelId", this.parameters.getInt("levelId"));
//                            intentLevel.putExtra("levelItemJsonId", this.parameters.getInt("levelFileJSONId"));
//                            startActivity(intentLevel);
//                        } else {
//                            String msg = getResources().getString(R.string.levelNotAccesible1) + " " + (20 - previousLevelStatus.length()) + " " + getResources().getString(R.string.levelNotAccesible2);
//                            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
//                        }
//                    } catch (JSONException e) {e.printStackTrace();}
//                }
//            });

            setLevelButton((Button) findViewById(R.id.buttonLevel3), 3, R.raw.level3, 1);

            Button level4 = findViewById(R.id.buttonLevel4);
            level4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.notImplementedYet), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (JSONException e) {
            Log.d("##### EXCPETION", "jsonResponse.get || new JSONObject(...)");
            e.printStackTrace();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //View decorView = getWindow().getDecorView();
        // Hide the status bar.
        //int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        //decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        //ActionBar actionBar = getActionBar();
        //actionBar.hide();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loading.dismiss();
    }

    public void setLevelButton(Button buttonObj, int levelId, int levelFileJSONId, int previousLevelId) throws JSONException {
        JSONObject parameters = new JSONObject("{\"levelId\" : " + levelId + ", \"levelFileJSONId\" : " + levelFileJSONId + ", \"previousLevelId\" : " + previousLevelId + "}");
        buttonObj.setOnClickListener(new MyOnClickListener(parameters) {
            @Override
            public void onClick(View view) {
                int unlockNextLevel = MainActivity.this.unlockNextLevel;
                try {
                    JSONArray previousLevelStatus = new JSONArray(GameUtils.readLevelStatusFile(MainActivity.this, "levelStatus" + this.parameters.getInt("previousLevelId") + ".json"));
                    if (previousLevelStatus.length() >= unlockNextLevel) {
                        loading.show();
                        Intent intentLevel = new Intent(getApplicationContext(), LevelActivity.class);
                        intentLevel.putExtra("levelId", this.parameters.getInt("levelId"));
                        intentLevel.putExtra("levelItemJsonId", this.parameters.getInt("levelFileJSONId"));
                        startActivity(intentLevel);
                    } else {
                        String msg = getResources().getString(R.string.levelNotAccesible1) + " " + (unlockNextLevel - previousLevelStatus.length()) + " " + getResources().getString(R.string.levelNotAccesible2);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {e.printStackTrace();}
            }
        });
    }

}