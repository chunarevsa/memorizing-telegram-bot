package org.memorizing.model.command;

import java.util.Arrays;

import static org.memorizing.model.ERegularMessages.*;

public enum ECommand {
    START_COMMAND("/start", (WELCOME.getText() + HOW_IT_WORKS.getText())),
    HELP_COMMAND("/help", HELP.getText()),
    HOW_IT_WORKS_COMMAND("/howitworks", HOW_IT_WORKS.getText()),
    ;

    private final String buttonText;
    private final String messageText;

    ECommand(String buttonText, String messageText) {
        this.buttonText = buttonText;
        this.messageText = messageText;
    }

    public String getButtonText() {
        return buttonText;
    }

    public String getMessageText() {
        return messageText;
    }

    public static ECommand getCommandByMessage(String message) {
        return Arrays.stream(ECommand.values())
                .filter(it -> it.buttonText != null && it.buttonText.equals(message))
                .findFirst().orElse(null);
    }
}
