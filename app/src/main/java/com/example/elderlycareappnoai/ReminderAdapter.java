package com.example.elderlycareappnoai;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

public class ReminderAdapter extends ArrayAdapter<ReminderItem> {

    public ReminderAdapter(Context context, List<ReminderItem> reminderItems) {
        super(context, 0, reminderItems);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ReminderItem reminderItem = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_with_checkbox, parent, false);
        }

        TextView itemText = convertView.findViewById(R.id.item_text);
        CheckBox itemCheckbox = convertView.findViewById(R.id.item_checkbox);

        if (reminderItem != null) {
            // --- DEFINITIVE FIX: Use getText() instead of getDisplayString() ---
            itemText.setText(reminderItem.getText());
            itemCheckbox.setChecked(reminderItem.isChecked());

            // Update the state in the data object when checked
            itemCheckbox.setOnClickListener(v -> {
                reminderItem.setChecked(itemCheckbox.isChecked());
            });
        }

        return convertView;
    }
}
