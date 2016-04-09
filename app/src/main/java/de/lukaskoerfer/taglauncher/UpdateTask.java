package de.lukaskoerfer.taglauncher;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import de.lukaskoerfer.taglauncher.db.DbHelper;
import de.lukaskoerfer.taglauncher.model.InstalledApp;

/**
 * Created by Koerfer on 09.04.2016.
 */
public abstract class UpdateTask extends AsyncTask<Void, Void, Void> {

    private Context context;

    public UpdateTask(Context c) {
        this.context = c;
    }

    @Override
    protected Void doInBackground(Void... params) {
        PackageManager pm = this.context.getPackageManager();
        List<ApplicationInfo> applicationInfos = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        List<InstalledApp> installedApps = new ArrayList<>();
        for (ApplicationInfo appInfo : applicationInfos) {
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                String appName = appInfo.loadLabel(pm).toString();
                installedApps.add(new InstalledApp(appInfo.packageName, appName));
            }
        }
        DbHelper.Instance(this.context).updateInstalledApps(installedApps);
        return null;
    }
}
