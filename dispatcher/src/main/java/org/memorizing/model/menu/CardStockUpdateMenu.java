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
        return "Copy text start with `#` and edit fields that you want:\n" +
                "Send all fields, even if they haven't changed:\n" +
                "Please use this format:\n" +
                "\n" +
                "#update-CardStock\n" +
                "{\n" +
                "  \"cardStockName\": \""+cardStock.getCardStockName()+"\",\n" +
                "  \"description\": \""+cardStock.getDescription()+"\",\n" +
                "  \"keyType\": \""+cardStock.getKeyType()+"\",\n" +
                "  \"valueType\": \""+cardStock.getValueType()+"\",\n" +
                "  \"maxPoint\": "+cardStock.getMaxPoint()+",\n" +
                "  \"testModeIsAvailable\": \""+cardStock.getTestModeIsAvailable()+"\",\n" +
                "  \"onlyFromKey\": \""+cardStock.getOnlyFromKey()+"\"\n" +
                "}\n" +
                "\n";

    }

    @Override
    public String getText() {
        return "Send me information about your new card stock.\n" +
                "if you need descriptions of these, push the button `info`\n" +
                "Please use this format:\n" +
                "\n" +
                "#update-CardStock\n" +
                "{\n" +
                "  \"cardStockName\": \""+cardStock.getCardStockName()+"\",\n" +
                "  \"description\": \""+cardStock.getDescription()+"\",\n" +
                "  \"keyType\": \""+cardStock.getKeyType()+"\",\n" +
                "  \"valueType\": \""+cardStock.getValueType()+"\",\n" +
                "  \"maxPoint\": "+cardStock.getMaxPoint()+",\n" +
                "  \"testModeIsAvailable\": \""+cardStock.getTestModeIsAvailable()+"\",\n" +
                "  \"onlyFromKey\": \""+cardStock.getOnlyFromKey()+"\"\n" +
                "}\n" +
                "\n";
    }

    @Override
    public String getName() {
        return "Card stock add menu";
    }
}
