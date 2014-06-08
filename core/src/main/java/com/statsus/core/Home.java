package com.statsus.core;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.statsus.core.metadata.Stat;
import com.statsus.core.metadata.ViewMode;
import com.statsus.core.persistence.LocalPersistenceManager;
import com.statsus.core.persistence.LocalPersistenceManager.SqlStatContainer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;


public class Home
        extends ActivityWithBanner {

    private static final String LOGTAG = "Home";
    public static final int DAY_INCRMENT = (1000 * 60 * 60 * 24);
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMMM dd yyyy");

    //TODO implement users
    public static long UID = 0;

    private Date date;
    private Date realDate;
    private ViewMode viewMode;

    final Set<Stat> selectedStats = new HashSet<Stat>();
    final Map<Stat, Button> statValues = new HashMap<Stat, Button>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_1_first_login);
        initHomePage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initHomePage();
    }

    @Override
    public void myDay(View v) {
        this.viewMode = null;
        this.date = null;
        onResume();
    }

    private void initHomePage() {
        checkAndSetViewModeAndDate();
        resetViews();

        final List<SqlStatContainer> statContainers =
                LocalPersistenceManager.getCompletedStatContainersForToday(this.date, getApplicationContext());

        if (isDailySubmitAllowed() && (this.viewMode == ViewMode.AddStat || statContainers.size() == 0)) {
            initStatCategories();
        }
        else {
            initSummaryPage(statContainers);
        }
    }

    private boolean isDailySubmitAllowed() {
        return viewDateIsYesterday() || viewDateIsCurrentDate();
    }

    private void resetViews() {
        findViewById(R.id.submit_pickItemsBelow).setVisibility(View.GONE);
        findViewById(R.id.cancelSubmitDailyStats).setVisibility(View.GONE);
        findViewById(R.id.footerButton_addStats).setVisibility(View.GONE);
        findViewById(R.id.footerButton_summary).setVisibility(View.GONE);
    }

    private void initSummaryPage(final List<SqlStatContainer> statContainers) {
        findViewById(R.id.footerButton_summary).setVisibility(View.VISIBLE);
        final LinearLayout contentAreaLl = (LinearLayout) findViewById(R.id.home_content_container);
        contentAreaLl.removeAllViews();

        for (final SqlStatContainer cont : statContainers) {
            final LinearLayout rowLl =
                    (LinearLayout) getLayoutInflater().inflate(R.layout.stat_summary_row, contentAreaLl, false);
            final ImageButton iconButton = (ImageButton) rowLl.findViewById(R.id.stat_row_icon);
            final Button textButton = (Button) rowLl.findViewById(R.id.stat_button);
            //todo add note lookup
            final Button valueButton = (Button) rowLl.findViewById(R.id.stat_value_button);

            final Stat stat = Stat.getStatFromId(cont.getSid());

            stat.setStatImageProperties(iconButton);
            stat.setTextProperties(textButton);
            stat.setBackgroundResource(valueButton);
            valueButton.setText(Integer.toString(cont.getVal()));

            contentAreaLl.addView(rowLl);
        }
    }

    private void checkAndSetViewModeAndDate() {
        this.realDate = new Date();
        if (this.viewMode == null) {
            this.viewMode = ViewMode.Default;
        }
        if (this.date == null) {
            this.date = this.realDate;
        }

        final View nextDay = findViewById(R.id.nextDay);
        if (viewDateIsCurrentDate()) {
            nextDay.setVisibility(View.GONE);
        }
        else {
            nextDay.setVisibility(View.VISIBLE);
        }
        ((TextView) findViewById(R.id.date)).setText(DATE_FORMAT.format(this.date));
    }

    public void cancelDailyStats(View v) {
        this.selectedStats.clear();
        ((LinearLayout)findViewById(R.id.selectedStats)).removeAllViews();
        findViewById(R.id.cancelSubmitDailyStats).setVisibility(View.GONE);

        initHomePage();
    }

    /**
     * Builds the 2 column multi rowed stat category layout from stats selected by the user
     */
    private void initStatCategories() {
        findViewById(R.id.submit_pickItemsBelow).setVisibility(View.VISIBLE);
        findViewById(R.id.footerButton_addStats).setVisibility(View.VISIBLE);
        if (this.selectedStats.size() > 0) {
            findViewById(R.id.cancelSubmitDailyStats).setVisibility(View.VISIBLE);
        }
        final LinearLayout statContainer = (LinearLayout) findViewById(R.id.home_content_container);
        final LinearLayout selectedStatsLl = (LinearLayout) findViewById(R.id.selectedStats);
        statContainer.removeAllViews();

        int colCount = 0;
        // init the first row
        LinearLayout row = (LinearLayout) getLayoutInflater().inflate(R.layout.home_category_row, statContainer, false);
        statContainer.addView(row);

        final Collection<Stat> stats = getValidStatsForToday();
        for (final Stat stat : stats) {
            if (this.selectedStats.contains(stat)) {
                continue;
            }
            if (colCount == 2) {
                row = (LinearLayout) getLayoutInflater().inflate(R.layout.home_stat_row, statContainer, false);
                statContainer.addView(row);
                colCount = 0;
            }
            final LinearLayout col =
                    (LinearLayout) getLayoutInflater().inflate(R.layout.home_stat_column, statContainer, false);
            final Button textButton = (Button) col.findViewById(R.id.stat_button);
            stat.setStatImageProperties((ImageButton) col.findViewById(R.id.stat_button_image));
            stat.setTextProperties(textButton);
            textButton.setOnClickListener(getClickListenerDailyStat(stat, selectedStatsLl));
            row.addView(col);
            colCount++;
        }

        if (((stats.size() - this.selectedStats.size()) & 1) != 0) {
            // odd number of categories, need to inflate an empty col
            final LinearLayout col =
                    (LinearLayout) getLayoutInflater().inflate(R.layout.home_column_empty, statContainer, false);
            row.addView(col);
        }
    }

    private Collection<Stat> getValidStatsForToday() {
        final Context context = getApplicationContext();
        final List<Stat> validStats = new ArrayList<Stat>(LocalPersistenceManager.getUserSelectedStats(context));
        final Collection<Stat> statsCompletedToday = LocalPersistenceManager.getCompletedStatsTypesForToday(this.date, context);
        for (final Stat stat : statsCompletedToday) {
            validStats.remove(stat);
        }
        return validStats;
    }

    private OnClickListener getClickListenerDailyStat(final Stat stat,
                                                      final LinearLayout selectedStatsLl) {
        return new OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (Home.this.selectedStats.size() == 0) {
                    // first selection of a stat - show the cancel / submit pane
                    findViewById(R.id.cancelSubmitDailyStats).setVisibility(View.VISIBLE);
                }
                if (!Home.this.selectedStats.contains(stat)) {
                    final LinearLayout row =
                            (LinearLayout) getLayoutInflater().inflate(R.layout.home_2_selected_row, selectedStatsLl, false);
                    stat.setTextProperties((Button) row.findViewById(R.id.statName));
                    stat.setStatImageProperties((ImageButton) row.findViewById(R.id.stat_icon_button));
                    prepareStatValues(row, stat);

                    selectedStatsLl.addView(row);
                    Home.this.selectedStats.add(stat);
                    initHomePage();
                }
            }
        };
    }

    private void prepareStatValues(final LinearLayout row, final Stat stat) {
        final Button button1 = (Button) row.findViewById(R.id.stat_selected_1);
        final Button button2 = (Button) row.findViewById(R.id.stat_selected_2);
        final Button button3 = (Button) row.findViewById(R.id.stat_selected_3);
        final Button button4 = (Button) row.findViewById(R.id.stat_selected_4);
        final Button button5 = (Button) row.findViewById(R.id.stat_selected_5);

        button1.setOnClickListener(getClickListenerButtonValueSelect(stat));
        button2.setOnClickListener(getClickListenerButtonValueSelect(stat));
        button3.setOnClickListener(getClickListenerButtonValueSelect(stat));
        button4.setOnClickListener(getClickListenerButtonValueSelect(stat));
        button5.setOnClickListener(getClickListenerButtonValueSelect(stat));

        stat.setBackgroundResource(button1);
        stat.setBackgroundResource(button2);
        stat.setBackgroundResource(button3);
        stat.setBackgroundResource(button4);
        stat.setBackgroundResource(button5);
    }

    private OnClickListener getClickListenerButtonValueSelect(final Stat stat) {
        return new OnClickListener() {
            @Override
            public void onClick(final View button) {
                doSelectValue((Button) button, stat);

                final Button selectedButton = Home.this.statValues.remove(stat);
                if (selectedButton != null) {
                    // another val for this stat has been selected. deselect and select this new val
                    doDeselectValue(selectedButton, stat);
                }
                Home.this.statValues.put(stat, (Button) button);
            }
        };
    }

    private OnClickListener getClickListenerButtonValueDeselect(final Stat stat) {
        return new OnClickListener() {
            @Override
            public void onClick(final View button) {
                doDeselectValue((Button) button, stat);
                Home.this.statValues.remove(stat);
            }
        };
    }

    private void doSelectValue(final Button button, final Stat stat) {
        button.setBackgroundResource(R.drawable.white_button);
        button.setOnClickListener(getClickListenerButtonValueDeselect(stat));
    }

    private void doDeselectValue(final Button button, final Stat stat) {
        stat.setBackgroundResource(button);
        button.setOnClickListener(getClickListenerButtonValueSelect(stat));
    }

    public void submitDailyStats(View v) {
        if (!viewDateIsYesterday() && !viewDateIsCurrentDate()) {
            Log.w("trying to submit stats for an old day:" + this.date, LOGTAG);
            return;
        }
        for (final Entry<Stat, Button> statButton : this.statValues.entrySet()) {
            final int val = Integer.valueOf(statButton.getValue().getText().toString());

            LocalPersistenceManager.addStat(statButton.getKey(), val, UID, this.date, getApplicationContext());
        }
        this.viewMode = ViewMode.StatSummary;
        cancelDailyStats(null);
    }

    public void addMoreItems(View v) {
        startActivity(new Intent(this, Categories.class));
    }

    public void viewModeAddStats(View v) {
        this.viewMode = ViewMode.AddStat;
        initHomePage();
    }

    public void nextDay(View v) {
        this.date = new Date(this.date.getTime() + DAY_INCRMENT);
        if (viewDateIsCurrentDate() || viewDateIsYesterday()) {
            // if the next day is today or yesterday, go back to default view
            this.viewMode = ViewMode.Default;
        }
        initHomePage();
    }

    public void previousDay(View v) {
        this.date = new Date(this.date.getTime() - DAY_INCRMENT);
        if (viewDateIsYesterday()) {
            // if the next day is today or yesterday, go back to default view
            this.viewMode = ViewMode.Default;
        }
        initHomePage();
    }

    private boolean viewDateIsYesterday() {
        final Calendar cal1 = Calendar.getInstance();
        final Calendar cal2 = Calendar.getInstance();
        cal1.setTime(this.date);
        cal2.setTime(new Date(this.realDate.getTime() - DAY_INCRMENT));
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private boolean viewDateIsCurrentDate() {
        final Calendar cal1 = Calendar.getInstance();
        final Calendar cal2 = Calendar.getInstance();
        cal1.setTime(this.date);
        cal2.setTime(this.realDate);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public void editOrRemove(View v) {
        startActivity(new Intent(this, EditOrRemoveStat.class));
    }
}
