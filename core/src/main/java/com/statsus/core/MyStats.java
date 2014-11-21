package com.statsus.core;

import java.util.Date;

import com.statsus.core.data.StatData;
import com.statsus.core.metadata.Stat;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * @author john.wright
 * @since May 20 2014
 */
public class MyStats
        extends ActivityWithMainMenu {
    private Date viewDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_single_item);

        setViewDate();

        initMyStatsContent();
    }

    private void setViewDate() {
        // TODO properly handle beginning of a month date,, and other date ranges:
        final long lastMonthDateTime = new Date().getTime() - Home.DAY_INCRMENT * 30;
        this.viewDate = new Date(lastMonthDateTime);
    }

    private void initMyStatsContent() {
        final Stat stat = Stat.values()[0];

        final Button itemText = (Button) findViewById(R.id.item_text);
        final ImageButton itemButton = (ImageButton) findViewById(R.id.item_icon_1);

        stat.setTextProperties(itemText);
        stat.setStatImageProperties(itemButton);

        final StatData statData = Stat.queryStatDataAfterDate(stat.getSid(), this.viewDate, getApplicationContext());

        setTotalCount(statData);
    }

    private void setTotalCount(final StatData statData) {
        final int totalCount = statData.getTotalCount();
        final String qualifier = totalCount > 1 ? " days" : " day";

        final TextView statCountView = (TextView) findViewById(R.id.stat_count);
        statCountView.setText(totalCount + qualifier);
    }
}
