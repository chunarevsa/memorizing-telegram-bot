package org.memorizing.model.menu;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

public class CardStocksMenu extends AMenu {

    InlineKeyboardMarkup inlineKeyboard;

    public CardStocksMenu(String[][] strings) {
        this.inlineKeyboard = createInlineKeyboard(strings);
    }

    @Override
    public InlineKeyboardMarkup getInlineKeyboard() {
        return inlineKeyboard;
    }

    @Override
    public String getTitle() {
        return "*Card stocks menu*";
    }

    @Override
    public EMenu getCurrentMenu() {
        return EMenu.CARD_STOCKS;
    }

    @Override
    public ReplyKeyboardMarkup getKeyboard() {
        return getKeyboardByButtons(new String[][]{
                {"add card stock"},
                {"info"}
        });
    }

    @Override
    public String getInfoText() {
        return "Some info text for Card Stocks Menu";
    }

    @Override
    public String getText() {
        if (!inlineKeyboard.getKeyboard().isEmpty()) {
            int size = inlineKeyboard.getKeyboard().get(0).size();
            return String.format("You have %s card stocks", size);
        } else return "You don't have card stocks. " +
                "Push the button `add card stock`";
    }
}
