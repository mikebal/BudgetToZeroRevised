package thesolocoder.solo.budgettozerorevised;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Editor extends ActionBarActivity {

    TextView date;
    TextView description;
    TextView cost;
    Button buttonDeleteEntry;
    Button buttonRefund;
    Button buttonDone;
    Button buttonCancel;
    String category;
    int position;
    String sourceCategory;
    double originalCost;
    ListObject originalItem;
    Spinner categorySpinner;
    String categorySpinnerSelction;
    boolean inital = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor);
        setupVariables();
        setupOnClickListers();
        loadPurchaseLog();
        populatespinner();
    }

    private void setupVariables() {
        date = (TextView) findViewById(R.id.etEditScreenDATE);
        description = (TextView) findViewById(R.id.etEditScreenDESCRIPTION);
        cost = (TextView) findViewById(R.id.etEditScreenCOST);
        buttonDeleteEntry = (Button) findViewById(R.id.bEditDelete);
        buttonRefund = (Button) findViewById(R.id.bEditRefund);
        buttonCancel = (Button) findViewById(R.id.bEditCancel);
        buttonDone = (Button) findViewById(R.id.bEditDone);
        categorySpinner = (Spinner) findViewById(R.id.editCategorySpinner);
    }

    private void setupOnClickListers() {
        buttonDeleteEntry.setOnClickListener(handleDeleteEntry);
        buttonRefund.setOnClickListener(handleRefundClicked);
        buttonCancel.setOnClickListener(handleCancelClicked);
        buttonDone.setOnClickListener(handleDoneClicked);
    }

    View.OnClickListener handleDeleteEntry = new View.OnClickListener() {
        public void onClick(View v) {
            deleteItem(false);
            finish();
        }
    };
    View.OnClickListener handleRefundClicked = new View.OnClickListener() {
        public void onClick(View v) {
            deleteItem(true);
            finish();
        }
    };
    View.OnClickListener handleCancelClicked = new View.OnClickListener() {
        public void onClick(View v) {
            finish();
        }
    };
    View.OnClickListener handleDoneClicked = new View.OnClickListener() {
        public void onClick(View v) {
            if (changesMade()) {
                // createNewObject();
                handleEdit();
            }
            finish();
        }
    };

    private Boolean changesMade() {
        String[] logEntry = getLogEntry();
        Boolean changesMade = false;

        if (!date.getText().toString().equals(logEntry[0]))
            changesMade = true;
        if (!description.getText().toString().equals(logEntry[1]))
            changesMade = true;
        if (!cost.getText().toString().equals(logEntry[2]))
            changesMade = true;
        if(!sourceCategory.equals(categorySpinnerSelction))
            changesMade = true;
        return changesMade;
    }

    private void loadPurchaseLog() {
        String[] logEntry = getLogEntry();
        date.setText(logEntry[0]);
        description.setText(logEntry[1]);
        cost.setText(logEntry[2]);
        originalCost = Double.valueOf(logEntry[2]);
    }

    private String[] getLogEntry() {
        String[] logEntry = new String[3];
        SharedPreferences savedData;
        savedData = getSharedPreferences("savedData", 0);// save data

        logEntry[0] = savedData.getString("toEdit_date", "-1");
        logEntry[1] = savedData.getString("toEdit_disc", "-1");
        logEntry[2] = savedData.getString("toEdit_cost", "-1");
        category = savedData.getString("toEdit_Category", "-1");
        position = savedData.getInt("edit_position", 1);
        sourceCategory = savedData.getString("toEdit_SourceCat", "-1");
        String catData = category;
        originalItem = new ListObject(logEntry[0], logEntry[1], logEntry[2], catData);
        if (sourceCategory.equals("Default"))
            category = "purchaseLog";

        return logEntry;
    }

    private void deleteItem(boolean isRefund) {
        ArrayList<ListObject> logFiles;// = new ArrayList<ListObject>();
        PurchaseLogManager logHandler = new PurchaseLogManager(getApplicationContext());
        logFiles = logHandler.readInPurchaseLog("purchaseLog.txt", false);

        EditManager editHandler = new EditManager(getApplicationContext(), logFiles);
        editHandler.deleteItem(position, !isRefund, sourceCategory);
    }

    private void handleEdit() {
        ArrayList<ListObject> logFiles;// = new ArrayList<ListObject>();
        PurchaseLogManager logHandler = new PurchaseLogManager(getApplicationContext());
        logFiles = logHandler.readInPurchaseLog(sourceCategory + ".txt", false);

        ListObject editedItem = createNewObject();//new ListObject(date.getText().toString(), description.getText().toString(), cost.getText().toString(), category);
        if(!sourceCategory.equals(categorySpinnerSelction))
        {
            deleteItem(true);
            BalanceManager balanceManager = new BalanceManager();
            balanceManager.subtract_purchase(cost.getText().toString(), getApplicationContext());
            create_log_entry("purchaseLog.txt", categorySpinnerSelction);
            if(!categorySpinner.equals("Default"))
                create_log_entry(categorySpinnerSelction + ".txt", categorySpinnerSelction);
            finish();
        }
        EditManager editHandler = new EditManager(getApplicationContext(), logFiles);
        double priceDifference = Double.valueOf(cost.getText().toString()) - originalCost;
        editHandler.handleEdit(sourceCategory, editedItem, originalItem, priceDifference, position);
    }

    private ListObject createNewObject() {

        DecimalFormat df = new DecimalFormat("0.00");
        df.setMaximumFractionDigits(2);
        String newCost = df.format(Double.parseDouble(cost.getText().toString()));

        ListObject newItem = new ListObject(date.getText().toString(),
                description.getText().toString(), newCost, category);
        return newItem;
    }

    private void populatespinner() {
        CategoryManager categoryManager = new CategoryManager(getApplicationContext());
        ArrayList<String> categorys = categoryManager.readInCategories();
        categorys.remove(1); // Remove category "New Category"
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categorys);
        categorySpinner.setAdapter(adapter);
        final int index = getIndexOfExistingCategory(categorys);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos,
                                       long id) {
                // TODO Auto-generated method stub
                if (index != -1 && inital) {
                    parent.setSelection(index);
                    inital = false;
                }
               else
                parent.setSelection(pos);
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                ((TextView) parent.getChildAt(0)).setTextSize(16);
                categorySpinnerSelction = ((TextView) parent.getChildAt(0)).getText().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

    }
    private int getIndexOfExistingCategory(ArrayList<String> categorys){
        int index = -1;

        for(int i = 0; i < categorys.size(); i++)
        {
            if(categorys.get(i).equals(originalItem.getCategory())) {
                index = i;
                break;
            }
        }
        return index;
    }
    private void create_log_entry(String fileName, String category) {

        String entry;
        if (date.getText().toString().equals(""))
            entry = "xx/xx/xxxx\n";
//            entry = Integer.toString(calendar.getMonth() + 1) + "/" + Integer.toString(calendar.getDayOfMonth()) + "/" + Integer.toString(calendar.getYear()) + "\n";
        else
            entry = date.getText().toString() + "\n";

        if (description.getText().toString().equals(""))
            entry += "Purchase\n";
        else
            entry += description.getText().toString() + "\n";
        if (cost.getText().toString().equals(""))
            return;
        else {
            DecimalFormat df = new DecimalFormat("0.00");
            df.setMaximumFractionDigits(2);
            double dollarAmount = Double.parseDouble(cost.getText().toString());

            entry += String.valueOf(df.format(dollarAmount)) + "\n";

            entry += categorySpinnerSelction + "\n";
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
}