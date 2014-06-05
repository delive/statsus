package com.statsus.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.statsus.core.metadata.Stat;
import com.statsus.core.persistence.LocalPersistenceManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;


public class Home
        extends ActivityWithBanner {

    //TODO implement users
    public static long UID = 0;
    private Date date = new Date();

    final Set<Stat> selectedStats = new HashSet<Stat>();
    final Map<Stat, Button> statValues = new HashMap<Stat, Button>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_1_first_login);
        initStatCategories();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAndSetDate();
        initStatCategories();
    }

    private void checkAndSetDate() {
        final Date currentDate = new Date();
        if (this.date != currentDate) {
            this.date = currentDate;
        }
    }

    /**
     * already on this page, just re-init content in case
     */
    @Override
    public void myDay(View v) {
        onResume();
    }

    public void cancelDailyStats(View v) {
        this.selectedStats.clear();
        ((LinearLayout)findViewById(R.id.selectedStats)).removeAllViews();
        findViewById(R.id.cancelSubmitDailyStats).setVisibility(View.GONE);

        this.initStatCategories();
    }

    /**
     * Builds the 2 column multi rowed stat category layout from stats selected by the user
     */
    private void initStatCategories() {
        final LinearLayout statContainer = (LinearLayout) findViewById(R.id.stat_category_container);
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
        final Collection<Stat> statsCompletedToday = LocalPersistenceManager.getCompletedStatsForToday(this.date, context);
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
                    initStatCategories();
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
        for (final Entry<Stat, Button> statButton : this.statValues.entrySet()) {
            final int val = Integer.valueOf(statButton.getValue().getText().toString());

            LocalPersistenceManager.addStat(statButton.getKey(), val, UID, new Date(), getApplicationContext());
        }
        cancelDailyStats(null);
    }

    public void addMoreItems(View v) {
        startActivity(new Intent(this, Categories.class));
    }

    public void editOrRemove(View v) {
        startActivity(new Intent(this, EditOrRemoveStat.class));
    }
}
