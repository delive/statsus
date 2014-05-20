package com.statsus.core.persistence;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.statsus.core.metadata.Stat;
import com.statsus.core.persistence.DatabaseSchema.StatContentSql;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * All communication to the local persistence should go through here.
 *
 * @author john.wright
 * @since May 19 2014
 */
public class LocalPersistenceManager {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static void addStat(final Stat stat,
                               final int statVal,
                               final int uid,
                               final Date date,
                               final Context context) {
        final LocalMysqlHelper dbHelper = new LocalMysqlHelper(context);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        if (db == null) {
            throw new IllegalStateException("could not get writeable db to local storage");
        }

        ContentValues values = new ContentValues();
        values.put(StatContentSql.COLUMN_NAME_STAT_ID, stat.getSid());
        values.put(StatContentSql.COLUMN_NAME_USER_ID, uid);
        values.put(StatContentSql.COLUMN_NAME_DATE, DATE_FORMAT.format(date));
        values.put(StatContentSql.COLUMN_NAME_VAL, statVal);

        db.insert(StatContentSql.TABLE_NAME, "null", values);

    }
}
