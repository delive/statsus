package com.statsus.core;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.statsus.core.utility.OnSwipeTouchListener;

import android.view.View;
import android.widget.TextView;

/**
 * @author john.wright
 * @since 21
 */
public class ActivityWithDateSlider
        extends ActivityWithMainMenu {
    public static final long DAY_INCRMENT = (1000 * 60 * 60 * 24);
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMMM dd yyyy");

    // date we are currently viewing
    protected Date date;
    // actual date
    protected Date realDate;
    boolean swipeInitialized;

    @Override
    protected void onResume() {
        super.onResume();

        if (!this.swipeInitialized) {
            findViewById(R.id.date_bar).setOnTouchListener(new OnSwipeTouchListener(this) {
                @Override
                public void onSwipeLeft() {
                    nextDay(null);
                }

                @Override
                public void onSwipeRight() {
                    previousDay(null);
                }
            });
            this.swipeInitialized = true;
        }
    }

    protected void initHomePage(final boolean hardReset) {
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
        if (!viewDateIsToday()) {
            this.date = new Date(this.date.getTime() + DAY_INCRMENT);
            initHomePage(true);
        }
    }

    public void previousDay(View v) {
        this.date = new Date(this.date.getTime() - DAY_INCRMENT);
        initHomePage(true);
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
