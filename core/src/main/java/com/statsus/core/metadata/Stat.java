package com.statsus.core.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.statsus.core.R;

import android.app.AlertDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * Stat metadata representing published stat categories users can choose from by default
 *
 * @author john.wright
 * @since May 19 2014
 */
public enum Stat {
    Happy(1, Category.Health, R.drawable.uicons_smiley, 0xFF34a569, R.drawable.icon_bg_green),
    VideoGame(2, Category.Entertainment, R.drawable.uicons_smiley, 0xFF34a569, R.drawable.icon_bg_green),
    Restaurant(3, Category.Food, R.drawable.uicons_smiley, 0xFF34a569, R.drawable.icon_bg_green),
    Drinking(4, Category.Food, R.drawable.uicons_smiley, 0xFF5a4ba7, R.drawable.icon_bg_purple),
    Movies(5, Category.Entertainment, R.drawable.uicons_smiley, 0xFF34a569, R.drawable.icon_bg_green),
    Cards(6, Category.Social, R.drawable.uicons_smiley, 0xFF5a4ba7, R.drawable.icon_bg_purple);

    private static final Map<Category, Set<Stat>> statsByCategory = new HashMap<Category, Set<Stat>>();

    // init statsByCategory lookup map
    static {
        for (final Stat stat : values()) {
            Set<Stat> statsForCategory = statsByCategory.get(stat.category);
            if (statsForCategory == null) {
                // first stat for category, init
                statsForCategory = new HashSet<Stat>();
                statsByCategory.put(stat.category, statsForCategory);
            }
            statsForCategory.add(stat);
        }
    }

    private final int sid;
    private final Category category;
    private final int iconId;
    private final int backgroundColor;
    private int backgroundDrawable;

    Stat(final int sid,
         final Category category,
         final int iconId,
         final int backgroundColor,
         final int backgroundDrawable) {
        this.sid = sid;
        this.category = category;
        this.iconId = iconId;
        this.backgroundColor = backgroundColor;
        this.backgroundDrawable = backgroundDrawable;
    }

    public void setStatImageProperties(final ImageButton view) {
        view.setImageResource(this.iconId);
        view.setBackgroundResource(this.backgroundDrawable);
    }

    public void setBackgroundResource(final Button view) {
        view.setBackgroundResource(this.backgroundDrawable);
        view.setBackgroundColor(this.backgroundColor);
    }

    public void setTextProperties(final Button button) {
        button.setTextColor(this.backgroundColor);
        button.setText(this.toString());
    }

    public int getSid() {
        return this.sid;
    }

    public static Stat getStatFromId(final int sid) {
        for (final Stat stat : values()) {
            if (stat.sid == sid) {
                return stat;
            }
        }
        throw new IllegalArgumentException("no such stat for id " + sid);
    }

    public static Collection<Stat> getStatsForCategory(final Category category) {
        final Set<Stat> stats = statsByCategory.get(category);
        return stats == null ? Collections.EMPTY_LIST : new ArrayList<Stat>(stats);
    }

    public void setNoteButton(final ImageButton button, final boolean hasNote, final AlertDialog alert) {
        if (hasNote) {
            button.setImageResource(R.drawable.note_exists);
        }
        else {
            button.setImageResource(R.drawable.note_na);
        }
        button.setOnClickListener(buildClickListenerForNoteButton(alert));
    }

    private OnClickListener buildClickListenerForNoteButton(final AlertDialog alert) {
        return new OnClickListener() {
            @Override
            public void onClick(final View view) {
                alert.show();
            }
        };
    }

    public static Stat getStatFromString(final String statString) {
        for (final Stat stat : values()) {
            if (stat.toString().equals(statString)) {
                return stat;
            }
        }
        throw new IllegalArgumentException("no stat named " + statString);
    }
}
