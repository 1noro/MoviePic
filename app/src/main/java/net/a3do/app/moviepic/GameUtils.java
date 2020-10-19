package net.a3do.app.moviepic;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GameUtils {

    public static void showResetProgressAlert(Context context) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
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

    @NotNull
    public static String readJsonFile(@NotNull Context context, int id) throws IOException {
        InputStream is = context.getResources().openRawResource(id);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
            is.close();
        }
        return writer.toString();
    }

    public static String readStringFromInputStream(InputStream inputStream) throws IOException {
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
            inputStream.close();
        }
        return writer.toString();
    }

    public static JSONObject readAppPreferences(@NotNull Context context, String defaultPreferences) throws JSONException {
        JSONObject out = null;
        File preferencesFile = new File(context.getFilesDir(), "preferences.json");
        try {
            FileInputStream fileInputStream = new FileInputStream(preferencesFile);
            out = new JSONObject(readStringFromInputStream(fileInputStream));
        } catch (FileNotFoundException e) {
            Log.d("FileNotFoundException", "No existe preferences.json, guardadndo uno por defecto.");
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(preferencesFile);
                fileOutputStream.write(defaultPreferences.getBytes());
                fileOutputStream.flush();
                fileOutputStream.close();
//                GameUtils.resetAllLevelStatus(context);
//                GameUtils.showResetProgressAlert(context);
            } catch (IOException ex) {
                Log.d("IOException", "El archivo preferences.json no se ha podido guardar con los valores por defecto.");
                ex.printStackTrace();
            }
            out = new JSONObject(defaultPreferences);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out;
    }

    public static void saveAppPreferences(Context context, JSONObject newPreferences) {
        File preferencesFile = new File(context.getFilesDir(), "preferences.json");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(preferencesFile);
            fileOutputStream.write(newPreferences.toString().getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            Log.d("IOException", "El archivo preferences.json no se ha podido guardar con los nuevos valores.");
            e.printStackTrace();
        }
    }

    public static void resetAllLevelStatus(Context context) {
        File dir = context.getFilesDir();
        for ( File file : Objects.requireNonNull(dir.listFiles())) {
            // levelStatusN.json
            Log.d("COMPARATIVA", file.getName() + ": ¿ '" + file.getName().substring(0, 11) + "' == 'levelStatus' ?");
            if (file.isFile() && file.getName().substring(0, 11).equals("levelStatus")) {
                boolean result = file.delete();
                if (result) {Log.d("Archivo eliminado", file.getAbsolutePath());}
            }
        }
    }

    public static String normalizeText(String txt) {
        txt = txt.trim();
        txt = txt.toLowerCase();
        txt = txt.replaceAll("á", "a");
        txt = txt.replaceAll("à", "a");
        txt = txt.replaceAll("ä", "a");
        txt = txt.replaceAll("é", "e");
        txt = txt.replaceAll("è", "e");
        txt = txt.replaceAll("ë", "e");
        txt = txt.replaceAll("í", "i");
        txt = txt.replaceAll("ì", "i");
        txt = txt.replaceAll("ï", "i");
        txt = txt.replaceAll("ó", "o");
        txt = txt.replaceAll("ò", "o");
        txt = txt.replaceAll("ö", "o");
        txt = txt.replaceAll("ú", "u");
        txt = txt.replaceAll("ù", "u");
        txt = txt.replaceAll("ü", "u");
        txt = txt.replaceAll("ñ", "n");
        txt = txt.replaceAll("[^a-zA-Z0-9 ]", "");
        return txt;
    }

    public static boolean checkTitle(@NotNull JSONArray titleArray, String titleToCheck) throws JSONException {
        boolean out = false;
        titleToCheck = normalizeText(titleToCheck);
        for (int i = 0; i < titleArray.length(); i++) {
            String realTitle = normalizeText(titleArray.getJSONObject(i).getString("value"));
            if (realTitle.equals(titleToCheck)) {
                Log.d("COMPARATIVA", "¿ '" + titleToCheck + "' == '" + realTitle + "' ?: Verdadero");
                out = true;
                break;
            } else {
                Log.d("COMPARATIVA", "¿ '" + titleToCheck + "' == '" + realTitle + "' ?: Falso");
            }
        }
        return out;
    }

    public static String readLevelStatusFile(@NotNull Context context, String fileDir) {
        String ret;
        try {
            while (true) {
                InputStream inputStream = context.openFileInput(fileDir);
                if (inputStream != null) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString;
                    StringBuilder stringBuilder = new StringBuilder();

                    while ((receiveString = bufferedReader.readLine()) != null) {
                        stringBuilder.append("\n").append(receiveString);
                    }

                    inputStream.close();
                    ret = stringBuilder.toString();
                } else {
                    ret = "[]";
                }
                break;
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
            writeEmptyLevelStatusFile(context, fileDir);
            ret = readLevelStatusFile(context, fileDir);
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
            ret = "[]";
        }

        return ret;
    }

    public static void writeEmptyLevelStatusFile(@NotNull Context context, String fileDir) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileDir, Context.MODE_PRIVATE));
            outputStreamWriter.write("[]");
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static void writeStringToFile(@NotNull Context context, String fileDir, String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileDir, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static boolean findIntInJSONArray(@NotNull JSONArray jArray, int number) throws JSONException {
        boolean out = false;
        for (int i = 0; i < jArray.length(); i++) {
            if (jArray.getInt(i) == number) {
                out = true;
                break;
            }
        }
        return out;
    }

//    public static void showToastOnTop(Context context, String text) {
//        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
//        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 40);
//        toast.show();
//    }

    public static void downloadLevelFrames(Context context, int levelId, int levelFileJSONId) throws IOException, JSONException {
        JSONArray levelArray = new JSONArray(GameUtils.readJsonFile(context, levelFileJSONId));

        File cacheDir = new File(context.getCacheDir(), "level" + levelId);
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

}
