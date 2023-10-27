package org.memorizing.model.menu;

import org.memorizing.resource.cardApi.CardStockDto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

public class CardStockMenu extends AMenu {
    private final CardStockDto cardStock;

    public CardStockMenu(CardStockDto cardStock) {
        this.cardStock = cardStock;
    }

    public CardStockDto getCardStock() { return this.cardStock; }

    @Override
    public EMenu getLastMenu() {
        return EMenu.CARD_STOCKS;
    }

    @Override
    public EMenu getCurrentMenu() {
        return EMenu.CARD_STOCK;
    }

    @Override
    public ReplyKeyboardMarkup getKeyboard() {
        return getKeyboardByButtons(new String[][]{
                {"X start studying"},
                {"show cards"},
                {"X edit card stock"},
                {"X delete card stock"},
                {"info"},
                {"go to back"},
        });
    }

    @Override
    public String getInfoText() {
        return "Some info text for Card Stock menu";
    }

    @Override
    public String getText() {
        return cardStock.getKeyType() + "/" + cardStock.getValueType() + "\n" +
                cardStock.toString();
    }

    @Override
    public InlineKeyboardMarkup getInlineKeyboard() {
        return null;
    }

    @Override
    public String getName() {
        return "Card Stock Menu";
    }

}
