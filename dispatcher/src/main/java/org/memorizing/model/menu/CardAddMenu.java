package org.memorizing.model.menu;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import static org.memorizing.model.command.EKeyboardCommand.GET_INFO;
import static org.memorizing.model.command.EKeyboardCommand.GO_BACK;

public class CardAddMenu extends AMenu {

    @Override
    public EMenu getCurrentMenu() {
        return EMenu.CARD_ADD;
    }

    @Override
    public ReplyKeyboardMarkup getKeyboard() {
        return getKeyboardByButtons(new String[][]{
                {GET_INFO.getButtonText(), GO_BACK.getButtonText()}
        });
    }

    @Override
    public String getInfoText() {
        return "The Card stock is a card storage with a one topic.\n" +
                "For example:\n" +
                "The card stock \"English words\" will have\n" +
                "keyType = \"English word\"\n" +
                "valueType = \"Russian translations\"\n" +
                "\n" +
                "You can add a Card to this Card stock\n" +
                "key = \"memory\"\n" +
                "value = \"память\"";
    }

    @Override
    public String getText() {
        return "#add-Card\n"+
                "{\n" +
                "  \"`cardKey`\":   \"provide\",\n" +
                "  \"`cardValue`\": \"предоставлять\"\n" +
                "}\n" +
                "\n";
    }

    @Override
    public String getTitle() {
        return "*Card add menu*\n" +
                "Send me information about a new card stock.\n" +
                "if you need descriptions these, push the button `info`\n" +
                "Please use this format:\n" +
                "\n";
    }
}
