package thesolocoder.solo.budgettozerorevised;

import android.content.Intent;
import android.widget.BaseAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by Michael on 1/6/2016.
 */
public class CategoryCustomAdapter extends BaseAdapter {

    private LayoutInflater inflator;
    private ArrayList<String> categorys;
    final private Context appcontext;
    private Button    renameButton;
    private EditText nameEditor;
    private int  lastClickedPosition = -1;
    private Button hideButton;
    private EditText hideEditText;

    public CategoryCustomAdapter(Context context, ArrayList<String> data, Button button, EditText editText, Button hideButton, EditText hideEditText) {
        inflator = LayoutInflater.from(context);
        this.categorys = data;
        this.appcontext = context;
        this.renameButton = button;
        this.nameEditor = editText;
        this.hideButton = hideButton;
        this.hideEditText = hideEditText;
    }

    public int getCount() {
        return categorys.size();
    }

    public String getItem(int position) {
        return categorys.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public int getLastClickedPosition(){ return lastClickedPosition; }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View customView = inflator.inflate(R.layout.categorylistview, parent, false);

        final TextView user_category = (TextView) customView.findViewById(R.id.list_category);
        String item = getItem(position);
        user_category.setText(item);
        user_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastClickedPosition = position;
                nameEditor.setVisibility(View.VISIBLE);
                nameEditor.setText(user_category.getText());
                renameButton.setVisibility(View.VISIBLE);

                hideButton.setVisibility(View.GONE);
                hideEditText.setVisibility(View.GONE);
            }
        });


        ImageButton btnCheckin = (ImageButton) customView.findViewById(R.id.list_deleteButton);

        btnCheckin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openDeletePopup = new Intent(appcontext, CategoryDeletePopup.class);
                openDeletePopup.putExtra("targetCategory", user_category.getText().toString());
                appcontext.startActivity(openDeletePopup);

            }
        });

        return customView;
    }
}
