package de.lukaskoerfer.taglauncher;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.menu.MenuView;
import android.widget.ImageView;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;

import de.lukaskoerfer.taglauncher.model.InstalledApp;

/**
 * Created by Koerfer on 08.04.2016.
 */
public class IconLoader {

    private Context context;

    private Map<String, Drawable> cachedIcons;
    private Map<String, ImageView> requests;

    public IconLoader(Context c) {
        this.context = c;
        this.cachedIcons = new HashMap<>();
        this.requests = new HashMap<>();
    }

    public void requestIcon(String packageName, ImageView imageView) {
        if (this.cachedIcons.containsKey(packageName)) {
            imageView.setImageDrawable(this.cachedIcons.get(packageName));
        } else {
            requests.put(packageName, imageView);
        }
    }

    public void loadIcons(List<InstalledApp> apps) {
        InstalledApp[] appArray = apps.toArray(new InstalledApp[apps.size()]);
        IconLoadTask task = new IconLoadTask();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, appArray);
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
                    icon = ContextCompat.getDrawable(IconLoader.this.context, R.mipmap.ic_launcher);
                }
                this.publishProgress(new LoadResult(app.getPackageName(), icon));
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(LoadResult... results) {
            for (LoadResult result : results) {
                if (IconLoader.this.requests.containsKey(result.packageName)) {
                    IconLoader.this.requests.get(result.packageName).setImageDrawable(result.icon);
                }
                IconLoader.this.cachedIcons.put(result.packageName, result.icon);
            }
            super.onProgressUpdate(results);
        }
    }

}
