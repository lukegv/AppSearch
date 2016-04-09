package de.lukaskoerfer.taglauncher.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.lukaskoerfer.taglauncher.db.DbContract;
import de.lukaskoerfer.taglauncher.model.InstalledApp;

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
        db.execSQL(DbContract.TagTable.SqlCreate());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new SQLiteException("No upgrade method implemented");
    }

    public void updateInstalledApps(List<InstalledApp> apps) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (InstalledApp app : apps) {
            ContentValues values = new ContentValues();
            values.put(DbContract.AppTable.COLUMN_PACKAGE_NAME, app.getPackageName());
            values.put(DbContract.AppTable.COLUMN_NAME, app.getAppName());
            db.insert(DbContract.AppTable.TABLE_APPS, null, values);
        }
    }

    public List<InstalledApp> getAllInstalledApps() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.query(false, DbContract.AppTable.TABLE_APPS, null, null, null, null, null, null, null);
        List<InstalledApp> installedApps = new ArrayList<>();
        boolean resultLeft = result.moveToFirst();
        while (resultLeft) {
            String packageName = result.getString(result.getColumnIndex(DbContract.AppTable.COLUMN_PACKAGE_NAME));
            String appName = result.getString(result.getColumnIndex(DbContract.AppTable.COLUMN_NAME));
            Log.d("Tag Launcher", appName);
            installedApps.add(new InstalledApp(packageName, appName));
            resultLeft = result.moveToNext();
        }
        return installedApps;
    }

}
