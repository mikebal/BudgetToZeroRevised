package thesolocoder.solo.budgettozerorevised;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import java.io.OutputStreamWriter;
import java.text.DecimalFormat;

/**
 * Created by Michael on 2015-09-09.
 */
public class EditBudget extends ActionBarActivity{

    EditText editNewBudget;
    RadioButton editHistoryYes;
    RadioButton editHistoryNo;
    Button editDone;
    Button editCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editbudget);
        setupVariables();

    }

    private void setupVariables(){

        editNewBudget = (EditText) findViewById(R.id.editbudget_newBudget);

        editHistoryYes = (RadioButton) findViewById(R.id.rbEditHistoryYES);
        editHistoryYes.setOnClickListener(HandleEditHistory);

        editHistoryNo = (RadioButton) findViewById(R.id.rbEditHistoryNO);
        editHistoryNo.setOnClickListener(HandleEditHistory);

        editDone = (Button) findViewById(R.id.bEditDone);
        editDone.setOnClickListener(HandleEditDone);

        editCancel = (Button) findViewById(R.id.bEditCancel);
        editCancel.setOnClickListener(HandleEditCancel);
    }

    View.OnClickListener HandleEditHistory = new View.OnClickListener() {
        public void onClick(View v) {
            int clicked = v.getId();
            if (clicked == R.id.rbEditHistoryNO)
                editHistoryYes.setChecked(false);
            else
                editHistoryNo.setChecked(false);
        }
    };

    View.OnClickListener HandleEditDone = new View.OnClickListener() {
        public void onClick(View v) {

            SharedPreferences savedData;
            savedData = getSharedPreferences("savedData", 0);// save data
            SharedPreferences.Editor editor = savedData.edit();


            if(!editNewBudget.getText().toString().equals("")) {
                editor.putString("total_budget", editNewBudget.getText().toString());

                DecimalFormat df = new DecimalFormat("0.00");
                df.setMaximumFractionDigits(2);

                if(savedData.getBoolean("count_direction_up",false))
                    editor.putString("budget_spending", "00.00");

                else
                    editor.putString("budget_spending", df.format(Double.parseDouble(editNewBudget.getText().toString())));

                editor.apply();
                //loadBalance();
            }
            handleClearHistoryLog();


            finish();
        }
    };
    private void handleClearHistoryLog(){
        if(editHistoryYes.isChecked())
        {
            try{
                OutputStreamWriter out = new OutputStreamWriter(openFileOutput("purchaseLog.txt", getApplicationContext().MODE_PRIVATE));
                out.write("");
                out.close();
            } catch (java.io.IOException e) {
                //do something if an IOException occurs.
            }
        }
    }



    View.OnClickListener HandleEditCancel = new View.OnClickListener() {
        public void onClick(View v) {
            finish();
        }
    };
}
