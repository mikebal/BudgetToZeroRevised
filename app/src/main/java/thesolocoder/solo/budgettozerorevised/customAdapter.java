package thesolocoder.solo.budgettozerorevised;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Michael on 2015-06-05.
 */
class customAdapter extends BaseAdapter{

    private LayoutInflater inflator;
    private ArrayList<ListObject> objects;

    public customAdapter(Context context, ArrayList<ListObject> data) {
        inflator = LayoutInflater.from(context);
        this.objects = data;
    }

    public int getCount() {
        return objects.size();
    }

    public ListObject getItem(int position) {
        return objects.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //return super.getView(position, convertView, parent);

        View customView = inflator.inflate(R.layout.custom_row_view, parent, false);


        TextView date = (TextView) customView.findViewById(R.id.list_textview_DATE);
        TextView body = (TextView) customView.findViewById(R.id.list_textview_DISC);
        TextView cost = (TextView) customView.findViewById(R.id.list_textview_COST);

        ListObject item = getItem(position);

        date.setText(item.getDate());
        body.setText(item.getDiscription());
        cost.setText(item.getCost());

        return customView;

    }
}
