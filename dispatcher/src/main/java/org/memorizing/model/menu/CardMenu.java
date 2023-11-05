package org.memorizing.model.menu;

import org.memorizing.resource.cardApi.CardDto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.Objects;

public class CardMenu extends AMenu {
    private final Integer cardStockId;
    private final CardDto card;
    private final int maxCardStockPoint;

    public CardMenu(Integer cardStockId, int maxCardStockPoint ,CardDto card) {
        this.cardStockId = cardStockId;
        this.maxCardStockPoint = maxCardStockPoint;
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
                {"info", "back"}
        });
    }

    @Override
    public String getInfoText() {
        return "Card is your `key` and `value` in chosen card stock\n" +
                "For example:\n" +
                "Card \"memory\" have two properties\n" +
                "`key` = \"memory\"\n" +
                "`value` = \"память\"" +
                "\n" +
                "Statuses\n" +
                "▪ `HARD` status means that your last answer was incorrect\n"+
                "▪ `NORMAL` status you didn't make mistakes \n"+
                "▪ `COMPLETED` status means that you didn't make mistakes \n"
                +maxCardStockPoint+" times (`max point` in this card stock) and you're good!\n"+
                "This card won't show you in test mode\n"+
                "If you have `HARD` status in this card, Let's start test";
    }

    @Override
    public String getText() {
        String backwardString = "";
        if (!Objects.equals(card.getStatusToKey(), "NO_SUPPORTED")) {
            backwardString = "▪ backward (value->key): `" + card.getStatusToKey() + "` (" + card.getPointToKey() + ")\n";
        }

        return card.getCardKey() + " = " + card.getCardValue() + "\n" +
                "\n" +
                "▪ forward (key->value): `" + card.getStatusFromKey() + "` (" + card.getPointFromKey() + ")\n" +
                backwardString +
                "\n";

    }

    @Override
    public InlineKeyboardMarkup getInlineKeyboard() {
        return null;
    }

    @Override
    public String getTitle() {
        return "*Card menu*";
    }
}
