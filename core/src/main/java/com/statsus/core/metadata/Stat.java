package com.statsus.core.metadata;

import com.statsus.core.R;

import android.graphics.drawable.GradientDrawable;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * Stat metadata representing published stat categories users can choose from by default
 *
 * @author john.wright
 * @since May 19 2014
 */
public enum Stat {
    Happy(1, R.drawable.uicons_smiley, 0xFF34a569),
    VideoGames(2, R.drawable.uicons_smiley, 0xFF34a569),
    Restaurant(3, R.drawable.uicons_smiley, 0xFF34a569),
    Drinking(4, R.drawable.uicons_smiley, 0xFF5a4ba7),
    Movies(5, R.drawable.uicons_smiley, 0xFF34a569),
    Cards(6, R.drawable.uicons_smiley, 0xFF5a4ba7);

    private final int sid;
    private final int iconId;
    private final int backgroundColor;

    Stat(final int sid, final int iconId, final int backgroundColor) {
        this.sid = sid;
        this.iconId = iconId;
        this.backgroundColor = backgroundColor;
    }

    public void setStatImageProperties(final ImageButton view) {
        view.setImageResource(this.iconId);
        ((GradientDrawable) view.getBackground()).setColor(this.backgroundColor);
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
}
