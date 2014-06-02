package com.statsus.core.persistence;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.statsus.core.metadata.Stat;
import com.statsus.core.persistence.DatabaseSchema.StatCategorySql;
import com.statsus.core.persistence.DatabaseSchema.StatContentSql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Facilitates all communication to the local persistence.
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
        db.close();
    }

    public static List<SqlStatContainer> getAllStats(final Context context) {
        final LocalMysqlHelper dbHelper = new LocalMysqlHelper(context);
        final List<SqlStatContainer> stats = new ArrayList<SqlStatContainer>();

        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db == null) {
            throw new IllegalStateException("could not get readable db to local storage");
        }

        final Cursor c = db.rawQuery("select * from " + StatContentSql.TABLE_NAME, null);
        if (!c.moveToFirst()) {
            return Collections.EMPTY_LIST;
        }
        while (!c.isAfterLast()) {
            final int sid = c.getInt(c.getColumnIndexOrThrow(StatContentSql.COLUMN_NAME_STAT_ID));
            final int uid = c.getInt(c.getColumnIndexOrThrow(StatContentSql.COLUMN_NAME_USER_ID));
            final String dateString = c.getString(c.getColumnIndexOrThrow(StatContentSql.COLUMN_NAME_DATE));
            final int val = c.getInt(c.getColumnIndexOrThrow(StatContentSql.COLUMN_NAME_VAL));

            final SqlStatContainer stat = new SqlStatContainer(sid, uid, dateString, val);
            stats.add(stat);
            c.moveToNext();
        }
        c.close();
        db.close();
        return stats;
    }

    public static Collection<Stat> getUserSelectedStatCategories(final Context context) {
        final LocalMysqlHelper dbHelper = new LocalMysqlHelper(context);

        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db == null) {
            throw new IllegalStateException("could not get readable db to local storage");
        }

        final Cursor c = db.rawQuery("select * from " + StatCategorySql.TABLE_NAME, null);
        if (!c.moveToFirst()) {
            return Collections.EMPTY_LIST;
        }
        final List<Stat> stats = new ArrayList<Stat>();
        while (!c.isAfterLast()) {
            final int sid = c.getInt(c.getColumnIndexOrThrow(StatCategorySql.COLUMN_NAME_STAT_ID));
            stats.add(Stat.getStatFromId(sid));
            c.moveToNext();
        }
        c.close();
        db.close();
        return stats;
    }

    public static void addSelectedStatCategory(final Stat stat,
                                               final Context context) {
        final LocalMysqlHelper dbHelper = new LocalMysqlHelper(context);

        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db == null) {
            throw new IllegalStateException("could not get writeable db to local storage");
        }

        if (!doesStatExistInDb(stat, db)) {
            insertStatCategory(stat, db);
        }

        db.close();
    }

    private static boolean doesStatExistInDb(final Stat stat, final SQLiteDatabase db) {
        final Cursor c =
                db.rawQuery("select count(*) as matches from " + StatCategorySql.TABLE_NAME + " where sid = " + stat.getSid(), null);
        c.moveToFirst();
        // should always have 1 row returned, since its aggregation count (even if there are 0)
        final int count = c.getInt(0);

        return count > 0;
    }

    private static void insertStatCategory(final Stat stat, final SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(StatCategorySql.COLUMN_NAME_STAT_ID, stat.getSid());

        db.insert(StatCategorySql.TABLE_NAME, "null", values);
    }

    public static void removeSelectedStats(final Set<Stat> stats, final Context context) {
        if (stats.size() == 0) {
            return;
        }
        final LocalMysqlHelper dbHelper = new LocalMysqlHelper(context);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        if (db == null) {
            throw new IllegalStateException("could not get readable db to local storage");
        }

        final StringBuilder sb = new StringBuilder();
        for (final Stat stat : stats) {
            sb.append(stat.getSid() + ",");
        }
        sb.setLength(sb.length() - 1);

        db.delete(StatCategorySql.TABLE_NAME, "sid IN (" + sb.toString() + ")", null);
        db.close();
    }

    public static class SqlStatContainer {
        final int sid;
        final int uid;
        final String dateString;
        final int val;

        public SqlStatContainer(final int sid,
                                final int uid,
                                final String dateString,
                                final int val) {
            this.sid = sid;
            this.uid = uid;
            this.dateString = dateString;
            this.val = val;
        }

        @Override
        public String toString() {
            return String.format("StatId: %d, UserId: %d, Date: %s, StatVal: %d", this.sid, this.uid, this.dateString, this.val);
        }
    }
}
