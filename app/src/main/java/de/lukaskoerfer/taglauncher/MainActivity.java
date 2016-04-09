package de.lukaskoerfer.taglauncher;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import de.lukaskoerfer.taglauncher.db.DbHelper;
import de.lukaskoerfer.taglauncher.model.InstalledApp;

public class MainActivity extends AppCompatActivity {

    private IconLoader iconLoader;

    private EditText etSearch;
    private ListView lvApps;
    private InstalledAppAdapter lvAppsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.iconLoader = new IconLoader(this);

        this.setContentView(R.layout.activity_main);
        this.etSearch = (EditText) this.findViewById(R.id.etSearch);
        this.lvApps = (ListView) this.findViewById(R.id.lvApps);
        this.lvAppsAdapter = new InstalledAppAdapter();
        this.lvApps.setAdapter(this.lvAppsAdapter);

        this.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                MainActivity.this.lvAppsAdapter.getFilter().filter(s.toString());
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

        this.loadApps();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.etSearch.setText("");
    }

    private void loadApps() {
        List<InstalledApp> allApps = DbHelper.Instance(this).getAllInstalledApps();
        if (allApps.size() > 0) {
            this.iconLoader.loadIcons(allApps);
            this.lvAppsAdapter.setAppList(allApps);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No apps found");
            builder.setMessage("Do you want to index the apps now?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MainActivity.this.runUpdate();
                }
            });
            builder.setNegativeButton("No", null);
            builder.create().show();
        }
    }

    private void runUpdate() {
        ActivityUpdateTask updateTask = new ActivityUpdateTask();
        updateTask.execute();
    }

    private class InstalledAppAdapter extends BaseAdapter implements Filterable {

        private String lastConstraint;

        private List<InstalledApp> installedApps;
        private List<InstalledApp> shownApps;

        public InstalledAppAdapter() {
            this.lastConstraint = "";
            this.installedApps = new ArrayList<>();
            this.shownApps = new ArrayList<>();
        }

        public void setAppList(List<InstalledApp> apps) {
            this.installedApps = apps;
            this.getFilter().filter(this.lastConstraint);
        }

        @Override
        public int getCount() {
            return this.shownApps.size();
        }

        @Override
        public Object getItem(int position) {
            return this.shownApps.get(position);
        }

        public InstalledApp getApp(int position) {
            return this.shownApps.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            InstalledApp app = this.shownApps.get(position);
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.element_app, null);
            }
            TextView tvAppName = (TextView) convertView.findViewById(R.id.tvAppName);
            ImageView ivAppIcon = (ImageView) convertView.findViewById(R.id.imgAppLogo);
            ProgressBar pgrLoading = (ProgressBar) convertView.findViewById(R.id.pgrLoading);
            LoadingImage loadingImage = new LoadingImage(ivAppIcon, pgrLoading);
            loadingImage.switchToLoading();
            tvAppName.setText(app.getAppName());
            MainActivity.this.iconLoader.requestIcon(loadingImage, app.getPackageName());
            return convertView;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    InstalledAppAdapter.this.lastConstraint = constraint.toString();
                    List<InstalledApp> filteredApps = new ArrayList<>();
                    for (InstalledApp app : InstalledAppAdapter.this.installedApps) {
                        if (StringUtils.containsIgnoreCase(app.getAppName(), constraint)) {
                            filteredApps.add(app);
                        }
                    }
                    FilterResults results = new FilterResults();
                    results.count = filteredApps.size();
                    results.values = filteredApps;
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    List<InstalledApp> filteredApps = (List<InstalledApp>) results.values;
                    InstalledAppAdapter.this.shownApps = filteredApps;
                    InstalledAppAdapter.this.notifyDataSetChanged();
                }
            };
        }
    }

    private class ActivityUpdateTask extends UpdateTask {

        private ProgressDialog dialog;

        public ActivityUpdateTask() {
            super(MainActivity.this);
        }

        @Override
        protected void onPreExecute() {
            this.dialog = ProgressDialog.show(MainActivity.this, "Indexing apps", "Please wait ...", true);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            this.dialog.dismiss();
            MainActivity.this.loadApps();
            super.onPostExecute(aVoid);
        }
    }
}
