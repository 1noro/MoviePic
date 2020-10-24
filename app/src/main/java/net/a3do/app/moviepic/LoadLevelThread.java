package net.a3do.app.moviepic;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;

import org.json.JSONException;

import java.io.IOException;

public class LoadLevelThread extends Thread {

    private Context context;
    private int levelId;
    private int levelFileJSONId;
    private AlertDialog loadingDialog;

    @SuppressLint("InflateParams")
    public LoadLevelThread(String name, Context context, int levelId, int levelFileJSONId) {
        super(name);
        this.context = context;
        this.levelId = levelId;
        this.levelFileJSONId = levelFileJSONId;
////        this.progressDialog = new ProgressDialog(context);
////        this.progressDialog.setTitle(context.getResources().getString(R.string.loading));
////        this.progressDialog.setMessage(context.getResources().getString(R.string.loadingLevelMsg));
////        this.progressDialog.setCancelable(false); // disable dismiss by tapping outside of the dialog
////        this.progressDialog.show();
//        this.progressDialog = GameUtils.getDialogProgressBar(context).create();
//        this.progressDialog.show();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Get the layout inflater
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_loading, null));
        this.loadingDialog = builder.create();
        this.loadingDialog.show();
    }

    @Override
    public void run() {
        try {
            GameUtils.downloadLevelFrames(this.context, this.levelId, this.levelFileJSONId);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        Intent intentLevel = new Intent(context, LevelActivity.class);
        intentLevel.putExtra("levelId", levelId);
        intentLevel.putExtra("levelItemJsonId", levelFileJSONId);
        context.startActivity(intentLevel);
        loadingDialog.dismiss();
    }
}
