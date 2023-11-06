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

    public static class SelfCheckMenu extends AMenu {
        private final CardDto card;
        private final EMode mode;
        private final List<Integer> ids;

        public SelfCheckMenu(CardDto card, EMode mode, List<Integer> ids) {
            this.card = card;
            this.mode = mode;
            this.ids = ids;
        }

        public CardDto getCard() {
            return card;
        }

        public EMode getMode() {
            return mode;
        }

        public List<Integer> getIds() {
            return ids;
        }

        @Override
        public EMenu getCurrentMenu() {
            return null;
        }

        @Override
        public ReplyKeyboardMarkup getKeyboard() {
            return null;
        }

        @Override
        public String getInfoText() {
            return null;
        }

        @Override
        public String getText() {
            return null;
        }

        @Override
        public String getTitle() {
            return null;
        }
    }
}
