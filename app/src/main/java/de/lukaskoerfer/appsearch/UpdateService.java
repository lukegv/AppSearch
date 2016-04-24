package de.lukaskoerfer.appsearch;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

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
