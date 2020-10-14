package net.a3do.app.moviepic;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class GameUtils {

    @NotNull
    public static String readJsonFile(@NotNull Context context, int id) throws Exception {
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

    public static boolean checkTitle(@NotNull JSONArray titleArray, String titleToCheck) {
        boolean out = false;
        titleToCheck = titleToCheck.toLowerCase().trim().replaceAll("[^a-zA-Z0-9 ]", "");
        try {
            for (int i = 0; i < titleArray.length(); i++) {
                String realTitle = titleArray.getJSONObject(i).getString("value").toLowerCase().trim().replaceAll("[^a-zA-Z0-9 ]", "");
                 Log.d("$$$COMPARATIVA$$$", "¿ " + titleToCheck + " == " + realTitle + " ?");
                if (realTitle.equals(titleToCheck)) {
                    out = true;
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return out;
    }

    public static String readLevelStatusFile(@NotNull Context context, String fileDir) {
        String ret = "";
        try {
            while (true) {
                InputStream inputStream = context.openFileInput(fileDir);
                if (inputStream != null) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString = "";
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

    public static void writeToFile(@NotNull Context context, String fileDir, String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileDir, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static boolean findIntInJSONArray(@NotNull JSONArray jArray, int number) {
        boolean out = false;
        for (int i = 0; i < jArray.length(); i++) {
            try {
                if (jArray.getInt(i) == number) {
                    out = true;
                    break;
                }
            } catch (JSONException e) {
                Log.d("Error:", "El JSONArray no contiene un Int en esta posición.");
                e.printStackTrace();
            }
        }
        return out;
    }

//    public static void showToastOnTop(Context context, String text) {
//        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
//        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 40);
//        toast.show();
//    }

    public static ProgressDialog createLoading(Context context) {
        ProgressDialog progress = new ProgressDialog(context);
        progress.setTitle(context.getResources().getString(R.string.loading));
        progress.setMessage(context.getResources().getString(R.string.loadingLevelMsg));
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
//        progress.show();
        return progress;
    }

}
