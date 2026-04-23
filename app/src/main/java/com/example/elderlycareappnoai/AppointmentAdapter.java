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

public class AppointmentAdapter extends ArrayAdapter<AppointmentItem> {

    public AppointmentAdapter(Context context, List<AppointmentItem> appointmentItems) {
        super(context, 0, appointmentItems);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        AppointmentItem appointmentItem = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_with_checkbox, parent, false);
        }

        TextView itemText = convertView.findViewById(R.id.item_text);
        CheckBox itemCheckbox = convertView.findViewById(R.id.item_checkbox);

        if (appointmentItem != null) {
            itemText.setText(appointmentItem.getDisplayString());
            itemCheckbox.setChecked(appointmentItem.isChecked());

            // Update the state in the data object when checked
            itemCheckbox.setOnClickListener(v -> {
                appointmentItem.setChecked(itemCheckbox.isChecked());
            });
        }

        return convertView;
    }
}
