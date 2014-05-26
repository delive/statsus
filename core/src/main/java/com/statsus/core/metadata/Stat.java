package com.statsus.core.metadata;

/**
 * Stat metadata representing published stat categories users can choose from by default
 *
 * @author john.wright
 * @since May 19 2014
 */
public enum Stat {
    Happy(1),
    VideoGames(2),
    Restaurant(3),
    Drinking(4),
    Movies(5),
    Cards(6);

    private final int sid;

    Stat(final int sid) {
        this.sid = sid;
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
