package thesolocoder.solo.budgettozerorevised;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

public class CategoryDeletePopup extends Activity {

    TextView    deleteHeader;
    ImageButton deleteOptions;
    Button      buttonConfirm;
    Button      buttonCancel;
    String      targetCategory;
    CheckBox    checkBoxDeleteAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categorydeletepopup);
        setUpVariables();
    }

    private void setUpVariables()
    {
        deleteHeader = (TextView) findViewById(R.id.tv_header_delete);
        deleteOptions = (ImageButton)findViewById(R.id.imageButton_popupDropDown);
        deleteOptions.setOnClickListener(showOptionsSelected);
        buttonConfirm = (Button)findViewById(R.id.button_confirmDelete);
        buttonConfirm.setOnClickListener(confirmDeleteClicked);
        buttonCancel = (Button) findViewById(R.id.button_cancelDelete);
        buttonCancel.setOnClickListener(cancelClicked);
        checkBoxDeleteAll = (CheckBox) findViewById(R.id.checkBox_deleteAllRecords);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            targetCategory = extras.getString("targetCategory");
        }
        else
            finish();
    }
    View.OnClickListener confirmDeleteClicked = new View.OnClickListener() {
        public void onClick(View v) {
            CategoryManager categoryManager = new CategoryManager(getApplicationContext());

            categoryManager.removeSingleCategory(targetCategory, checkBoxDeleteAll.isChecked());
            finish();
        }
    };
    View.OnClickListener cancelClicked = new View.OnClickListener() {
        public void onClick(View v) {
            finish();
        }
    };
    View.OnClickListener showOptionsSelected = new View.OnClickListener() {
        public void onClick(View v) {
            if(checkBoxDeleteAll.getVisibility() == View.GONE)
                checkBoxDeleteAll.setVisibility(View.VISIBLE);
            else {
                checkBoxDeleteAll.setVisibility(View.GONE);
                checkBoxDeleteAll.setChecked(false);
            }
        }
    };
}