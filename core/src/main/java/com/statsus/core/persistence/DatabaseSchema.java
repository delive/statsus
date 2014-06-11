package com.statsus.core.persistence;

import android.provider.BaseColumns;

/**
 * DB schema for statsus tables including create and delete sql.
 *
 * @author john.wright
 * @since May 19 2014
 */
public class DatabaseSchema {
    private DatabaseSchema() {
    }

    public static abstract class StatContentSql
            implements BaseColumns {

        public static final String TABLE_NAME = "statcontent";

        public static final String COLUMN_NAME_STAT_ID = "sid";
        public static final String COLUMN_NAME_USER_ID = "uid";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_VAL = "val";
        public static final String COLUMN_NAME_NOTE = "note";

        // primary key sid,uid,date

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + StatContentSql.TABLE_NAME + " (" +
                        StatContentSql._ID + " INTEGER," +
                        StatContentSql.COLUMN_NAME_STAT_ID + " INTEGER, " +
                        StatContentSql.COLUMN_NAME_USER_ID + " BIGINT, " +
                        StatContentSql.COLUMN_NAME_DATE + " DATE, " +
                        StatContentSql.COLUMN_NAME_VAL + " INTEGER," +
                        StatContentSql.COLUMN_NAME_NOTE + " TEXT," +
                        " PRIMARY KEY ("
                        + StatContentSql.COLUMN_NAME_STAT_ID + ", "
                        + StatContentSql.COLUMN_NAME_USER_ID + ", "
                        + StatContentSql.COLUMN_NAME_DATE + "))";

        public static final String SQL_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + StatContentSql.TABLE_NAME;
    }

    public static abstract class StatCategorySql
            implements BaseColumns {

        public static final String TABLE_NAME = "statcategory";

        public static final String COLUMN_NAME_STAT_ID = "sid";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + StatCategorySql.TABLE_NAME + " (" +
                        StatCategorySql._ID + " INTEGER PRIMARY KEY," +
                        StatCategorySql.COLUMN_NAME_STAT_ID + " INTEGER" +
                        " )";

        public static final String SQL_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + StatCategorySql.TABLE_NAME;
    }
}
