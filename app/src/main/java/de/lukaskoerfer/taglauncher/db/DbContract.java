package de.lukaskoerfer.taglauncher.db;

import android.provider.BaseColumns;

/**
 * Created by Koerfer on 07.04.2016.
 */
public final class DbContract {

    public static class AppTable implements BaseColumns {
        public static final String TABLE_APPS = "apps";
        public static final String COLUMN_PACKAGE_NAME = "package_name";
        public static final String COLUMN_NAME = "name";

        public static String SqlCreate() {
            return "CREATE TABLE " + TABLE_APPS + " (" + COLUMN_PACKAGE_NAME + " TEXT," + COLUMN_NAME + " TEXT);";
        }

        public static String SqlDelete() {
            return "DROP TABLE " + TABLE_APPS + " IF EXISTS;";
        }
    }

    public static class TagTable implements BaseColumns {
        public static final String TABLE_TAGS = "tags";
        public static final String COLUMN_PACKAGE_NAME = "package_name";
        public static final String COLUMN_TAG = "tag";

        public static String SqlCreate() {
            return "CREATE TABLE " + TABLE_TAGS + " (" + COLUMN_PACKAGE_NAME + " TEXT," + COLUMN_TAG + " TEXT);";
        }

        public static String SqlDelete() {
            return "DROP TABLE " + TABLE_TAGS + " IF EXISTS;";
        }

    }

}
