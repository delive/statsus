package com.statsus.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.statsus.core.metadata.Category;
import com.statsus.core.metadata.Stat;
import com.statsus.core.persistence.LocalPersistenceManager;

import android.app.Activity;
import android.content.Context;
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

    public static Collection<Stat> getStatTypesNotSelected(final Category category, final Context context) {
        final Collection<Stat> statsSelected = LocalPersistenceManager.getUserSelectedStats(context);
        final Collection<Stat> stats =
                category == null ? new ArrayList<Stat>(Arrays.asList(Stat.values())) : Stat.getStatsForCategory(category);

        for (final Stat removeStat : statsSelected) {
            stats.remove(removeStat);
        }
        return stats;
    }

    public static void goHomeDirectly(final Activity activity) {
        final Intent intent = new Intent(activity, Home.class);
        setIntentFlagNoHistory(intent);
        activity.startActivity(intent);
    }
}
