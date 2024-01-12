package org.memorizing.model.command;

import org.memorizing.model.menu.EMenu;

import java.util.Arrays;

public enum EKeyboardCommand {
    ADD_CARD_STOCK("add card stock", EMenu.CARD_STOCK_ADD),
    UPDATE_CARD_STOCK("update card stock", EMenu.CARD_STOCK_UPDATE),
    DELETE_CARD_STOCK("delete card stock"),

    START_STUDYING("start studying", EMenu.MODE),

    START_FORWARD_TESTING("forward testing", EMenu.FORWARD_TESTING),
    START_BACKWARD_TESTING("backward testing", EMenu.BACKWARD_TESTING),
    START_FORWARD_SELF_CHECK("forward self-checking", EMenu.FORWARD_SELF_CHECK),
    START_BACKWARD_SELF_CHECK("backward self-checking", EMenu.BACKWARD_SELF_CHECK),
    START_FORWARD_MEMORIZING("forward memorizing", EMenu.FORWARD_MEMORIZING),
    START_BACKWARD_MEMORIZING("backward memorizing", EMenu.BACKWARD_MEMORIZING),

    SKIP("skip"),
    NEXT("next"),

    GO_TO_CARD("go to card"),
    SHOW_CARDS("show cards", EMenu.CARDS),

    ADD_CARD("add card", EMenu.CARD_ADD),
    UPDATE_CARD("update card", EMenu.CARD_UPDATE),
    DELETE_CARD("delete card"),

    GET_INFO("info"),
    GO_BACK("back");

    private final String buttonText;
    private final EMenu nextMenu;

    EKeyboardCommand(String buttonText, EMenu nextMenu) {
        this.buttonText = buttonText;
        this.nextMenu = nextMenu;
    }
    EKeyboardCommand(String buttonText) {
        this.buttonText = buttonText;
        this.nextMenu = null;
    }

    public String getButtonText() {
        return buttonText;
    }

    public EMenu getNextMenu() {
        return nextMenu;
    }

    public static EKeyboardCommand getKeyboardCommandByMessage(String message) {
        return Arrays.stream(EKeyboardCommand.values())
                .filter(it -> it.buttonText.equals(message))
                .findFirst().orElse(null);
    }
}
