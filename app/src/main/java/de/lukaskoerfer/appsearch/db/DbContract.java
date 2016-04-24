package de.lukaskoerfer.appsearch.db;

import android.provider.BaseColumns;

/**
 * Created by Koerfer on 07.04.2016.
 */
public final class DbContract {

    public static class AppTable implements BaseColumns {
        public static final String TABLE_APPS = "apps";

        public static final String COLUMN_PACKAGE_NAME = "package_name";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_LOCAL_TAGS = "local_tags";
        public static final String COLUMN_GLOBAL_TAGS = "global_tags";
        public static final String COLUMN_REMAINS = "remains";

        public static String SqlCreate() {
            return "CREATE TABLE " + TABLE_APPS + " ( " +
                    COLUMN_PACKAGE_NAME + " TEXT PRIMARY KEY, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_LOCAL_TAGS + " TEXT DEFAULT '', " +
                    COLUMN_GLOBAL_TAGS + " TEXT DEFAULT '', " +
                    COLUMN_REMAINS + " INTEGER DEFAULT 1);";
        }

        public static String SqlDelete() {
            return "DROP TABLE " + TABLE_APPS + " IF EXISTS;";
        }
    }

}
