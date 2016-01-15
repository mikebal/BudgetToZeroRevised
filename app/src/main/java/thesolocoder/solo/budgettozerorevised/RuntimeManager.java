package thesolocoder.solo.budgettozerorevised;

import android.content.Context;

/**
 * Created by Michael on 2015-09-11.
 */
public class RuntimeManager {

    public String getAutoResetRemaning(Context context)
    {
        DateHandler dateManager = new DateHandler();
        String daysRemaing = "";

        // int daysRemaining = dateManager.daysUntilReset(context);
        int daysRemaining = dateManager.badDateCounter(context);
        if(daysRemaining <= 31)
            daysRemaing = "Days remaining: " + Integer.toString(daysRemaining);

        return daysRemaing;

    }
}
