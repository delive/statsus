package com.statsus.core;

import com.statsus.core.mystats.MyStats;
import com.statsus.core.persistence.LocalPersistenceManager;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

/**
 * @author john.wright
 * @since 21
 */
public class ActivityWithBanner
        extends Activity {

    public void myDay(View v) {
        final Intent intent = new Intent(v.getContext(), Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void myStats(View v) {
        final Intent intent = new Intent(v.getContext(), MyStats.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
      }

    public void myFriends(View v) {
//        final Intent intent = new Intent(v.getContext(), Friends.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
        LocalPersistenceManager.truncateAll(getApplicationContext());
    }
}
