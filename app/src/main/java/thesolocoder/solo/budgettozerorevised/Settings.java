package thesolocoder.solo.budgettozerorevised;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import java.text.DecimalFormat;


/**
 * Created by Michael on 2/12/2015.
 */
public class Settings extends ActionBarActivity {

    RadioButton countDirDown;
    RadioButton countDirUp;
    RadioButton showHistoryON;
    RadioButton showHistoryOFF;
    RadioButton resetOFF;
    RadioButton resetDaily;
    RadioButton resetWeekly;
    RadioButton resetBiWeekly;
    RadioButton resetMonthly;
    RadioButton rolloverON;
    RadioButton rolloverOFF;
    TextView    rollover_header;
    TextView    rollover_disc;
    RadioButton deleteHistoryON;
    RadioButton deleteHistoryOFF;
    Button done;
    int resetVariable     = -1;
    int loadedResetPeriod = -1;
    boolean originalCountDirectionUp;
    DateHandler resetHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        setVariableId();
        load_settings();
        setButtonOnClicks();

    }

    private void setVariableId() {
        resetHandler = new DateHandler();
        countDirDown = (RadioButton) findViewById(R.id.rbCountDOWN);
        countDirUp = (RadioButton) findViewById(R.id.rbCountUP);
        showHistoryOFF = (RadioButton) findViewById(R.id.rbShowHistoryOff);
        showHistoryON = (RadioButton) findViewById(R.id.rbShowHistoryOn);
        resetOFF = (RadioButton) findViewById(R.id.rbResetOff);
        resetDaily = (RadioButton) findViewById(R.id.rbResetDaily);
        resetWeekly = (RadioButton) findViewById(R.id.rbResetWeekly);
        resetBiWeekly = (RadioButton) findViewById(R.id.rbResetBiWeekly);
        resetMonthly = (RadioButton) findViewById(R.id.rbResetMonthly);
        done = (Button) findViewById(R.id.bSettingsDone);

        rollover_header = (TextView) findViewById(R.id.tvBudgetRollover_HEADER);
        rollover_disc   = (TextView) findViewById(R.id.tv_rollover_DISCRIPTION);
        rolloverON = (RadioButton) findViewById(R.id.rb_rollover_ON);
        rolloverOFF = (RadioButton) findViewById(R.id.rb_rollover_OFF);

        deleteHistoryON = (RadioButton) findViewById(R.id.rbDeleteHistoryOn);
        deleteHistoryOFF = (RadioButton) findViewById(R.id.rbDeleteHistoryOff);
    }

    private void setButtonOnClicks() {
        countDirDown.setOnClickListener(handle_countDir);
        countDirUp.setOnClickListener(handle_countDir);
        showHistoryOFF.setOnClickListener(handle_showHistory);
        showHistoryON.setOnClickListener(handle_showHistory);
        resetOFF.setOnClickListener(handle_autoBudget);
        resetDaily.setOnClickListener(handle_autoBudget);
        resetWeekly.setOnClickListener(handle_autoBudget);
        resetBiWeekly.setOnClickListener(handle_autoBudget);
        resetMonthly.setOnClickListener(handle_autoBudget);
        done.setOnClickListener(handle_SaveSettings);

        rolloverON.setOnClickListener(handle_rollover);
        rolloverOFF.setOnClickListener(handle_rollover);

        deleteHistoryON.setOnClickListener(handle_deleteHistory);
        deleteHistoryOFF.setOnClickListener(handle_deleteHistory);
    }

    View.OnClickListener handle_countDir = new View.OnClickListener() {
        public void onClick(View v) {
            int clicked = v.getId();
            if (clicked == R.id.rbCountDOWN)
                countDirUp.setChecked(false);
            else
                countDirDown.setChecked(false);
        }
    };
    View.OnClickListener handle_showHistory = new View.OnClickListener() {
        public void onClick(View v) {
            int clicked = v.getId();
            if (clicked == R.id.rbShowHistoryOn)
                showHistoryOFF.setChecked(false);
            else
                showHistoryON.setChecked(false);
        }
    };
    View.OnClickListener handle_autoBudget = new View.OnClickListener() {
        public void onClick(View v) {
            int clicked = v.getId();
            if (clicked == R.id.rbResetOff) {
                resetDaily.setChecked(false);
                resetWeekly.setChecked(false);
                resetBiWeekly.setChecked(false);
                resetMonthly.setChecked(false);
                resetVariable = 0;
                hide_rollover();
            } else if (clicked == R.id.rbResetDaily) {
                resetOFF.setChecked(false);
                resetWeekly.setChecked(false);
                resetBiWeekly.setChecked(false);
                resetMonthly.setChecked(false);
                resetVariable = 1;
                show_rollover();
            } else if (clicked == R.id.rbResetWeekly) {
                resetOFF.setChecked(false);
                resetDaily.setChecked(false);
                resetBiWeekly.setChecked(false);
                resetMonthly.setChecked(false);
                resetVariable = 2;
                show_rollover();
            }
            else if(clicked == R.id.rbResetBiWeekly){
                resetOFF.setChecked(false);
                resetDaily.setChecked(false);
                resetWeekly.setChecked(false);
                resetMonthly.setChecked(false);
                resetVariable = 3;
                show_rollover();
            }
            else {
                resetOFF.setChecked(false);
                resetDaily.setChecked(false);
                resetWeekly.setChecked(false);
                resetBiWeekly.setChecked(false);
                resetVariable = 4;
                show_rollover();
            }
        }
    };
    View.OnClickListener handle_SaveSettings = new View.OnClickListener() {
        public void onClick(View v) {

            SharedPreferences savedData;
            savedData = getSharedPreferences("savedData", 0);// save data
            SharedPreferences.Editor editor = savedData.edit();

            if (countDirUp.isChecked()){
                if (!originalCountDirectionUp)
                    flipBudget();
            } else {
                if (originalCountDirectionUp)
                    flipBudget();
            }

            if (showHistoryON.isChecked())
                editor.putBoolean("show_history_on", true);
            else
                editor.putBoolean("show_history_on", false);

            if (resetVariable != -1)
                editor.putInt("reset_period", resetVariable);

            if(deleteHistoryON.isChecked())
                editor.putBoolean("clearHistoryOnReset",true);
            else
                editor.putBoolean("clearHistoryOnReset", false);

            if(loadedResetPeriod != resetVariable)
                editor.putBoolean("set_new_reset_period", true);

            if(deleteHistoryON.isChecked())
                editor.putBoolean("log_autoreset",true);
            else
                editor.putBoolean("log_autoreset",false);

            if((!resetOFF.isChecked()) && (rolloverON.isChecked()))
                editor.putBoolean("rollover_on", true);
            else
                editor.putBoolean("rollover_on", false);
            editor.apply();
            finish();
        }
    };

    View.OnClickListener handle_rollover = new View.OnClickListener() {
        public void onClick(View v) {
            int clicked = v.getId();
            if (clicked == R.id.rb_rollover_ON)
                rolloverOFF.setChecked(false);
            else
                rolloverON.setChecked(false);
        }
    };

    private void load_settings() {
        SharedPreferences savedData;
        savedData = getSharedPreferences("savedData", 0);// save data

        if (savedData.getBoolean("count_direction_up", false)) {
            countDirUp.setChecked(true);
            originalCountDirectionUp = true;
        } else {
            countDirDown.setChecked(true);
            originalCountDirectionUp = false;
        }
        if (savedData.getBoolean("show_history_on", true))
            showHistoryON.setChecked(true);
        else
            showHistoryOFF.setChecked(true);

        int var = savedData.getInt("reset_period", 0);

        if (var == 0)
            resetOFF.setChecked(true);
        else if (var == 1)
            resetDaily.setChecked(true);
        else if (var == 2)
            resetWeekly.setChecked(true);
        else if (var == 3)
            resetBiWeekly.setChecked(true);
        else
            resetMonthly.setChecked(true);

        if(var == 0)
            hide_rollover();

        if(savedData.getBoolean("log_autoreset", false))
            deleteHistoryON.setChecked(true);
        else
            deleteHistoryOFF.setChecked(true);

        if(savedData.getBoolean("rollover_on",false))
            rolloverON.setChecked(true);
        else
            rolloverOFF.setChecked(true);

        loadedResetPeriod = var;
    }


    private void flipBudget() {
        SharedPreferences savedData;
        savedData = getSharedPreferences("savedData", 0);// save data
        SharedPreferences.Editor editor = savedData.edit();

        boolean count_direction_up = savedData.getBoolean("count_direction_up", false);
        double budget_total = Double.valueOf(savedData.getString("total_budget", "00.00"));
        double budget_spent = Double.valueOf(savedData.getString("budget_spending", "00.00"));

        saveDoubleAsString(editor, "budget_spending", budget_total - budget_spent);

        if (count_direction_up)
            editor.putBoolean("count_direction_up", false);

        else
            editor.putBoolean("count_direction_up", true);

        editor.putBoolean("requestBudgetFlip", false);
        editor.apply();
    }
    private void saveDoubleAsString(SharedPreferences.Editor editor, String key, Double value){

        DecimalFormat df = new DecimalFormat("0.00");
        df.setMaximumFractionDigits(2);

        editor.putString(key, df.format(value));
        editor.apply();
    }

    private void hide_rollover()
    {
        rollover_header.setVisibility(View.GONE);
        rollover_disc.setVisibility(View.GONE);
        rolloverON.setVisibility(View.GONE);
        rolloverOFF.setVisibility(View.GONE);
    }


    private void show_rollover()
    {
        rollover_header.setVisibility(View.VISIBLE);
        rollover_disc.setVisibility(View.VISIBLE);
        rolloverON.setVisibility(View.VISIBLE);
        rolloverOFF.setVisibility(View.VISIBLE);
    }

    View.OnClickListener handle_deleteHistory = new View.OnClickListener() {
        public void onClick(View v) {
            int clicked = v.getId();
            if (clicked == R.id.rbDeleteHistoryOn)
                deleteHistoryOFF.setChecked(false);
            else
                deleteHistoryON.setChecked(false);
        }
    };

}