package org.memorizing.model.command;

import org.memorizing.model.menu.AMenu;
import org.memorizing.model.menu.EMenu;
import org.memorizing.model.EMode;
import org.memorizing.resource.cardApi.CardDto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.Arrays;
import java.util.List;

public enum EKeyboardCommand {
    ADD_CARD_STOCK("add card stock", EMenu.CARD_STOCK_ADD),
    UPDATE_CARD_STOCK("update card stock", EMenu.CARD_STOCK_UPDATE),
    DELETE_CARD_STOCK("delete card stock", null),

    START_STUDYING("start studying", EMenu.MODE),

    START_FORWARD_TESTING("forward testing", EMenu.FORWARD_TESTING),
    START_BACKWARD_TESTING("backward testing", EMenu.BACKWARD_TESTING),
    START_FORWARD_SELF_CHECK("forward self-checking", EMenu.FORWARD_SELF_CHECK),
    START_BACKWARD_SELF_CHECK("backward self-checking", EMenu.BACKWARD_SELF_CHECK),
    START_FORWARD_MEMORIZING("forward memorizing", EMenu.FORWARD_MEMORIZING),
    START_BACKWARD_MEMORIZING("backward memorizing", EMenu.BACKWARD_MEMORIZING),

    SKIP("skip", null),
    NEXT("next", null),

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
