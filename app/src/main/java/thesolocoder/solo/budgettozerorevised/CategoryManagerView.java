package thesolocoder.solo.budgettozerorevised;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import java.util.ArrayList;

public class CategoryManagerView extends ActionBarActivity {

    int menuID_ADD;
    ListView categories;
    EditText nameEditor;
    EditText newNameEditor;
    Button   button_rename;
    Button   button_addNewCategory;
    CategoryCustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categorymanager);
        setUpVariables();
        readInCategories();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item=menu.add("Title"); //your desired title here
        item.setIcon(R.mipmap.ic_add_white_48dp); //your desired icon here
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuID_ADD = item.getItemId();
        return super.onCreateOptionsMenu(menu);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        button_addNewCategory.setVisibility(View.VISIBLE);
        newNameEditor.setVisibility(View.VISIBLE);
        button_rename.setVisibility(View.GONE);
        nameEditor.setVisibility(View.GONE);

        return super.onOptionsItemSelected(item);
    }

    private void setUpVariables(){
        categories = (ListView)findViewById(R.id.lvCategories);
        nameEditor = (EditText)findViewById(R.id.editText_renameCategory);
        newNameEditor = (EditText)findViewById(R.id.editText_newCategoryName);
        button_rename = (Button)findViewById(R.id.button_renameCategory);
        button_rename.setOnClickListener(button_renameClicked);
        button_addNewCategory = (Button) findViewById(R.id.button_addCategory);
        button_addNewCategory.setOnClickListener(button_addNewCategory_Clicked);
    }
    public void readInCategories()
    {
        CategoryManager category_manager = new CategoryManager(getApplicationContext());
        ArrayList<String> category_items = category_manager.readInCategories();
        category_items.remove(0);   // Remove Default
        category_items.remove(0);   // Remove New Category
        customAdapter = new CategoryCustomAdapter(this, category_items, button_rename, nameEditor, button_addNewCategory, newNameEditor);
        categories.setAdapter(customAdapter);
        customAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        readInCategories();
    }

    View.OnClickListener button_renameClicked = new View.OnClickListener() {
        public void onClick(View v) {
            int list_index = customAdapter.getLastClickedPosition();
            String original_name = customAdapter.getItem(list_index);
            String edited_name = nameEditor.getText().toString();

            if(!original_name.equals(edited_name))
            {
                CategoryManager categoryManager = new CategoryManager(getApplicationContext());
                if(!categoryManager.isValidCategoryName(edited_name))
                {
                    nameEditor.setTextColor(Color.parseColor("RED"));
                    return;
                }
                categoryManager.renameCategory(original_name, edited_name);
                nameEditor.setTextColor(Color.parseColor("WHITE"));
            }
        }
    };

    View.OnClickListener button_addNewCategory_Clicked = new View.OnClickListener() {
        public void onClick(View v) {
            CategoryManager categoryManager = new CategoryManager(getApplicationContext());
            if(!categoryManager.isValidCategoryName(newNameEditor.getText().toString()))
            {
                nameEditor.setTextColor(Color.parseColor("RED"));
                return;
            }
            categoryManager.addAlphabetically(newNameEditor.getText().toString());
            readInCategories();
        }
    };
}