package com.example.elderlycareappnoai;

public class AppointmentItem {
    private String documentId;
    private String displayString;
    private boolean isChecked;

    public AppointmentItem(String documentId, String displayString) {
        this.documentId = documentId;
        this.displayString = displayString;
        this.isChecked = false;
    }

    // FIX: Return displayString instead of text
    public String getText() {
        return displayString;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getDisplayString() {
        return displayString;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}