package com.statsus.core.persistence;

import com.statsus.core.persistence.DatabaseSchema.StatCategorySql;
import com.statsus.core.persistence.DatabaseSchema.StatContentSql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author john.wright
 * @since May 19 2014
 */
class LocalMysqlHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 6;
    private static final String DATABASE_NAME = "statsus.db";

    LocalMysqlHelper(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        db.execSQL(StatContentSql.SQL_CREATE_TABLE);
        db.execSQL(StatCategorySql.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        db.execSQL(StatContentSql.SQL_DELETE_TABLE);
        db.execSQL(StatCategorySql.SQL_DELETE_TABLE);
        onCreate(db);
    }
}
