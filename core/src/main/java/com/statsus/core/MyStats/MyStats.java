package com.statsus.core.mystats;

import java.util.List;

import com.statsus.core.R;
import com.statsus.core.persistence.LocalPersistenceManager;
import com.statsus.core.persistence.LocalPersistenceManager.SqlStatContainer;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 *
 * @author john.wright
 * @since May 20 2014
 */
public class MyStats extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_stats);

        initMyStatsContent();
    }

    private void initMyStatsContent() {
        //TODO just test text to test persistence for now
        final TextView textView = (TextView) findViewById(R.id.content);

        final List<SqlStatContainer> stats = LocalPersistenceManager.getAllStats(getApplicationContext());

        final StringBuilder sb = new StringBuilder();
        for (final SqlStatContainer stat : stats) {
            sb.append(stat);
        }
        textView.setText(sb.toString());
    }
}
