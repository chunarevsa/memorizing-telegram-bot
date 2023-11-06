package org.memorizing.model.menu;

import java.util.Arrays;

public enum EKeyboardCommand {
    ADD_CARD_STOCK("add card stock", EMenu.CARD_STOCK_ADD),
    UPDATE_CARD_STOCK("update card stock", EMenu.CARD_STOCK_UPDATE),
    DELETE_CARD_STOCK("delete card stock", null),

    START_STUDYING("start studying", EMenu.MODE),

    START_FORWARD_TESTING("start forward testing", EMenu.FORWARD_TESTING),
    START_BACKWARD_TESTING("start backward testing", EMenu.BACKWARD_TESTING),
    START_FORWARD_SELF_CHECK("start forward self-checking", EMenu.FORWARD_SELF_CHECK),
    START_BACKWARD_SELF_CHECK("start backward self-checking", EMenu.BACKWARD_SELF_CHECK),
    START_FORWARD_MEMORIZING("start forward memorizing", EMenu.FORWARD_MEMORIZING),
    START_BACKWARD_MEMORIZING("start backward memorizing", EMenu.BACKWARD_MEMORIZING),

    SKIP_WORD("skip", null),

    SHOW_CARDS("show cards", EMenu.CARDS),

    ADD_CARD("add card", EMenu.CARD_ADD),
    UPDATE_CARD("update card", EMenu.CARD_UPDATE),
    DELETE_CARD("delete card", null),

    GET_INFO("info", null),
    GO_BACK("back", null);

    private final String buttonText;
    private final EMenu nextMenu;

    EKeyboardCommand(String buttonText, EMenu nextMenu) {
        this.buttonText = buttonText;
        this.nextMenu = nextMenu;
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
