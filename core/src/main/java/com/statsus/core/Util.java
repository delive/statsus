package com.statsus.core;

import android.content.Intent;

/**
 * @author john.wright
 * @since 21
 */
public class Util {
    public static void setIntentFlagNoHistory(final Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    public static boolean isNullOrEmpty(final String str) {
        return str == null || str.equals("");
    }
}
