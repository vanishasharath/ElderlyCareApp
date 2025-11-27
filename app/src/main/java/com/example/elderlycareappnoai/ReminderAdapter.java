package com.example.elderlycareappnoai;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class ReminderAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ReminderItem> items;
    private CheckboxStateCallback callback;  // ADD THIS

    // ADD THIS INTERFACE
    public interface CheckboxStateCallback {
        void onCheckboxStateChanged(String documentId, boolean isChecked);
    }

    // MODIFIED CONSTRUCTOR - add callback parameter
    public ReminderAdapter(Context context, ArrayList<ReminderItem> items, CheckboxStateCallback callback) {
        this.context = context;
        this.items = items;
        this.callback = callback;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.reminder_item, parent, false);
            holder = new ViewHolder();
            holder.checkBox = convertView.findViewById(R.id.item_checkbox);
            holder.textView = convertView.findViewById(R.id.item_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ReminderItem item = items.get(position);
        holder.textView.setText(item.getText());

        // Remove listener before setting checked state to prevent unwanted triggers
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(item.isChecked());

        // Set listener to save state when checkbox is clicked
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setChecked(isChecked);
            // Save to SharedPreferences via callback
            if (callback != null) {
                callback.onCheckboxStateChanged(item.getDocumentId(), isChecked);
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        CheckBox checkBox;
        TextView textView;
    }
}