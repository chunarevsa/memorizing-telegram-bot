package org.memorizing.model.menu;

import org.memorizing.resource.cardApi.CardDto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

public class CardMenu extends AMenu {
    private final Integer cardStockId;
    private final CardDto card;

    public CardMenu(Integer cardStockId, CardDto card) {
        this.cardStockId = cardStockId;
        this.card = card;
    }

    public CardDto getCard() {
        return card;
    }

    public Integer getCardStockId() {
        return cardStockId;
    }

    @Override
    public EMenu getCurrentMenu() {
        return EMenu.CARD;
    }

    @Override
    public ReplyKeyboardMarkup getKeyboard() {
        return getKeyboardByButtons(new String[][]{
                {"update card"},
                {"delete card"},
                {"info"},
                {"back"},
        });
    }

    @Override
    public String getInfoText() {
        return "You can update yor card";
    }

    @Override
    public String getText() {
        return "Information: "
                + card.toString();
    }

    @Override
    public InlineKeyboardMarkup getInlineKeyboard() {
        return null;
    }

    @Override
    public String getName() {
        return "Card menu";
    }
}
