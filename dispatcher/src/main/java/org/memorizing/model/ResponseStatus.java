package org.memorizing.model;

public enum ResponseStatus {

    BAD_REQUEST("❌ Bad request."),
    SOMETHING_WENT_WRONG("❌ Sorry, something went wrong. We try to manage it rapidly"),
    SUCCESSFULLY("✅ Success ");

    private final String value;

    ResponseStatus(String s) {
        this.value = s;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
