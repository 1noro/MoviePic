package net.a3do.app.moviepic;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private int unlockNextLevel = 2;
    private ProgressDialog loading;

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

    public void downloadLevelFrames(int levelId, int levelFileJSONId) throws IOException, JSONException {
        JSONArray levelArray = new JSONArray(GameUtils.readJsonFile(this, levelFileJSONId));
        for (int i = 0; i < levelArray.length(); i++) {
            String filename = levelArray.getJSONObject(i).getString("frame") + ".jpg";
            Bitmap imageBitmap;
            File cacheDir = new File(this.getCacheDir(), "level" + levelId);
            boolean createdCacheLevelDir = cacheDir.mkdirs();
            if (createdCacheLevelDir) Log.d("CARPETA CREADA", String.valueOf(cacheDir));
            File imageFile = new File(cacheDir, filename);
            if (!imageFile.exists()) {
                Log.d("CARGA DESDE URL", imageFile + " cargada desde URL");
                URL imageurl = new URL("https://storage.rat.la/moviepic/level" + levelId + "/" + filename);
                try {
                    imageBitmap = BitmapFactory.decodeStream(imageurl.openConnection().getInputStream());
                    FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (FileNotFoundException | UnknownHostException e) {
                    Log.d("FileNotFoundException", "La imagen no se ha podido cargar desde la URL por algún motivo.");
                    e.printStackTrace();
                }
            }
        }
    }

    public void downloadLevelFrames2(int levelId, int levelFileJSONId) throws IOException, JSONException {
        JSONArray levelArray = new JSONArray(GameUtils.readJsonFile(this, levelFileJSONId));

        File cacheDir = new File(this.getCacheDir(), "level" + levelId);
        boolean createdCacheLevelDir = cacheDir.mkdirs();
        if (createdCacheLevelDir) Log.d("CARPETA CREADA", String.valueOf(cacheDir));

        ExecutorService es = Executors.newCachedThreadPool();
        for (int i = 0; i < levelArray.length(); i++) {
            String filename = levelArray.getJSONObject(i).getString("frame") + ".jpg";
            es.execute(new FrameDownloaderThread("fdw" + i, levelId, cacheDir, filename));
        }
        es.shutdown();
        try {
            boolean finished = es.awaitTermination(10, TimeUnit.SECONDS);
            if (finished) Log.d("Info de la descarga", "Se han terminado de ejecutar todos los hilos de descarga.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void executeIntent(int levelId, int levelFileJSONId) throws IOException, JSONException {
        loading.show();
        MainActivity.this.downloadLevelFrames2(levelId, levelFileJSONId);

        Intent intentLevel = new Intent(getApplicationContext(), LevelActivity.class);
        intentLevel.putExtra("levelId", levelId);
        intentLevel.putExtra("levelItemJsonId", levelFileJSONId);
        startActivity(intentLevel);
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