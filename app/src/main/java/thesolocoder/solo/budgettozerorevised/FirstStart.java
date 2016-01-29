package thesolocoder.solo.budgettozerorevised;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.widget.EditText;
import java.text.DecimalFormat;

/**
 * Created by Michael on 2015-11-30.
 */
public class FirstStart extends ActionBarActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handleFirstStart();
    }
    private void handleFirstStart(){
        SharedPreferences savedData;
        savedData = getSharedPreferences("savedData", 0);// save data
        final SharedPreferences.Editor editor = savedData.edit();

        if((savedData.getString("total_budget", "None").equals("None"))) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle("Welcome new user!");
            alert.setMessage("Enter your desired budget:");

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            input.setKeyListener(DigitsKeyListener.getInstance(false, true));
            alert.setView(input);

            alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String value = input.getText().toString();
                    if (!value.equals("")) {

                        DecimalFormat df = new DecimalFormat("0.00");
                        df.setMaximumFractionDigits(2);

                        double numValue = Double.parseDouble(value);

                        editor.putString("total_budget", String.valueOf(df.format(numValue)));
                        editor.putString("budget_spending", String.valueOf(df.format(numValue)));
                        editor.putBoolean("NewFileFormatNeeded", false);
                        editor.apply();
                        finish();
                    } else
                        handleFirstStart();
                    // Do something with value!
                }
            });
            alert.create();
            alert.show();
        }

    }
}
