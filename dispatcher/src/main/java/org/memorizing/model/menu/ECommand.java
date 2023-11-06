package org.memorizing.model.menu;

import org.memorizing.model.ERegularMessages;

import java.util.Arrays;

public enum ECommand {
    START("/start", (ERegularMessages.WELCOME.getText() + ERegularMessages.HOW_IT_WORKS.getText())),
    HELP("/help", ERegularMessages.HELP.getText()),
    HOW_IT_WORKS("/howitworks", ERegularMessages.HOW_IT_WORKS.getText()),
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
