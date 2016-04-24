package de.lukaskoerfer.appsearch.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.lukaskoerfer.appsearch.model.InstalledApp;

/**
 * Created by Koerfer on 05.04.2016.
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "InstalledApps.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public static DbHelper Instance(Context context) {
        return new DbHelper(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DbContract.AppTable.SqlCreate());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new SQLiteException("No upgrade method implemented");
    }

    public void updateInstalledApps(List<InstalledApp> apps) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (InstalledApp app : apps) {
            db.execSQL("UPDATE " + DbContract.AppTable.TABLE_APPS +
                    " SET " + DbContract.AppTable.COLUMN_REMAINS + " = 1" +
                    " WHERE " + DbContract.AppTable.COLUMN_PACKAGE_NAME + " = '" + app.getPackageName() + "';" );
            db.execSQL("INSERT OR IGNORE INTO " + DbContract.AppTable.TABLE_APPS +
                    " (" + DbContract.AppTable.COLUMN_PACKAGE_NAME + ", " + DbContract.AppTable.COLUMN_NAME + ")" +
                    " VALUES ('" + app.getPackageName() + "', '" + app.getAppName() + "');");
        }
        db.execSQL("DELETE FROM " + DbContract.AppTable.TABLE_APPS +
                " WHERE " + DbContract.AppTable.COLUMN_REMAINS + " = 0;");
        db.execSQL("UPDATE " + DbContract.AppTable.TABLE_APPS +
                " SET " + DbContract.AppTable.COLUMN_REMAINS + " = 0;");
    }

    public void saveLocalTags(InstalledApp app) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + DbContract.AppTable.TABLE_APPS +
                " SET " + DbContract.AppTable.COLUMN_LOCAL_TAGS + " = '" + DbHelper.packTags(app.localTags) +
                "' WHERE " + DbContract.AppTable.COLUMN_PACKAGE_NAME + " = '" + app.getPackageName() + "';");
    }

    public List<InstalledApp> getInstalledApps() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.query(false, DbContract.AppTable.TABLE_APPS, null, null, null, null, null, null, null);
        List<InstalledApp> installedApps = new ArrayList<>();
        boolean resultLeft = result.moveToFirst();
        while (resultLeft) {
            String packageName = result.getString(result.getColumnIndex(DbContract.AppTable.COLUMN_PACKAGE_NAME));
            String appName = result.getString(result.getColumnIndex(DbContract.AppTable.COLUMN_NAME));
            InstalledApp newApp = new InstalledApp(packageName, appName);
            String localTagString = result.getString(result.getColumnIndex(DbContract.AppTable.COLUMN_LOCAL_TAGS));
            newApp.localTags = DbHelper.extractTags(localTagString);
            String globalTagString = result.getString(result.getColumnIndex(DbContract.AppTable.COLUMN_GLOBAL_TAGS));
            newApp.globalTags = DbHelper.extractTags(globalTagString);
            installedApps.add(newApp);
            resultLeft = result.moveToNext();
        }
        result.close();
        return installedApps;
    }

    private static String packTags(List<String> tags) {
        String tagString = "";
        for (String tag : tags) {
            tagString += "," + tag;
        }
        return tagString.substring(1);
    }

    private static List<String> extractTags(String tagString) {
        return new ArrayList<String>(Arrays.asList(tagString.split(",")));
    }

}
