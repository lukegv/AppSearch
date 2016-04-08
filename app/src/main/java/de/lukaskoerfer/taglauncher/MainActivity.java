package de.lukaskoerfer.taglauncher;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.lukaskoerfer.taglauncher.db.DbHelper;
import de.lukaskoerfer.taglauncher.model.InstalledApp;

public class MainActivity extends AppCompatActivity {

    private PackageManager packageManager;
    private IconLoader iconLoader;

    private EditText txtSearch;
    private ListView lvApps;
    private InstalledAppAdapter lvAppsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.packageManager = this.getPackageManager();
        this.iconLoader = new IconLoader(this);

        this.setContentView(R.layout.activity_main);
        this.txtSearch = (EditText) this.findViewById(R.id.txtSearch);
        this.lvApps = (ListView) this.findViewById(R.id.lvApps);
        this.lvAppsAdapter = new InstalledAppAdapter();
        this.lvApps.setAdapter(this.lvAppsAdapter);

        this.txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("Tag Launcher", s.toString());
            }
        });

        this.lvApps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InstalledApp app = MainActivity.this.lvAppsAdapter.getApp(position);
                Intent launchIntent = MainActivity.this.getPackageManager().getLaunchIntentForPackage(app.getPackageName());
                MainActivity.this.startActivity(launchIntent);
            }
        });

        this.lvApps.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return false;
            }
        });

        List<InstalledApp> allApps = (new DbHelper(this)).getAllInstalledApps();
        this.iconLoader.loadIcons(allApps);
        this.lvAppsAdapter.updateAppList(allApps);

        // fill with dummy apps
        //List<InstalledApp> dummyApps = new ArrayList<>();
        //dummyApps.add(new InstalledApp("de.lukaskoerfer.cdjandroid2", "CDJ for Android"));
        //this.lvAppsAdapter.updateAppList(dummyApps);
    }

    private class InstalledAppAdapter extends BaseAdapter {

        private List<InstalledApp> installedApps;
        private Drawable defaultIcon;

        public InstalledAppAdapter() {
            this.installedApps = new ArrayList<>();
            this.defaultIcon = ContextCompat.getDrawable(MainActivity.this, R.mipmap.ic_launcher);
        }

        public void updateAppList(List<InstalledApp> apps) {
            this.installedApps = apps;
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return this.installedApps.size();
        }

        @Override
        public Object getItem(int position) {
            return this.installedApps.get(position);
        }

        public InstalledApp getApp(int position) {
            return this.installedApps.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            InstalledApp app = this.installedApps.get(position);
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.element_app, null);
            }
            TextView tvAppName = (TextView) convertView.findViewById(R.id.tvAppName);
            ImageView ivAppIcon = (ImageView) convertView.findViewById(R.id.imgAppLogo);
            tvAppName.setText(app.getAppName());
            ivAppIcon.setImageDrawable(this.defaultIcon);
            MainActivity.this.iconLoader.requestIcon(app.getPackageName(), ivAppIcon);
            return convertView;
        }
    }
}
