package thesolocoder.solo.budgettozerorevised;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Purchase extends ActionBarActivity {

    Button date;
    Button done;
    Button calenderDone;
    Button calenderCancel;
    EditText discription;
    EditText purchase_total;
    TextView date_header;
    TextView discription_header;
    TextView total_header;
    DatePicker calendar;
    Spinner categorys;
    TextView headerNewCategory;
    EditText newCategory;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.purchase);
        setVariableId();
        getCategories();
    }

    private void setVariableId() {

        date_header = (TextView) findViewById(R.id.tvDate);
        discription_header = (TextView) findViewById(R.id.tvDiscription);
        total_header = (TextView) findViewById(R.id.tvTotalHeader);
        //*****************************************************************
        //Variables related to the Calendar showing
        calendar = (DatePicker) findViewById(R.id.datePicker);
        calendar.setVisibility(View.GONE);
        calendar.setSelected(false);

        calenderDone = (Button) findViewById(R.id.bCalenderDone);
        calenderDone.setOnClickListener(handle_calendar_done);
        calenderDone.setVisibility(View.GONE);

        calenderCancel = (Button) findViewById(R.id.bCalenderCancel);
        calenderCancel.setOnClickListener(handle_calendar_cancel);
        calenderCancel.setVisibility(View.GONE);
        //*****************************************************************
        date = (Button) findViewById(R.id.bDate);
        date.setOnClickListener(handle_calendar_view);
        done = (Button) findViewById(R.id.bPurchaseDone);
        done.setOnClickListener(done_clicked);
        discription = (EditText) findViewById(R.id.etDiscription);
        discription.setSelected(false);
        purchase_total = (EditText) findViewById(R.id.etPurchaseTotal);
        purchase_total.setSelected(false);
        //*****************************************************************
        categorys = (Spinner) findViewById(R.id.spinner1);
        categorys.setOnItemSelectedListener(OnCatSpinnerCL);
        headerNewCategory = (TextView) findViewById(R.id.tvNewCategory);
        newCategory = (EditText) findViewById(R.id.etNewCategory);
    }

    View.OnClickListener handle_calendar_view = new View.OnClickListener() {
        public void onClick(View v) {
            hide_for_calender(true);

        }
    };

    View.OnClickListener handle_calendar_done = new View.OnClickListener() {
        public void onClick(View v) {

            String dateStr = Integer.toString(calendar.getMonth()) + "/" + Integer.toString(calendar.getDayOfMonth()) + "/" + Integer.toString(calendar.getYear());
            date.setText(dateStr);
            hide_for_calender(false);

        }
    };
    View.OnClickListener handle_calendar_cancel = new View.OnClickListener() {
        public void onClick(View v) {
            hide_for_calender(false);
        }
    };

    private void hide_for_calender(boolean hide) {
        LinearLayout Done_and_Cancel = (LinearLayout) findViewById(R.id.LinearLayout02);
        if (hide) {
            date_header.setVisibility(View.GONE);
            discription_header.setVisibility(View.GONE);
            total_header.setVisibility(View.GONE);
            date.setVisibility(View.GONE);
            done.setVisibility(View.GONE);
            discription.setVisibility(View.GONE);
            discription.clearFocus();
            purchase_total.setVisibility(View.GONE);
            purchase_total.clearFocus();
            headerNewCategory.setVisibility(View.GONE);
            newCategory.setVisibility(View.GONE);
            categorys.setVisibility(View.GONE);
            calendar.setVisibility(View.VISIBLE);
            calenderCancel.setVisibility(View.VISIBLE);
            calenderDone.setVisibility(View.VISIBLE);
            TextView temp = (TextView) findViewById(R.id.tvCategory);
            temp.setVisibility(View.GONE);
            calendar.setPadding(0, 125, 0, 0);
            Done_and_Cancel.setPadding(0, 100, 0, 0);
        } else {
            date_header.setVisibility(View.VISIBLE);
            discription_header.setVisibility(View.VISIBLE);
            total_header.setVisibility(View.VISIBLE);
            date.setVisibility(View.VISIBLE);
            done.setVisibility(View.VISIBLE);
            discription.setVisibility(View.VISIBLE);
            purchase_total.setVisibility(View.VISIBLE);
            categorys.setVisibility(View.VISIBLE);
            if (categorys.getSelectedItem().toString().equals("New Category")) {
                headerNewCategory.setVisibility(View.VISIBLE);
                newCategory.setVisibility(View.VISIBLE);
            }
            TextView temp = (TextView) findViewById(R.id.tvCategory);
            temp.setVisibility(View.VISIBLE);

            calendar.setVisibility(View.GONE);
            calenderCancel.setVisibility(View.GONE);
            calenderDone.setVisibility(View.GONE);
            calendar.setPadding(0, 0, 0, 0);
            Done_and_Cancel.setPadding(0, 0, 0, 0);
        }
        hideKeyboard();
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        discription.setSelected(false);
        purchase_total.setSelected(false);
    }

    View.OnClickListener done_clicked = new View.OnClickListener() {
        public void onClick(View v) {
            subtract_purchase();
            if (categorys.getSelectedItem().toString().equals("Default")) {
                newCategory.setText("");
            }
            if (!categorys.getSelectedItem().toString().equals("New Category"))
                create_log_entry("purchaseLog.txt", categorys.getSelectedItem().toString());
            if (!newCategory.getText().toString().equals("")) {
                if (validFileName(newCategory.getText().toString())) {
                    create_log_entry(newCategory.getText().toString() + ".txt", newCategory.getText().toString());
                    addCategory(newCategory.getText().toString());
                }
                create_log_entry("purchaseLog.txt", newCategory.getText().toString());
            } else if (!(categorys.getSelectedItemPosition() == 0 || categorys.getSelectedItemPosition() == 1))
                create_log_entry(categorys.getSelectedItem().toString() + ".txt", categorys.getSelectedItem().toString());
            finish();
        }
    };

    private boolean validFileName(String fileName) {
        CategoryManager categoryManager = new CategoryManager(getApplicationContext());
        return categoryManager.isValidCategoryName(fileName);

    }

    private void subtract_purchase() {

        if (purchase_total.getText().toString().equals(""))
            return;

        double balance;
        SharedPreferences savedData;
        savedData = getSharedPreferences("savedData", 0);// save data
        SharedPreferences.Editor editor = savedData.edit();

        balance = Double.parseDouble(savedData.getString("budget_spending", "00.00"));

        DecimalFormat df = new DecimalFormat("0.00");
        df.setMaximumFractionDigits(2);

        if (savedData.getBoolean("count_direction_up", false))
            balance += Double.parseDouble(purchase_total.getText().toString());
        else
            balance -= Double.parseDouble(purchase_total.getText().toString());

        editor.putString("budget_spending", String.valueOf(df.format(balance)));
        editor.apply();
    }

    private void create_log_entry(String fileName, String category) {

        String entry;
        if (date.getText().toString().equals("Today"))
            entry = Integer.toString(calendar.getMonth() + 1) + "/" + Integer.toString(calendar.getDayOfMonth()) + "/" + Integer.toString(calendar.getYear()) + "\n";
        else
            entry = date.getText().toString() + "\n";

        if (discription.getText().toString().equals(""))
            entry += "Purchase\n";
        else
            entry += discription.getText().toString() + "\n";
        if (purchase_total.getText().toString().equals(""))
            return;
        else {
            DecimalFormat df = new DecimalFormat("0.00");
            df.setMaximumFractionDigits(2);
            double dollarAmount = Double.parseDouble(purchase_total.getText().toString());

            entry += String.valueOf(df.format(dollarAmount)) + "\n";

            entry += category + "\n";
        }
        saveLog(entry, fileName);
    }

    public void saveLog(String newEntry, String fileName) {
        try {
            OutputStreamWriter out = new OutputStreamWriter(openFileOutput(fileName, MODE_APPEND));
            out.write(newEntry);
            out.close();
        } catch (java.io.IOException e) {
            //do something if an IOException occurs.
        }
    }

    public void getCategories() {
        CategoryManager manager = new CategoryManager(getApplicationContext());
        ArrayList<String> categoryContent = manager.readInCategories();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categoryContent);
        categorys.setAdapter(adapter);
    }

    public void addCategory(String addedCategory) {
        if (addedCategory.equals("New Category") || addedCategory.equals("Default"))
            return;
        CategoryManager manager = new CategoryManager(getApplicationContext());
        manager.addAlphabetically(addedCategory);
    }

    private AdapterView.OnItemSelectedListener OnCatSpinnerCL = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            if (((TextView) parent.getChildAt(0)).getText().toString().equals("New Category")) {
                headerNewCategory.setVisibility(View.VISIBLE);
                newCategory.setVisibility(View.VISIBLE);
            } else {
                headerNewCategory.setVisibility(View.GONE);
                newCategory.setVisibility(View.GONE);
            }
        }

        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
}
