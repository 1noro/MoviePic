package net.a3do.app.moviepic;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import org.json.JSONException;

import java.io.IOException;

public class LoadLevelThread extends Thread {

    private Context context;
    private int levelId;
    private int levelFileJSONId;
    private ProgressDialog progressDialog;

    public LoadLevelThread(String name, Context context, int levelId, int levelFileJSONId) {
        super(name);
        this.context = context;
        this.levelId = levelId;
        this.levelFileJSONId = levelFileJSONId;
        this.progressDialog = new ProgressDialog(context);
        this.progressDialog.setTitle(context.getResources().getString(R.string.loading));
        this.progressDialog.setMessage(context.getResources().getString(R.string.loadingLevelMsg));
        this.progressDialog.setCancelable(false); // disable dismiss by tapping outside of the dialog
        this.progressDialog.show();
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
        progressDialog.dismiss();
    }
}
