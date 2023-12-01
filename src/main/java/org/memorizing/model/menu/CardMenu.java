package org.memorizing.model.menu;

import org.memorizing.resource.cardApi.CardDto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.Objects;

import static org.memorizing.model.command.EKeyboardCommand.*;

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
                {UPDATE_CARD.getButtonText(), DELETE_CARD.getButtonText()},
                {GET_INFO.getButtonText(), GO_BACK.getButtonText()}
        });
    }

    @Override
    public String getInfoText() {
        return "The Card is your `key` and `value` in chosen the card stock\n" +
                "For example:\n" +
                "The Card \"memory\" has two properties\n" +
                "`key` = \"memory\"\n" +
                "`value` = \"память\"" +
                "\n" +
                "Statuses\n" +
                "▪ `HARD` status means that your last answer was incorrect.\n"+
                "▪ `NORMAL` status means you didn't make mistakes.\n"+
                "▪ `COMPLETED` status means that you increase points\n" +
                "to card stock’s maxPoint ("+maxCardStockPoint+") and you're good!\n"+
                "This card won’t show you during a studying.\n"+
                "If you have `HARD` status in this card, Let's start studying!";
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