package org.memorizing.model.menu;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

public class ModeMenu extends AMenu {

    @Override
    public EMenu getCurrentMenu() {
        return EMenu.MODE;
    }

    @Override
    public ReplyKeyboardMarkup getKeyboard() {
        return getKeyboardByButtons(new String[][]{
                {"start testing mode"},
                {"start self-check mode"},
                {"start memorizing mode"},
                {"info"},
                {"back"},
        });
    }

    @Override
    public String getInfoText() {
        return "\"Memorizing\" bot show you card key (\"memory\") and card value (\"память\").\n" +
                "You try to remember it and go to the next.\n" +
                "\n" +
                "\"Self-check\" - bot show you card key (\"memory\") and hidden translation. You have to remember translation, push the hidden text and check it by your-self\n" +
                "\n" +
                "\"Testing\" - bot show you only card key (\"memory\") and you have to remember translation. You send it (\"память\"). Bot check out It, update status and point for this card and  show you next card key.\n" +
                "This mode have 2 submods\n" +
                "Forward or FromKey (key -> value) bot show you key. You should send value\n" +
                "Backward or ToKey (value -> key) bot show you value. You should send key\n" +
                "\n" +
                "f you need Backward mod, specify it when you create card stock or turn of it mode by updating card stock\n" +
                "\n" +
                "Whichever mode you choose, the status and amount of point will change.\n" +
                "HARD status means that your last answer was incorrect\n"+
                "NORMAL status you didn't make mistakes \n"+
                "COMPLETED status means that you didn't make mistakes \n"+
                "If your answer will be correct your card increase a point and your card status will become `NORMAL`\n" +
                "If your answer won't be correct your card decrease a point  and your card status will become `HARD`\n" +
                "Status NORMAL can have only positive numbers. Status HARD can have only negative numbers\n" +
                "If you got a correct answer to card with HARD status (point = -5) \n" +
                "This card become NORMAL and will have point = 1. It works the other way, too.\n" +
                "Card have two statuses and points for a both submods";
    }

    @Override
    public String getText() {
        return "Please choose the mode:\n";
    }

    @Override
    public String getTitle() {
        return "*Mode menu*\n";

    }
}
