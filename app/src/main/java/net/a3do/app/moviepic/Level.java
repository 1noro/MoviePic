package net.a3do.app.moviepic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.viewpager.widget.ViewPager;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.UnknownHostException;

public class Level {

    private Context context;
    private JSONArray levelArray;
    private JSONArray levelStatusArray;
    private Bitmap[] frameList;
    private String fileStatusDir;
    private String[] lastFailedAnswersArray;

    public Level(Context context, int levelId, int levelItemJsonId) {
        this.context = context;
        this.fileStatusDir = "levelStatus" + levelId + ".json";

        try {
            this.levelArray = new JSONArray(GameUtils.readJsonFile(this.context, levelItemJsonId));
            this.levelStatusArray = new JSONArray(GameUtils.readLevelStatusFile(context, this.fileStatusDir));
//            Toast.makeText(context, "fileStatusDir: " + this.fileStatusDir, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.d("##### EXCPETION","readJsonFile || new JSONArray(data)");
            e.printStackTrace();
        }

        assert this.levelArray != null;
        this.frameList = new Bitmap[this.levelArray.length()];
        this.lastFailedAnswersArray = new String[this.levelArray.length()];

        for (int i = 0; i < this.frameList.length; i++) {
            this.lastFailedAnswersArray[i] = "";
            // descargamos todas las imagenes y las guardamos en cache
            try {
                String filename = this.levelArray.getJSONObject(i).getString("frame") + ".jpg";
                Bitmap imageBitmap;
                File cacheDir = new File(this.context.getCacheDir(), "level" + levelId);
                boolean createdCacheLevelDir = cacheDir.mkdirs();
                if (createdCacheLevelDir) Log.d("#DIRECTORIO CREADO#", String.valueOf(cacheDir));
                File imageFile = new File(cacheDir, filename);
                if (!imageFile.exists()) {
                    Log.d("#IMAGEN DESDE URL#", imageFile + " cargada desde URL");
                    URL imageurl = new URL("https://storage.rat.la/moviepic/level" + levelId + "/" + filename);
                    try {
                        imageBitmap = BitmapFactory.decodeStream(imageurl.openConnection().getInputStream());
                        FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
                        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    } catch (FileNotFoundException|UnknownHostException e) {
                        Log.d("#FileNotFoundException#", "La imagen no se ha podido cargar desde la URL por algún motivo, asignando la imagen 404.");
                        e.printStackTrace();
                        imageBitmap = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.frame_error404);
                    }
                } else {
                    Log.d("#IMAGEN DESDE CACHE#", imageFile + " cargada desde cache");
                    FileInputStream fileInputStream = null;
                    try {
                        fileInputStream = new FileInputStream(imageFile);
                        imageBitmap = BitmapFactory.decodeStream(fileInputStream);
                    } catch (FileNotFoundException e) {
                        Log.d("#FileNotFoundException#", "La imagen no se ha podido cargar desde la CACHÉ por algún motivo, asignando la imagen 404.");
                        e.printStackTrace();
                        imageBitmap = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.frame_error404);
                    }
                    assert fileInputStream != null;
                    fileInputStream.close();
                }
                this.frameList[i] = imageBitmap;
            } catch (Exception e) {
                Log.d("##### EXCPETION", "FALLO AL OBTENER LOS FRAMES DE INTERNET O DESDE LA CACHE");
                e.printStackTrace();
            }
        }

    }

    public Bitmap[] getFrameList() {
        return frameList;
    }

    public boolean checkTitle(@NotNull ViewPager mViewPager, String titleToCheck) {
        boolean out = false;
        try {
            if (GameUtils.checkTitle(levelArray.getJSONObject(mViewPager.getCurrentItem()).getJSONArray("title"), titleToCheck)) {
                levelStatusArray.put(mViewPager.getCurrentItem());
                GameUtils.writeToFile(context, this.fileStatusDir, levelStatusArray.toString());
                out = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return out;
    }

    public boolean checkFrameAnswered(int frameId) {
        boolean out = false;
        if (GameUtils.findIntInJSONArray(levelStatusArray, frameId)) out = true;
        return out;
    }

    public String getFrameTitleByLang(int frameId, String langId) {
        String out = "Null.";
        try {
            JSONArray frameTitles = levelArray.getJSONObject(frameId).getJSONArray("title");
            for (int i = 0; i < frameTitles.length(); i++) {
                JSONObject frameTitleObject = frameTitles.getJSONObject(i);
//                Log.d("$$$COMPARATIVA$$$", "¿ " + frameTitleObject.getString("lang") + " == " + langId + " ?");
                if (frameTitleObject.getString("lang").equals(langId)) {
                    out = frameTitleObject.getString("value");
                    break;
                }
            }
            if (out.equals("Null.")) out = frameTitles.getJSONObject(0).getString("value");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return out;
    }

    public void setLastFailedAnswer(int frameId, String failedAnswer) {
        this.lastFailedAnswersArray[frameId] = failedAnswer;
    }

    public String getLastFailedAnswer(int frameId) {
        return this.lastFailedAnswersArray[frameId];
    }

}
