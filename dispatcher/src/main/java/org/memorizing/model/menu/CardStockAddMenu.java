package org.memorizing.model.menu;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

public class CardStockAddMenu extends AMenu {

    @Override
    public EMenu getCurrentMenu() {
        return EMenu.CARD_STOCK_ADD;
    }

    @Override
    public EMenu getLastMenu() {
        return EMenu.CARD_STOCKS;
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
        return "For example:\n" +
                "Our native language is Russian. We want to learn English.\n" +
                "We should add a new card stock `ENGLISH/RUSSIAN`.\n" +
                "It means, we have to add english words (keys) and these translations (values)\n" +
                "\n" +
                "We also want to have test mode. In this mode, you will get only english word (key) and have to type your answer\n" +
                "This mode has name `Form key` because we try to remember a translate (value) by a word (key) -> `From key`\n" +
                "If your answer will be correct your card increase a point `From key point` and your card status will become `NORMAL`\n" +
                "If your answer won't be correct your card decrease a point `From key point` and your card status will become `HARD`\n" +
                "Status NORMAL can have only positive numbers. Status HARD can have only negative numbers\n" +
                "If you got a correct answer to card with HARD status (for example: From key point = -5) \n" +
                "This card become `Normal` and will have `From key point` = 1. It works the other way, too.\n" +
                "\n"+
                "We also want to have inverse mode `From value` (remember a word (key) by a translate (value) you need to declare `only from key` = false\n" +
                "You have to declare max point. When your card will have `From key point` equals this value Your card will become status `COMPLETE`\n" +
                "This card won't show you again in this test mode. We recommend set this value from 4 to 8\n" +
                "We need to send this message:\n" +
                "\n" +
                "#add-cardStock\n"+
                "{\n" +
                "  \"name\": \"English words\",\n" +
                "  \"description\": \"Some description\",\n" +
                "  \"keyType\": \"English\",\n" +
                "  \"valueType\": \"Russian\",\n" +
                "  \"maxPoint\": 5,\n" +
                "  \"testModeIsAvailable\": \"true\",\n" +
                "  \"onlyFromKey\": \"false\"\n" +
                "}\n" +
                "\n";

    }

    @Override
    public String getText() {
        return "Send me information about your new card stock.\n" +
                "Please use this format:\n" +
                "\n" +
                "#add-CardStock\n"+
                "{\n" +
                "  \"name\": \"Some name\",\n" +
                "  \"description\": \"Some description\",\n" +
                "  \"keyType\": \"Key name\",\n" +
                "  \"valueType\": \"Value name\",\n" +
                "  \"maxPoint\": 10,\n" +
                "  \"testModeIsAvailable\": \"true\",\n" +
                "  \"onlyFromKey\": \"false\"\n" +
                "}\n" +
                "\n" +
                "if you need descriptions these, push the button `info`\n" +
                "\n"
                ;
    }

    @Override
    public String getName() {
        return "Card stock add menu";
    }
}
