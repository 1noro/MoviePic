package net.a3do.app.moviepic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

public class FrameDownloaderThread extends Thread {

    private File cacheDir;
    private String filename;
    private URL imageurl;

    FrameDownloaderThread(String name, int levelId, File cacheDir, String filename) {
        super(name);
        this.cacheDir = cacheDir;
        this.filename = filename;
        try {
            this.imageurl = new URL("https://storage.rat.la/moviepic/v2/level" + levelId + "/" + this.filename);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        File imageFile = new File(this.cacheDir, this.filename);
        if (!imageFile.exists()) {
            Log.d("CARGA DESDE URL", imageFile + " cargada desde URL");
            try {
                Bitmap imageBitmap = BitmapFactory.decodeStream(imageurl.openConnection().getInputStream());
                FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (FileNotFoundException | UnknownHostException e) {
                Log.d("Internet Error", "La imagen no se ha podido cargar desde la URL por algún motivo.");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
