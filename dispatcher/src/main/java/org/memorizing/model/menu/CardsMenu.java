package org.memorizing.model.menu;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import static org.memorizing.model.command.EKeyboardCommand.*;

public class CardsMenu extends AMenu {
    private final Integer cardStockId;
    private final InlineKeyboardMarkup inlineKeyboard;

    public CardsMenu(Integer cardStockId, String[][] strings) {
        this.cardStockId = cardStockId;
        this.inlineKeyboard = createInlineKeyboard(strings);
    }

    public Integer getCardStockId() {
        return cardStockId;
    }

    @Override
    public EMenu getCurrentMenu() {
        return EMenu.CARDS;
    }

    @Override
    public ReplyKeyboardMarkup getKeyboard() {
        return getKeyboardByButtons(new String[][]{
                {ADD_CARD.getButtonText()},
                {GET_INFO.getButtonText(), GO_BACK.getButtonText()}
        });
    }

    @Override
    public String getInfoText() {
        return "The Card is your `key` and `value` in chosen the card stock.\n" +
                "For example:\n" +
                "Card \"memory\" has two properties\n" +
                "`key` = \"memory\"\n" +
                "`value` = \"память\"\n" +
                "\n" +
                "If you want to manage a card, Just open it.\n";
    }

    @Override
    public String getText() {
        if (!inlineKeyboard.getKeyboard().isEmpty()) {
            int size = inlineKeyboard.getKeyboard().get(0).size();
            return String.format("You have %s cards", size);
        } else return "You don't have cards. " +
                "Push the button `add card`";
    }

    @Override
    public InlineKeyboardMarkup getInlineKeyboard() {
        return inlineKeyboard;
    }

    @Override
    public String getTitle() {
        return "*Cards menu*";
    }
}
