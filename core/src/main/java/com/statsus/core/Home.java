package com.statsus.core;

import java.util.Date;

import com.statsus.core.metadata.Stat;
import com.statsus.core.persistence.LocalPersistenceManager;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class Home extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void submitStats(View v) {
        //TODO make this real test data, right now just setting up the framework for local persistence
        final int value1 = Integer.valueOf(((EditText) findViewById(R.id.value1)).getText().toString());
        final int value2 = Integer.valueOf(((EditText) findViewById(R.id.value2)).getText().toString());

        final Stat stat1 = Stat.getStatFromId(1);
        final Stat stat2 = Stat.getStatFromId(2);

        final Date date = new Date();

        final int uid = 1;

        LocalPersistenceManager.addStat(stat1, value1, uid, date, getApplicationContext());
        LocalPersistenceManager.addStat(stat2, value2, uid, date, getApplicationContext());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
