package org.memorizing.model.command;

import org.memorizing.model.ERegularMessages;
import org.memorizing.model.menu.AMenu;
import org.memorizing.model.menu.EMenu;
import org.memorizing.model.EMode;
import org.memorizing.resource.cardApi.CardDto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.Arrays;
import java.util.List;

import static org.memorizing.model.command.EKeyboardCommand.*;

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

    public static class TestMenu extends AMenu {
        private final CardDto card;
        private final EMode mode;
        private final List<Integer> ids;

        public TestMenu(CardDto card, EMode mode, List<Integer> ids) {
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
            return EMenu.FORWARD_TESTING;
        }

        @Override
        public ReplyKeyboardMarkup getKeyboard() {
            return getKeyboardByButtons(new String[][]{
                    {SKIP_WORD.getButtonText()},
                    {GET_INFO.getButtonText(), GO_BACK.getButtonText()}
            });
        }

        @Override
        public String getInfoText() {
            return "Some info text from Test menu";
        }

        @Override
        public String getText() {
            if (mode.isFromKeyMode()) {
                return card.getCardKey();
            } else return card.getCardValue();
        }

        @Override
        public String getTitle() {
            return "*Test menu*\n";
        }
    }
}
