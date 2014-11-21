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
import android.util.Log;

/**
 * Facilitates all communication to the local persistence.
 *
 * @author john.wright
 * @since May 19 2014
 */
public class LocalPersistenceManager {
    private static final String LOGTAG = "LocalPersistenceManager";

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static void addStat(final Stat stat,
                               final int statVal,
                               final long uid,
                               final Date date,
                               final Context context) {
        final SQLiteDatabase db = getWriteableDb(context);

        final ContentValues values = new ContentValues();
        final int sid = stat.getSid();
        final String dateText = DATE_FORMAT.format(date);

        if (dailyStatAlreadyExists(db, sid, uid, dateText)) {
            Log.w(LOGTAG, "skipping daily stat insert, row already exists for the day");
            return;
        }

        values.put(StatContentSql.COLUMN_NAME_STAT_ID, sid);
        values.put(StatContentSql.COLUMN_NAME_USER_ID, uid);
        values.put(StatContentSql.COLUMN_NAME_DATE, dateText);
        values.put(StatContentSql.COLUMN_NAME_VAL, statVal);

        final long result = db.insert(StatContentSql.TABLE_NAME, "null", values);
        db.close();

        if (result == -1) {
            Log.w(LOGTAG, "error adding stat, possible duplicate entry");
        }
    }

    private static boolean dailyStatAlreadyExists(final SQLiteDatabase db,
                                                  final int sid,
                                                  final long uid,
                                                  final String dateText) {
        final Cursor c =
                db.rawQuery(String.format("select count(*) as rowCount from %s where sid = %d AND uid = %d AND date = '%s'",
                        StatContentSql.TABLE_NAME, sid, uid, dateText), null);
        c.moveToFirst();

        final int rowCount = c.getInt(c.getColumnIndexOrThrow("rowCount"));
        if (rowCount > 1) {
            Log.w(LOGTAG, String.format("multiple entries for daily stat for, sid:%d uid:%d date:%s", sid, uid, dateText));
        }
        return rowCount > 0;
    }

    public static List<SqlStatContainer> getAllStats(final Context context) {
        final SQLiteDatabase db = getReadableDb(context);
        final List<SqlStatContainer> stats = new ArrayList<SqlStatContainer>();


        final Cursor c = db.rawQuery("select * from " + StatContentSql.TABLE_NAME, null);
        if (!c.moveToFirst()) {
            return Collections.EMPTY_LIST;
        }
        while (!c.isAfterLast()) {
            final SqlStatContainer stat = buildSqlContainerFromCursor(c);
            stats.add(stat);
            c.moveToNext();
        }
        c.close();
        db.close();
        return stats;
    }

    public static Collection<Stat> getUserSelectedStats(final Context context) {
        final SQLiteDatabase db = getReadableDb(context);

        final Cursor c = db.rawQuery("select sid from " + StatCategorySql.TABLE_NAME, null);
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
        final SQLiteDatabase db = getWriteableDb(context);

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
        final SQLiteDatabase db = getWriteableDb(context);

        final StringBuilder sb = new StringBuilder();
        for (final Stat stat : stats) {
            sb.append(stat.getSid() + ",");
        }
        sb.setLength(sb.length() - 1);

        db.delete(StatCategorySql.TABLE_NAME, "sid IN (" + sb.toString() + ")", null);
        db.close();
    }

    public static Collection<Stat> getCompletedStatsTypesForToday(final Date date, final Context context) {
        final SQLiteDatabase db = getReadableDb(context);

        final Cursor c =
                db.rawQuery("select sid from " + StatContentSql.TABLE_NAME + " where date = '" + DATE_FORMAT.format(date) + "'", null);
        if (!c.moveToFirst()) {
            return Collections.EMPTY_LIST;
        }

        final List<Stat> stats = new ArrayList<Stat>();
        while (!c.isAfterLast()) {
            final int sid = c.getInt(c.getColumnIndexOrThrow(StatCategorySql.COLUMN_NAME_STAT_ID));
            stats.add(Stat.getStatFromId(sid));
            c.moveToNext();
        }
        return stats;
    }

    public static List<SqlStatContainer> getCompletedStatContainersForToday(final Date date, final Context context) {
        final SQLiteDatabase db = getReadableDb(context);
        final List<SqlStatContainer> stats = new ArrayList<SqlStatContainer>();

        final Cursor c =
                db.rawQuery("select * from " + StatContentSql.TABLE_NAME + " where date = '" + DATE_FORMAT.format(date) + "'", null);
        if (!c.moveToFirst()) {
            return Collections.EMPTY_LIST;
        }
        while (!c.isAfterLast()) {
            final SqlStatContainer stat = buildSqlContainerFromCursor(c);
            stats.add(stat);
            c.moveToNext();
        }
        c.close();
        db.close();

        return stats;
    }

    private static SqlStatContainer buildSqlContainerFromCursor(final Cursor c) {
        final int sid = c.getInt(c.getColumnIndexOrThrow(StatContentSql.COLUMN_NAME_STAT_ID));
        final int uid = c.getInt(c.getColumnIndexOrThrow(StatContentSql.COLUMN_NAME_USER_ID));
        final String dateString = c.getString(c.getColumnIndexOrThrow(StatContentSql.COLUMN_NAME_DATE));
        final int val = c.getInt(c.getColumnIndexOrThrow(StatContentSql.COLUMN_NAME_VAL));
        final String note = c.getString(c.getColumnIndexOrThrow(StatContentSql.COLUMN_NAME_NOTE));

        return new SqlStatContainer(sid, uid, dateString, val, note);
    }

    public static void truncateAll(final Context context) {
        final SQLiteDatabase db = getWriteableDb(context);

        db.delete(StatContentSql.TABLE_NAME, null, null);
        db.delete(StatCategorySql.TABLE_NAME, null, null);

        db.close();
    }

    public static SQLiteDatabase getReadableDb(final Context context) {
        final LocalMysqlHelper dbHelper = new LocalMysqlHelper(context);
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db == null) {
            throw new IllegalStateException("could not get readable db to local storage");
        }
        return db;
    }

    private static SQLiteDatabase getWriteableDb(final Context context) {
        final LocalMysqlHelper dbHelper = new LocalMysqlHelper(context);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db == null) {
            throw new IllegalStateException("could not get writeable db to local storage");
        }
        return db;
    }

    public static void updateNoteForStat(final SqlStatContainer statCont, final String note, final Context context) {
        final SQLiteDatabase db = getReadableDb(context);
        final ContentValues values = new ContentValues();
        values.put("note", note);

        final String whereClause = "sid = " + statCont.getSid() + " AND uid = " + statCont.getUid() + " AND date = '" + statCont.getDateString() + "'";

        final int result = db.update(StatContentSql.TABLE_NAME, values, whereClause, null);

        db.close();

        if (result == -1) {
            Log.w(LOGTAG, "error updating note");
        }
    }

    public static class SqlStatContainer {
        final int sid;
        final int uid;
        final String dateString;
        final int val;
        private final String note;

        public SqlStatContainer(final int sid,
                                final int uid,
                                final String dateString,
                                final int val,
                                final String note) {
            this.sid = sid;
            this.uid = uid;
            this.dateString = dateString;
            this.val = val;
            this.note = note;
        }

        @Override
        public String toString() {
            return String.format("StatId: %d, UserId: %d, Date: %s, StatVal: %d", this.sid, this.uid, this.dateString, this.val);
        }

        public int getSid() {
            return sid;
        }

        public int getUid() {
            return uid;
        }

        public String getDateString() {
            return dateString;
        }

        public int getVal() {
            return val;
        }

        public String getNote() {
            return this.note;
        }
    }
}
