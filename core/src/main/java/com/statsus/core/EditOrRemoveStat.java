package com.statsus.core;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.statsus.core.utility.Util;
import com.statsus.core.metadata.Stat;
import com.statsus.core.persistence.LocalPersistenceManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

/**
 * @author john.wright
 * @since 21
 */
public class EditOrRemoveStat
        extends ActivityWithDateSlider {

    final Set<Stat> statsToRemove = new HashSet<Stat>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_1_first_login_remove_items);

        final Date dateFromIntent = (Date) getIntent().getSerializableExtra("date");
        if (dateFromIntent != null) {
            this.date = dateFromIntent;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAndSetDate();
        initStatCategories();
    }

    /**
     * Builds the 2 column multi rowed stat category layout from stats selected by the user
     */
    private void initStatCategories() {
        super.checkAndSetDate();

        final LinearLayout statContainer = (LinearLayout) findViewById(R.id.home_content_container);
        statContainer.removeAllViews();

        int colCount = 0;
        // init the first row
        LinearLayout row = (LinearLayout) getLayoutInflater().inflate(R.layout.home_category_row, statContainer, false);
        statContainer.addView(row);
        final Collection<Stat> stats = LocalPersistenceManager.getUserSelectedStats(getApplicationContext());
        for (final Stat stat : stats) {
            if (colCount == 2) {
                row = (LinearLayout) getLayoutInflater().inflate(R.layout.home_stat_row, statContainer, false);
                statContainer.addView(row);
                colCount = 0;
            }
            final LinearLayout col =
                    (LinearLayout) getLayoutInflater().inflate(R.layout.home_stat_column, statContainer, false);
            final ImageButton statIconButton = (ImageButton) col.findViewById(R.id.stat_button_image);
            final Button statButton = (Button) col.findViewById(R.id.stat_button);
            stat.setStatImageProperties(statIconButton);
            stat.setTextProperties(statButton);
            statButton.setOnClickListener(getClickListenerGreyItem(stat, this.statsToRemove, statButton, statIconButton));

            row.addView(col);
            colCount++;
        }

        if ((stats.size() & 1) != 0) {
            // odd number of categories, need to inflate an empty col
            final LinearLayout col =
                    (LinearLayout) getLayoutInflater().inflate(R.layout.home_column_empty, statContainer, false);
            row.addView(col);
        }
    }

    public void cancel(View v) {
        finish();
    }

    public void removeStats(View v) {
        LocalPersistenceManager.removeSelectedStats(this.statsToRemove, getApplicationContext());
        finish();
    }

    private static OnClickListener getClickListenerGreyItem(final Stat stat,
                                                            final Set<Stat> statsToRemove,
                                                            final Button button,
                                                            final ImageButton imageButton) {
        return new OnClickListener() {
            @Override
            public void onClick(final View view) {
                button.setTextColor(0x20FFFFFF);
                imageButton.setBackgroundResource(R.drawable.icon_bg_gray);
                button.setOnClickListener(getClickListenerNormal(stat, statsToRemove, button, imageButton));
                statsToRemove.add(stat);
            }
        };
    }

    private static OnClickListener getClickListenerNormal(final Stat stat,
                                                          final Set<Stat> statsToRemove,
                                                          final Button button,
                                                          final ImageButton imageButton) {
        return new OnClickListener() {
            @Override
            public void onClick(final View view) {
                stat.setTextProperties(button);
                stat.setStatImageProperties(imageButton);
                button.setOnClickListener(getClickListenerGreyItem(stat, statsToRemove, button, imageButton));
                statsToRemove.remove(stat);
            }
        };
    }

    @Override
    public void nextDay(View v) {
        super.nextDay(v);
        final Intent intent = new Intent(v.getContext(), Home.class);
        intent.putExtra("date", this.date);
        Util.setIntentFlagNoHistory(intent);
        startActivity(intent);
    }

    @Override
    public void previousDay(View v) {
        super.previousDay(v);
        final Intent intent = new Intent(v.getContext(), Home.class);
        intent.putExtra("date", this.date);
        Util.setIntentFlagNoHistory(intent);
        startActivity(intent);
    }
}
