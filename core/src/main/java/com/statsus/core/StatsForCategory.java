package com.statsus.core;

import java.util.Set;

import com.statsus.core.metadata.Category;
import com.statsus.core.metadata.Stat;
import com.statsus.core.persistence.LocalPersistenceManager;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author john.wright
 * @since 21
 */
public class StatsForCategory
        extends ActivityWithBanner {
    private Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_add_more_items_category);

        this.category = (Category) getIntent().getSerializableExtra("category");

        initTitle();
        initStats();
    }

    private void initTitle() {
        final TextView tv = (TextView) findViewById(R.id.currentlyViewing);
        final String currentText = tv.getText().toString();
        tv.setText(currentText + this.category);
    }

    private void initStats() {
        final LinearLayout statContainer = (LinearLayout) findViewById(R.id.stat_category_container);

        int colCount = 0;
        // init the first row
        LinearLayout row = (LinearLayout) getLayoutInflater().inflate(R.layout.home_category_row, statContainer, false);
        statContainer.addView(row);
        final Set<Stat> stats = Stat.getStatsForCategory(this.category);
        for (final Stat stat : stats) {
            if (colCount == 2) {
                row = (LinearLayout) getLayoutInflater().inflate(R.layout.home_stat_row, statContainer, false);
                statContainer.addView(row);
                colCount = 0;
            }
            final LinearLayout col =
                    (LinearLayout) getLayoutInflater().inflate(R.layout.home_stat_column, statContainer, false);
            stat.setStatImageProperties((ImageButton) col.findViewById(R.id.stat_button_image));
            final Button statButton = (Button) col.findViewById(R.id.stat_button);
            statButton.setOnClickListener(getClickListenerForAddStatCategory(stat));
            stat.setTextProperties(statButton);
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

    private OnClickListener getClickListenerForAddStatCategory(final Stat stat) {
        return new OnClickListener() {
            @Override
            public void onClick(final View view) {
                LocalPersistenceManager.addSelectedStatCategory(stat, view.getContext());
                finish();
            }
        };
    }
}
