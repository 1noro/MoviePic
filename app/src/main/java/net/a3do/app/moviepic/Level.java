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
import java.io.IOException;

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
        } catch (IOException|JSONException e) {e.printStackTrace();}


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
                if (createdCacheLevelDir) Log.d("Carpeta creada", String.valueOf(cacheDir));
                File imageFile = new File(cacheDir, filename);
                Log.d("CARGA DESDE CACHE", imageFile + " cargada desde cache");
                try {
                    FileInputStream fileInputStream = new FileInputStream(imageFile);
                    imageBitmap = BitmapFactory.decodeStream(fileInputStream);
                    fileInputStream.close();
                } catch (FileNotFoundException e) {
                    Log.d("FileNotFoundException", "La imagen no se ha podido cargar desde la CACHÉ por algún motivo, asignando la imagen 404.");
//                    e.printStackTrace();
                    imageBitmap = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.frame_error404);
                }
                this.frameList[i] = imageBitmap;
            } catch (Exception e) {
                Log.d("Excepción", "Fallo al obtener los frames desde internet o desde la cache.");
                e.printStackTrace();
            }
        }

    }

    public Bitmap[] getFrameList() {
        return frameList;
    }

    public boolean checkTitle(@NotNull ViewPager mViewPager, String titleToCheck) throws JSONException {
        boolean out = false;
        if (GameUtils.checkTitle(levelArray.getJSONObject(mViewPager.getCurrentItem()).getJSONArray("title"), titleToCheck)) {
            levelStatusArray.put(mViewPager.getCurrentItem());
            GameUtils.writeToFile(context, this.fileStatusDir, levelStatusArray.toString());
            out = true;
        }
        return out;
    }

    public boolean checkFrameAnswered(int frameId) throws JSONException {
        boolean out = false;
        if (GameUtils.findIntInJSONArray(levelStatusArray, frameId)) out = true;
        return out;
    }

    public String getFrameTitleByLang(int frameId, String langId) throws JSONException {
        String out = "Null.";
        String originalTitle = "Null.";
        JSONArray frameTitles = levelArray.getJSONObject(frameId).getJSONArray("title");
        String originalLangId = levelArray.getJSONObject(frameId).getString("lang");
        for (int i = 0; i < frameTitles.length(); i++) {
            JSONObject frameTitleObject = frameTitles.getJSONObject(i);
            if (frameTitleObject.getString("lang").equals(langId)) {
                out = frameTitleObject.getString("value");
                break;
            }
        }
        for (int i = 0; i < frameTitles.length(); i++) {
            JSONObject frameTitleObject = frameTitles.getJSONObject(i);
            if (frameTitleObject.getString("lang").equals(originalLangId)) {
                originalTitle = frameTitleObject.getString("value");
                break;
            }
        }
        if (out.equals("Null.")) {
            out = originalTitle;
            Log.d("Error obteniendo titulo", "El titulo no se encuentra en el idioma por defecto, asignando título original: " + originalTitle + ".");
        }
        if (out.equals("Null.")) {
            String primerTituloEncontrado = frameTitles.getJSONObject(0).getString("value");
            out = primerTituloEncontrado;
            Log.d("Error obt. titulo ori.", "El titulo original no se encuentra. Asignando el primer título encontrado: " + primerTituloEncontrado + ".");
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
