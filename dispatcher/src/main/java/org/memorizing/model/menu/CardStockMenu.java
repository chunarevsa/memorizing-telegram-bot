package org.memorizing.model.menu;

import org.memorizing.resource.cardApi.CardStockDto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import static org.memorizing.model.command.EKeyboardCommand.*;

public class CardStockMenu extends AMenu {
    private final CardStockDto cardStock;

    public CardStockMenu(CardStockDto cardStock) {
        this.cardStock = cardStock;
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
        return getKeyboardByButtons(new String[][]{
                {START_STUDYING.getButtonText()},
                {SHOW_CARDS.getButtonText()},
                {UPDATE_CARD_STOCK.getButtonText(), DELETE_CARD_STOCK.getButtonText()},
                {GET_INFO.getButtonText(), GO_BACK.getButtonText()}
        });
    }

    @Override
    public String getInfoText() {
        return "You can start testing this card stock" +
                "You also can update or delete this card stock";

    }

    @Override
    public String getText() {
        String testModeIsAvailable = "isn't";
        String testBackwardIsAvailable = "";

        if (cardStock.getTestModeIsAvailable()) {
            testModeIsAvailable = "is";
            if (!cardStock.getOnlyFromKey()) testBackwardIsAvailable = "▪ You can learn it backwards";
        }

        return "*"+cardStock.getCardStockName()+"*" + "\n" +
                cardStock.getDescription() + "\n" +
                "▪ Key and value: " + cardStock.getKeyType() + "/" + cardStock.getValueType() + "\n" +
                "▪ Your card is complete when it have "+ cardStock.getMaxPoint() + " points" + "\n" +
                "▪ This card stock "+testModeIsAvailable+" available for test"+ "\n" +
                testBackwardIsAvailable +
                "\n";
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
