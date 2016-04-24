package de.lukaskoerfer.appsearch.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;

import de.lukaskoerfer.appsearch.MainActivity;
import de.lukaskoerfer.appsearch.R;
import de.lukaskoerfer.appsearch.db.DbHelper;
import de.lukaskoerfer.appsearch.model.InstalledApp;

/**
 * Created by Koerfer on 22.04.2016.
 */
public class DialogBuilder {

    public static AlertDialog BuildIndexAppsDialog(final MainActivity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("No apps found");
        builder.setMessage("Do you want to index the apps now?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.runUpdate();
            }
        });
        builder.setNegativeButton("No", null);
        return builder.create();
    }

    public static AlertDialog BuildModifyTagsDialog(final MainActivity activity, final InstalledApp app) {
        View dialogView = activity.getLayoutInflater().inflate(R.layout.dialog_tags, null);
        final EditText etTag[] = new EditText[5];
        etTag[0] = (EditText) dialogView.findViewById(R.id.etTag1);
        etTag[1] = (EditText) dialogView.findViewById(R.id.etTag2);
        etTag[2] = (EditText) dialogView.findViewById(R.id.etTag3);
        etTag[3] = (EditText) dialogView.findViewById(R.id.etTag4);
        etTag[4] = (EditText) dialogView.findViewById(R.id.etTag5);
        for (int i = 0; i < app.localTags.size(); i++) {
            etTag[i].setText(app.localTags.get(i));
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Modify tags for " + app.getAppName());
        builder.setView(dialogView);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                app.localTags.clear();
                for (int i = 0; i < 5; i++) {
                    String tag = etTag[i].getText().toString();
                    if (tag.length() > 0) {
                        app.localTags.add(tag);
                    }
                }
                DbHelper.Instance(activity).saveLocalTags(app);
                activity.loadApps();
            }
        });
        builder.setNegativeButton("Cancel", null);
        return builder.create();
    }

}
