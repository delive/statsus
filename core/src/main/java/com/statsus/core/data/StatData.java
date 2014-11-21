package com.statsus.core.data;

/**
 * @author john.wright
 * @since 21
 */
public class StatData {
    public static final StatData EMPTY = new StatData() {
        @Override
        public boolean isEmpty() {
            return true;
        }
    };

    private int total;
    private int totalCount;

    public boolean isEmpty() {
        return false;
    }

    public void setTotal(final int total) {
        this.total = total;
    }

    public int getTotalCount() {
        return totalCount;
    }
}
