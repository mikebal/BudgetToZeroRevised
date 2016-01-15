package thesolocoder.solo.budgettozerorevised;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import java.io.OutputStreamWriter;

/**
 * Created by Michael on 2015-09-17.
 */
public class BudgetResetManager extends Activity {

    public Boolean budgetResetHandler(Context context)
    {
        DateHandler autoResetHandler = new DateHandler();

        Boolean dateResetNeeded = false;
        if(autoResetHandler.isResetRequired(context)) {
            resetBalance(context);
            logResetHandler(context);
            dateResetNeeded = true;
        }
        return dateResetNeeded;
    }

    public void resetBalance(Context context)
    {
        BalanceManager resetHandler = new BalanceManager();
        resetHandler.resetBalance(context);

    }
    private void logResetHandler(Context context)
    {
        SharedPreferences savedData;
        savedData = context.getSharedPreferences("savedData", 0);// save data

        if(savedData.getBoolean("log_autoreset",false))
        {
            try{
                OutputStreamWriter out = new OutputStreamWriter(context.openFileOutput("purchaseLog.txt", context.MODE_PRIVATE));
                out.write("");
                out.close();

            } catch (java.io.IOException e) {
                //do something if an IOException occurs.
            }
        }

    }

    public void newBudgetAutoResetDateHandler(Boolean forceRest, Context context)
    {
        SharedPreferences savedData;
        savedData = context.getSharedPreferences("savedData", 0);// save data
        SharedPreferences.Editor editor = savedData.edit();

        DateHandler autoResetHandler = new DateHandler();

        if(savedData.getBoolean("set_new_reset_period", false) || forceRest)
        {
            int newResetPeriod = savedData.getInt("reset_period", 0);

            if(newResetPeriod == 0)
                autoResetHandler.setAutoResetOFF(context);
            else if(newResetPeriod == 1)
                autoResetHandler.setAutoResetDAYLIE(context);
            else if(newResetPeriod == 2)
                autoResetHandler.setAutoResetWEEK(context);
            else if(newResetPeriod == 3)
                autoResetHandler.setAutoResetTWOWEEK(context);
            else
                autoResetHandler.setAutoResetMonth(context);

            editor.putBoolean("set_new_reset_period", false);
            editor.apply();
        }
    }
}
