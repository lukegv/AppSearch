package de.lukaskoerfer.appsearch.ui;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.lukaskoerfer.appsearch.R;
import de.lukaskoerfer.appsearch.model.InstalledApp;

/**
 * Created by Koerfer on 08.04.2016.
 */
public class IconLoader {

    private Context context;

    private Map<String, Drawable> cachedIcons;
    private BiMap<LoadingImage, String> requests;

    public IconLoader(Context c) {
        this.context = c;
        this.cachedIcons = new HashMap<>();
        this.requests = HashBiMap.create();
    }

    public void requestIcon(LoadingImage loadingImage, String packageName) {
        if (this.cachedIcons.containsKey(packageName)) {
            loadingImage.switchToImage(this.cachedIcons.get(packageName));
        } else {
            requests.forcePut(loadingImage, packageName);
        }
    }

    public void loadIcons(List<InstalledApp> apps) {
        InstalledApp[] appArray = apps.toArray(new InstalledApp[apps.size()]);
        IconLoadTask task = new IconLoadTask();
        task.execute(appArray);
    }

    private class LoadResult {
        public String packageName;
        public Drawable icon;

        public LoadResult(String name, Drawable i) {
            this.packageName = name;
            this.icon = i;
        }
    }

    private class IconLoadTask extends AsyncTask<InstalledApp, LoadResult, Void> {

        @Override
        protected Void doInBackground(InstalledApp... apps) {
            for (InstalledApp app : apps) {
                Drawable icon;
                try {
                    icon = IconLoader.this.context.getPackageManager().getApplicationIcon(app.getPackageName());
                } catch (PackageManager.NameNotFoundException nnfex) {
                    icon = ContextCompat.getDrawable(IconLoader.this.context, R.mipmap.app_icon);
                }
                this.publishProgress(new LoadResult(app.getPackageName(), icon));
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(LoadResult... results) {
            for (LoadResult result : results) {
                if (IconLoader.this.requests.containsValue(result.packageName)) {
                    IconLoader.this.requests.inverse().remove(result.packageName).switchToImage(result.icon);
                }
                IconLoader.this.cachedIcons.put(result.packageName, result.icon);
            }
            super.onProgressUpdate(results);
        }
    }

}
