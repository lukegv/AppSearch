package de.lukaskoerfer.appsearch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuItem;
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

import java.util.ArrayList;
import java.util.List;

import de.lukaskoerfer.appsearch.db.DbHelper;
import de.lukaskoerfer.appsearch.model.InstalledApp;
import de.lukaskoerfer.appsearch.ui.DialogBuilder;
import de.lukaskoerfer.appsearch.ui.IconLoader;
import de.lukaskoerfer.appsearch.ui.LoadingImage;

public class MainActivity extends AppCompatActivity {

    private IconLoader iconLoader;

    private EditText etSearch;
    private ListView lvApps;
    private InstalledAppAdapter lvAppsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize the asynchronous icon loader
        this.iconLoader = new IconLoader(this);

        // set the layout to the activity
        this.setContentView(R.layout.activity_main);
        // get the user interface elements from the view
        this.etSearch = (EditText) this.findViewById(R.id.etSearch);
        this.lvApps = (ListView) this.findViewById(R.id.lvApps);
        // create an adapter for the list
        this.lvAppsAdapter = new InstalledAppAdapter();
        this.lvApps.setAdapter(this.lvAppsAdapter);
        // register app list for long-click menu
        this.registerForContextMenu(this.lvApps);

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

        this.loadApps();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.etSearch.setText("");
    }

    public void loadApps() {
        List<InstalledApp> allApps = DbHelper.Instance(this).getInstalledApps();
        if (allApps.size() > 0) {
            this.iconLoader.loadIcons(allApps);
            this.lvAppsAdapter.setAppList(allApps);
        } else {
            DialogBuilder.BuildIndexAppsDialog(this).show();
        }
    }

    public void runUpdate() {
        ActivityUpdateTask updateTask = new ActivityUpdateTask();
        updateTask.execute();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        this.getMenuInflater().inflate(R.menu.context_app, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        InstalledApp app = this.lvAppsAdapter.getApp(info.position);
        switch (item.getItemId()) {
            case R.id.itemAddCurrent:
                if (app.localTags.size() <= 5) {
                    app.localTags.add(this.etSearch.getText().toString());
                    DbHelper.Instance(this).saveLocalTags(app);
                    this.loadApps();
                } else {

                }
                return true;
            case R.id.itemModifyTags:
                DialogBuilder.BuildModifyTagsDialog(this, app).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private class InstalledAppAdapter extends BaseAdapter implements Filterable {

        private String constraint;

        private List<InstalledApp> installedApps;
        private List<InstalledApp> shownApps;

        public InstalledAppAdapter() {
            this.constraint = "";
            this.installedApps = new ArrayList<>();
            this.shownApps = new ArrayList<>();
        }

        public void setAppList(List<InstalledApp> apps) {
            this.installedApps = apps;
            this.getFilter().filter(this.constraint);
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
            // get the presented app element
            InstalledApp app = this.shownApps.get(position);
            // inflate layout if the view is not recycled
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.element_app, null);
            }
            // find all views in the layout
            TextView tvAppName = (TextView) convertView.findViewById(R.id.tvAppName);
            ImageView ivAppIcon = (ImageView) convertView.findViewById(R.id.imgAppLogo);
            ProgressBar pgrLoading = (ProgressBar) convertView.findViewById(R.id.pgrLoading);
            // assign app name and switch to icon loading animation
            tvAppName.setText(app.getAppName());
            LoadingImage loadingImage = new LoadingImage(ivAppIcon, pgrLoading);
            loadingImage.switchToLoading();
            // request the icon of the app for later assignment
            MainActivity.this.iconLoader.requestIcon(loadingImage, app.getPackageName());
            return convertView;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence newConstraint) {
                    InstalledAppAdapter.this.constraint = newConstraint.toString();
                    List<InstalledApp> filteredApps = new ArrayList<>();
                    for (InstalledApp app : InstalledAppAdapter.this.installedApps) {
                        if (app.matchFactor(InstalledAppAdapter.this.constraint) > 0.5) {
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
                    InstalledAppAdapter.this.shownApps = (List<InstalledApp>) results.values;
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
