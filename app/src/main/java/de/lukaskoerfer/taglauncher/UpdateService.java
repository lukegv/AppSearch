package de.lukaskoerfer.taglauncher;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.lukaskoerfer.taglauncher.db.DbHelper;
import de.lukaskoerfer.taglauncher.model.InstalledApp;

public class UpdateService extends Service {

    public UpdateService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        UpdateTask Task = new ServiceUpdateTask();
        Task.execute();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class ServiceUpdateTask extends UpdateTask {

        public ServiceUpdateTask() {
            super(UpdateService.this);
        }

        @Override
        protected void onCancelled() {
            UpdateService.this.stopSelf();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            UpdateService.this.stopSelf();
        }
    }
}
