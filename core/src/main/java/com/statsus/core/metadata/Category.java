package com.statsus.core.metadata;

import android.widget.Button;

/**
 * @author john.wright
 * @since 21
 */
public enum Category {
    Social(1),
    Love(2),
    Sports(3),
    Health(4),
    Entertainment(5),
    Food(6),
    Music(7);

    private final int cid;

    Category(final int cid) {
        this.cid = cid;
    }

    public void setTextProperties(final Button button) {
        button.setText(this.toString());
    }

    public int getCid() {
        return this.cid;
    }

    public static Category getCategoryFromId(final int cid) {
        for (final Category category : values()) {
            if (category.cid == cid) {
                return category;
            }
        }
        throw new IllegalArgumentException("no such category for id " + cid);
    }
}
