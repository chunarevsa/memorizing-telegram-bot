package org.memorizing.model;

public enum Constants {
    WELCOME_MESSAGE("Hello, {name}!\nWelcome!"),
    BAD_REQUEST("Bad request. Try to use bot's keyboard. Please."),
    INFO_MESSAGE("Hi, {name}!\n\nThis is a memorizing bot.\n\nThis service help you with memorizing any items. You can create card stock with two properties. For example: ENGLISH / RUSSIAN \n\nPlease report bugs to @chunarevsea\n\nContent source: https://github.com/enhorse/java-interview"),
    RESET_SUCCESSFUL("Progress has been reset"),
    THATS_ALL("Thanks, champion, that's all.\nPlease send feedback to @chunarevsea\n"),
    SOMETHING_WENT_WRONG("Sorry, something went wrong. We try to manage it rapidly"),
    SUCCESSFULLY("Successfully")

    ;

    private final String value;

    Constants(String s) {
        this.value = s;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
