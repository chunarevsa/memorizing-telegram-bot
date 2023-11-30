package org.memorizing.model;

public enum EStatus {

    BAD_REQUEST("❌ Bad request."),
    SOMETHING_WENT_WRONG("❌ Sorry, something went wrong. We try to manage it rapidly"),
    SUCCESSFULLY("✅ Success "),
    COMPLETE_SET("✅ You made this list!");

    private final String text;

    EStatus(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

}
