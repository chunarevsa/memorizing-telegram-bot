package org.memorizing.model.menu;

import org.memorizing.resource.cardApi.CardStockDto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

public class CardStockUpdateMenu extends AMenu {

    private final CardStockDto cardStock;

    public CardStockUpdateMenu(CardStockDto oldCardStock) {
        this.cardStock = oldCardStock;
    }

    public CardStockDto getCardStock() {
        return cardStock;
    }

    @Override
    public EMenu getCurrentMenu() {
        return EMenu.CARD_STOCK_UPDATE;
    }

    @Override
    public ReplyKeyboardMarkup getKeyboard() {
        return getKeyboardByButtons(new String[][]{
                {"info"},
                {"back"},
        });
    }

    @Override
    public String getInfoText() {
        return "\n" +
                "\"`cardStockName`\": Name will be unique for your list.\n" +
                "\"`description`\": It need for you. I think, you manage it\n" +
                "\"`keyType`\":\"English word\", \"Interview question\" etc.\n" +
                "\"`valueType`\": What do you want to learn. \"Russian translation\", \"My answer\" etc\n" +
                "\"`maxPoint`\": How many correct answers do you need before the card is no longer shown (status = COMPLETED)\n" +
                "\"`testModeIsAvailable`\": Can you type entirely card value? If need it set it true \n" +
                "\"`onlyFromKey`\": Do you need backward submode? It means, you have to remember key by value.\n " +
                "\"English word\" by \"Russian translation\" is acceptable situation. \n" +
                "But \"Interview question\" by \"My answer\" is bad approach. You should set it `false`\n" +
                "\n" +
                "You can read more information by /howItWorks command and /info in testing menu";

    }

    @Override
    public String getText() {
        return "#update-CardStock\n" +
                "{\n" +
                "  \"`cardStockName`\":             \"" + cardStock.getCardStockName() + "\",\n" +
                "  \"`description`\":                   \"" + cardStock.getDescription() + "\",\n" +
                "  \"`keyType`\":                            \"" + cardStock.getKeyType() + "\",\n" +
                "  \"`valueType`\":                         \"" + cardStock.getValueType() + "\",\n" +
                "  \"`maxPoint`\":                           " + cardStock.getMaxPoint() + ",\n" +
                "  \"`testModeIsAvailable`\": \"" + cardStock.getTestModeIsAvailable() + "\",\n" +
                "  \"`onlyFromKey`\":                 \"" + cardStock.getOnlyFromKey() + "\"\n" +
                "}\n" +
                "\n";
    }

    @Override
    public String getTitle() {
        return "*Card stock add menu*\n" +
                "Send me information about your new card stock.\n" +
                "if you need descriptions of these, push the button /info/\n" +
                "Please use this format:\n" +
                "\n";
    }
}
