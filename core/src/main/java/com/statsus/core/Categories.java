package com.statsus.core;

import com.statsus.core.metadata.Category;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * @author john.wright
 * @since 21
 */
public class Categories
        extends ActivityWithBanner {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_add_more_items);
        initCategories();
    }

    private void initCategories() {
        final LinearLayout categoryContainer = (LinearLayout) findViewById(R.id.category_container);

        int colCount = 0;
        // init the first row
        LinearLayout row = (LinearLayout) getLayoutInflater().inflate(R.layout.home_category_row, categoryContainer, false);
        categoryContainer.addView(row);
        final Category[] categories = Category.values();
        for (final Category category : categories) {
            if (colCount == 2) {
                row = (LinearLayout) getLayoutInflater().inflate(R.layout.home_category_row, categoryContainer, false);
                categoryContainer.addView(row);
                colCount = 0;
            }
            final LinearLayout col =
                    (LinearLayout) getLayoutInflater().inflate(R.layout.home_category_column, categoryContainer, false);
            final Button categoryButton = (Button) col.findViewById(R.id.category_button);
            category.setTextProperties(categoryButton);
            categoryButton.setOnClickListener(getClickListenerForIntent(category));
            row.addView(col);
            colCount++;
        }

        if ((categories.length & 1) != 0) {
            // odd number of categories, need to inflate an empty col
            final LinearLayout col =
                    (LinearLayout) getLayoutInflater().inflate(R.layout.home_column_empty, categoryContainer, false);
            row.addView(col);
        }
    }

    private OnClickListener getClickListenerForIntent(final Category category) {
        return new OnClickListener() {
            @Override
            public void onClick(final View view) {
                final Intent intent = new Intent(view.getContext(), StatsForCategory.class);
                intent.putExtra("category", category);
                startActivity(intent);
            }
        };
    }

    public void addNewStat(View v) {
        //todo new intent to home_add_more_items_category
        // need to figure out how to get the category they picked, maybe from ID? need to set ID on initCategories
    }

    public void myItems(View v) {
        // view my items custom stat category page
    }
}
