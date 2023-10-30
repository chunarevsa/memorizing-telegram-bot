package org.memorizing.model.menu;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

public class CardAddMenu extends AMenu {

    @Override
    public EMenu getCurrentMenu() {
        return EMenu.CARD_ADD;
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
        return "Some info text";
    }

    @Override
    public String getText() {
        return "#add-Card\n"+
                "{\n" +
                "  \"`cardKey`\":     \"provide\",\n" +
                "  \"`cardValue`\":    \"предоставлять\"\n" +
                "}\n" +
                "\n";
    }

    @Override
    public String getTitle() {
        return "*Card add menu*\n" +
                "Send me information about your new card stock.\n" +
                "if you need descriptions these, push the button `info`\n" +
                "Please use this format:\n" +
                "\n";
    }
}
