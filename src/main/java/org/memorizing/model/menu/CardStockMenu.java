package org.memorizing.model.menu;

import org.memorizing.resource.cardApi.CardDto;
import org.memorizing.resource.cardApi.CardStockDto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.lang.reflect.Array;
import java.util.List;

import static org.memorizing.model.command.EKeyboardCommand.*;

public class CardStockMenu extends AMenu {
    private final CardStockDto cardStock;
    private final List<CardDto> cards;

    public CardStockMenu(CardStockDto cardStock, List<CardDto> cards) {
        this.cardStock = cardStock;
        this.cards = cards;
    }

    public CardStockDto getCardStock() {
        return this.cardStock;
    }

    @Override
    public EMenu getCurrentMenu() {
        return EMenu.CARD_STOCK;
    }

    @Override
    public ReplyKeyboardMarkup getKeyboard() {
        if (cards == null || cards.isEmpty()) {
            return getKeyboardByButtons(new String[][]{
                    {ADD_CARD.getButtonText()},
                    {UPDATE_CARD_STOCK.getButtonText(), DELETE_CARD_STOCK.getButtonText()},
                    {GET_INFO.getButtonText(), GO_BACK.getButtonText()}
            });
        } else {
            return getKeyboardByButtons(new String[][]{
                    {START_STUDYING.getButtonText()},
                    {SHOW_CARDS.getButtonText()},
                    {UPDATE_CARD_STOCK.getButtonText(), DELETE_CARD_STOCK.getButtonText()},
                    {GET_INFO.getButtonText(), GO_BACK.getButtonText()}
            });
        }
    }

    @Override
    public String getInfoText() {
        return "You can start studying this card stock.\n" +
                "You also can update or delete this card stock.";

    }

    @Override
    public String getText() {
        String testModeIsAvailable = cardStock.getTestModeIsAvailable() ? "is" : "isn't";
        String testBackwardIsAvailable = cardStock.getTestModeIsAvailable() || !cardStock.getOnlyFromKey()
                ? "▪ You can learn it backwards.\n"
                : "";

        String size = cards == null || cards.isEmpty()
                ? "▪ You don't have cards\n"
                : "▪ You have "+cards.size() +" cards\n";

        String statuses = "";
        if (cards != null && !cards.isEmpty() && cardStock.getTestModeIsAvailable()) {
            long normalCountFromKey = 0;
            long hardCountFromKey = 0;
            long completedCountFromKey = 0;

            long normalCountToKey = 0;
            long hardCountToKey = 0;
            long completedCountToKey = 0;

            for (CardDto card : cards) {
                if (card.getStatusFromKey().equals("NORMAL")) normalCountFromKey++;
                if (card.getStatusFromKey().equals("HARD")) hardCountFromKey++;
                if (card.getStatusFromKey().equals("COMPLETED")) completedCountFromKey++;

                if (!cardStock.getOnlyFromKey()) {
                    if (card.getStatusToKey().equals("NORMAL")) normalCountToKey++;
                    if (card.getStatusToKey().equals("HARD")) hardCountToKey++;
                    if (card.getStatusToKey().equals("COMPLETED")) completedCountToKey++;
                }
            }

            statuses = "  Status        - (from key)"+ (!cardStock.getOnlyFromKey() ? " / (to key):\n" : ":\n") +
                    "    `NORMAL`       - ("+normalCountFromKey+")" + (!cardStock.getOnlyFromKey() ? " / ("+normalCountToKey+")\n" : ":\n") +
                    "    `HARD`             - ("+hardCountFromKey+")" + (!cardStock.getOnlyFromKey() ? " / ("+hardCountToKey+")\n" : ":\n") +
                    "    `COMPLETED` - ("+completedCountFromKey+")" + (!cardStock.getOnlyFromKey() ? " / ("+completedCountToKey+")\n" : ":\n");

        }


        return "*"+cardStock.getCardStockName()+"*\n" +
                "▪ Description: " +cardStock.getDescription() + "\n" +
                "▪ Key and value: " + cardStock.getKeyType() + "/" + cardStock.getValueType() + "\n" +
                "▪ Your card is complete when it has "+ cardStock.getMaxPoint() + " points.\n" +
                "▪ This card stock "+testModeIsAvailable+" available for testing mode.\n" +
                testBackwardIsAvailable +
                size +
                statuses;
    }

    @Override
    public InlineKeyboardMarkup getInlineKeyboard() {
        return null;
    }

    @Override
    public String getTitle() {
        return "*Card stock menu*";
    }

}
