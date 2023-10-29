package org.memorizing.model.menu;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

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
                {"add card"},
                {"info"},
                {"back"}
        });
    }

    @Override
    public String getInfoText() {
        return "Some info text for cards menu";
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
    public String getName() {
        return "Cards menu";
    }
}