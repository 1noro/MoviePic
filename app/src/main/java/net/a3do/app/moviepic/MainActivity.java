package net.a3do.app.moviepic;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private int unlockNextLevel = 20;
//    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // para que android permita cargas desde webs en el main thread
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);

        // Quitamos la barra del titulo
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Mostramos el layout
        setContentView(R.layout.activity_main);

        // Mostrar el código del idioma al iniciar
//        Toast.makeText(this, Locale.getDefault().getLanguage(), Toast.LENGTH_SHORT).show();

        // Creamos el dialogo de carga desde caché
//        loading = GameUtils.createLoading(this);

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
//        loading.dismiss();
    }

//    public void downloadLevelFrames(int levelId, int levelFileJSONId) throws IOException, JSONException {
//        JSONArray levelArray = new JSONArray(GameUtils.readJsonFile(this, levelFileJSONId));
//
//        File cacheDir = new File(this.getCacheDir(), "level" + levelId);
//        boolean createdCacheLevelDir = cacheDir.mkdirs();
//        if (createdCacheLevelDir) Log.d("CARPETA CREADA", String.valueOf(cacheDir));
//
//        ExecutorService es = Executors.newCachedThreadPool();
//        for (int i = 0; i < levelArray.length(); i++) {
//            String filename = levelArray.getJSONObject(i).getString("frame") + ".jpg";
//            es.execute(new FrameDownloaderThread("fdw" + i, levelId, cacheDir, filename));
//        }
//        es.shutdown();
//        try {
//            boolean finished = es.awaitTermination(10, TimeUnit.SECONDS);
//            if (finished) Log.d("Info de la descarga", "Se han terminado de ejecutar todos los hilos de descarga.");
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

    public void executeIntent(int levelId, int levelFileJSONId) throws IOException, JSONException {
//        loading.show();

//        new Thread(new Runnable() {
//            public void run() {
//                try {
//                    MainActivity.this.downloadLevelFrames(levelId, levelFileJSONId);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                loading.dismiss();
//            }
//        }).start();

        LoadLevelThread loading = new LoadLevelThread("loading1", this, levelId, levelFileJSONId);
        loading.start();


//        Intent intentLevel = new Intent(getApplicationContext(), LevelActivity.class);
//        intentLevel.putExtra("levelId", levelId);
//        intentLevel.putExtra("levelItemJsonId", levelFileJSONId);
//        startActivity(intentLevel);
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
                            MainActivity.this.executeIntent(this.parameters.getInt("levelId"), this.parameters.getInt("levelFileJSONId"));
                        } else {
                            String msg = getResources().getString(R.string.levelNotAccesible1) + " " + (unlockNextLevel - previousCorrectAnswers) + " " + getResources().getString(R.string.levelNotAccesible2);
                            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        MainActivity.this.executeIntent(this.parameters.getInt("levelId"), this.parameters.getInt("levelFileJSONId"));
                    }
                } catch (JSONException|IOException e) {e.printStackTrace();}
            }
        });
    }

}