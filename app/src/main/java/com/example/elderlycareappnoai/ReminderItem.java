package com.example.elderlycareappnoai;

public class ReminderItem {
    private String documentId;
    private String text;
    private boolean isChecked;

    public ReminderItem(String documentId, String text) {
        this.documentId = documentId;
        this.text = text;
        this.isChecked = false;
    }

    // ADD THIS METHOD
    public String getText() {
        return text;
    }

    public String getDocumentId() {
        return documentId;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}