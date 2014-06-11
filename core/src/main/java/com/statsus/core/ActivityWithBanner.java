package com.statsus.core;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.statsus.core.persistence.LocalPersistenceManager;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

/**
 * @author john.wright
 * @since 21
 */
public class ActivityWithBanner
        extends Activity {
    public static final int DAY_INCRMENT = (1000 * 60 * 60 * 24);
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMMM dd yyyy");

    protected Date date;
    protected Date realDate;

    public void myDay(View v) {
        final Intent intent = new Intent(v.getContext(), Home.class);
        Util.setIntentFlagNoHistory(intent);
        startActivity(intent);
    }

    public void myStats(View v) {
        final Intent intent = new Intent(v.getContext(), MyStats.class);
        Util.setIntentFlagNoHistory(intent);
        startActivity(intent);
      }

    public void myFriends(View v) {
//        final Intent intent = new Intent(v.getContext(), Friends.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
        LocalPersistenceManager.truncateAll(getApplicationContext());
    }

    protected void initHomePage() {
        checkAndSetDate();
    }

    public void checkAndSetDate() {
        this.realDate = new Date();
        if (this.date == null) {
            this.date = this.realDate;
        }

        final View nextDay = findViewById(R.id.nextDay);
        if (viewDateIsToday()) {
            nextDay.setVisibility(View.GONE);
        }
        else {
            nextDay.setVisibility(View.VISIBLE);
        }
        ((TextView) findViewById(R.id.date)).setText(DATE_FORMAT.format(this.date));
    }

    public void nextDay(View v) {
        this.date = new Date(this.date.getTime() + DAY_INCRMENT);
        initHomePage();
    }

    public void previousDay(View v) {
        this.date = new Date(this.date.getTime() - DAY_INCRMENT);
        initHomePage();
    }

    public boolean viewDateIsYesterday() {
        final Calendar cal1 = Calendar.getInstance();
        final Calendar cal2 = Calendar.getInstance();
        cal1.setTime(this.date);
        cal2.setTime(new Date(this.realDate.getTime() - DAY_INCRMENT));
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public boolean viewDateIsToday() {
        final Calendar cal1 = Calendar.getInstance();
        final Calendar cal2 = Calendar.getInstance();
        cal1.setTime(this.date);
        cal2.setTime(this.realDate);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
}
