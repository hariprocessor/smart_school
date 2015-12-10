package emos.absence.listview;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import emos.absence.R;


public class CustomAdapter extends BaseAdapter {
    private ArrayList<CustomListViewData> arrayList;

    public CustomAdapter() {
        arrayList = new ArrayList<CustomListViewData>();
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tagIDTextView.setText(arrayList.get(position).getTagID());
        viewHolder.isLateTextView.setText(arrayList.get(position).getIsLate());
        return convertView;
    }

    public void add(String tagID, String isLate) {
        arrayList.add(new CustomListViewData(tagID, isLate));
    }

    public void remove(int _position) {
        arrayList.remove(_position);
    }

    static class ViewHolder {
        @InjectView(R.id.tagIDTextView) TextView tagIDTextView;
        @InjectView(R.id.isLateTextView) TextView isLateTextView;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}