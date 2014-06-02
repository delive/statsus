package com.statsus.core;

import android.os.Bundle;

/**
 * @author john.wright
 * @since 21
 */
public class Friends
        extends ActivityWithBanner {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //todo real page
        setContentView(R.layout.home_add_more_items);
    }
}
