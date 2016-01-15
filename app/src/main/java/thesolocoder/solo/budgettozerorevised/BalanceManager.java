package thesolocoder.solo.budgettozerorevised;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.text.DecimalFormat;

/**
 * Created by Michael on 2015-09-17.
 */
public class BalanceManager extends Activity {

    public String[] getBalanceInfo(Context context){

        String loaded[] = new String[3];

        SharedPreferences savedData;
        savedData = context.getSharedPreferences("savedData", 0);// save data

        String budget_spending = savedData.getString("budget_spending", "0.00");
        String total_budget = savedData.getString("total_budget", "None");

        loaded[0] = budget_spending;
        loaded[2] = "BLACK";

        if(savedData.getBoolean("count_direction_up",false)) {
            loaded[1] = "▲ Spent out of: $" + total_budget;
            if(!total_budget.equals("None"))
                if(checkIfOverBudget(Double.valueOf(budget_spending), Double.valueOf(total_budget), true))
                    loaded[2] = "RED";
        }
        else {
            loaded[1] ="▼ Remaining out of: $" + total_budget;
            if(!total_budget.equals("None"))
                if(checkIfOverBudget(Double.valueOf(budget_spending), Double.valueOf(total_budget),false))
                    loaded[2] = "RED";
        }
        return loaded;
    }

    private boolean checkIfOverBudget(Double spent, Double budget, Boolean countDirectionUp)
    {
        boolean isOverBudget = false;

        if(countDirectionUp) {
            if (spent > budget)
                isOverBudget = true;
        }
        else
        if (spent < 0)
            isOverBudget = true;

        return isOverBudget;
    }

    public void resetBalance(Context context)
    {
        SharedPreferences savedData;
        savedData = context.getSharedPreferences("savedData", 0);// save data
        SharedPreferences.Editor editor = savedData.edit();

        if(!savedData.getBoolean("rollover_on", false)) {
            if (savedData.getBoolean("count_direction_up", false))
                editor.putString("budget_spending", "00.00");
            else
                saveDoubleAsString(editor, "budget_spending", Double.valueOf(savedData.getString("total_budget", "0.00")));
            editor.apply();
        }
        else
            setBudgetWithRollover(context);
    }

    private void saveDoubleAsString(SharedPreferences.Editor editor, String key, Double value){

        DecimalFormat df = new DecimalFormat("0.00");
        df.setMaximumFractionDigits(2);

        editor.putString(key, df.format(value));
        editor.apply();
    }

    private void setBudgetWithRollover(Context context)
    {
        SharedPreferences savedData;
        savedData = context.getSharedPreferences("savedData", 0);// save data
        SharedPreferences.Editor editor = savedData.edit();
        Boolean reflip = false;

        if(!savedData.getBoolean("count_direction_up", false)) {
            flipBudget(context);
            reflip = true;
        }

        double money_spent = Double.parseDouble(savedData.getString("budget_spending", "0.0"));
        double budget = Double.parseDouble(savedData.getString("total_budget","0.0"));

        double difference = budget - money_spent;

        if(difference < 0)
            difference = 00.00;

        if (savedData.getBoolean("count_direction_up", false))
            editor.putString("budget_spending", String.valueOf(0 - difference));
        else
            saveDoubleAsString(editor, "budget_spending", Double.valueOf(savedData.getString("total_budget", "0.00")) - difference);
        editor.apply();

        if(reflip)
            flipBudget(context);
    }

    public void flipBudget(Context context)
    {
        SharedPreferences savedData;
        savedData = context.getSharedPreferences("savedData", 0);// save data
        SharedPreferences.Editor editor = savedData.edit();

        boolean count_direction_up = savedData.getBoolean("count_direction_up", false);
        double budget_total = Double.valueOf(savedData.getString("total_budget", "00.00"));
        double budget_spent = Double.valueOf(savedData.getString("budget_spending","00.00"));

        saveDoubleAsString(editor, "budget_spending", budget_total - budget_spent);

        if(count_direction_up)
            editor.putBoolean("count_direction_up", false);

        else
            editor.putBoolean("count_direction_up", true);

        editor.putBoolean("requestBudgetFlip", false);
        editor.apply();
    }
}
