package com.statsus.core;

import java.util.Collection;
import java.util.Date;

import com.statsus.core.metadata.Stat;
import com.statsus.core.persistence.LocalPersistenceManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;


public class Home
        extends ActivityWithBanner {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_1_first_login);
        initStatCategories();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initStatCategories();
    }

    /**
     * already on this page, just re-init content in case
     */
    @Override
    public void myDay(View v) {
        onResume();
    }

    /**
     * Builds the 2 column multi rowed stat category layout from stats selected by the user
     */
    private void initStatCategories() {
        final LinearLayout statContainer = (LinearLayout) findViewById(R.id.stat_category_container);
        statContainer.removeAllViews();

        int colCount = 0;
        // init the first row
        LinearLayout row = (LinearLayout) getLayoutInflater().inflate(R.layout.home_category_row, statContainer, false);
        statContainer.addView(row);
        final Collection<Stat> stats = LocalPersistenceManager.getUserSelectedStatCategories(getApplicationContext());
        for (final Stat stat : stats) {
            if (colCount == 2) {
                row = (LinearLayout) getLayoutInflater().inflate(R.layout.home_stat_row, statContainer, false);
                statContainer.addView(row);
                colCount = 0;
            }
            final LinearLayout col =
                    (LinearLayout) getLayoutInflater().inflate(R.layout.home_stat_column, statContainer, false);
            stat.setStatImageProperties((ImageButton) col.findViewById(R.id.stat_button_image));
            stat.setTextProperties((Button) col.findViewById(R.id.stat_button));
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

    public void submitStats(View v) {
        //TODO make this real test data, right now just setting up the framework for local persistence
//        final int value1 = Integer.valueOf(((EditText) findViewById(R.id.value1)).getText().toString());
//        final int value2 = Integer.valueOf(((EditText) findViewById(R.id.value2)).getText().toString());

        final Stat stat1 = Stat.getStatFromId(1);
        final Stat stat2 = Stat.getStatFromId(2);

        final Date date = new Date();

        final int uid = 1;

        LocalPersistenceManager.addStat(stat1, 3, uid, date, getApplicationContext());
        LocalPersistenceManager.addStat(stat2, 5, uid, date, getApplicationContext());
    }

    public void addMoreItems(View v) {
        startActivity(new Intent(this, Categories.class));
    }

    public void editOrRemove(View v) {
        startActivity(new Intent(this, EditOrRemoveStat.class));
    }
}
