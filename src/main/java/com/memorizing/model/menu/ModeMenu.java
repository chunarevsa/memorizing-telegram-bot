package com.memorizing.model.menu;

import com.memorizing.model.storage.CardStock;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import static com.memorizing.model.command.EKeyboardCommand.*;

public class ModeMenu extends AMenu {

    CardStock cardStock;

    public ModeMenu(CardStock cardStock) {
        this.cardStock = cardStock;
    }

    @Override
    public EMenu getCurrentMenu() {
        return EMenu.MODE;
    }

    @Override
    public ReplyKeyboardMarkup getKeyboard() {
        String[] testLine = null;
        String[] selfCheckLine;
        String[] memorizingLine;
        String[] commonLine;

        if (cardStock.getTestModeIsAvailable()) {
            testLine = new String[]{START_FORWARD_TESTING.getButtonText()};
            if (!cardStock.getOnlyFromKey()) {
                testLine = new String[]{START_FORWARD_TESTING.getButtonText(), START_BACKWARD_TESTING.getButtonText()};
            }
        }

        if (!cardStock.getOnlyFromKey()) {
            selfCheckLine = new String[]{START_FORWARD_SELF_CHECK.getButtonText(), START_BACKWARD_SELF_CHECK.getButtonText()};
        } else selfCheckLine = new String[]{START_FORWARD_SELF_CHECK.getButtonText()};

        if (!cardStock.getOnlyFromKey()) {
            memorizingLine = new String[]{START_FORWARD_MEMORIZING.getButtonText(), START_BACKWARD_MEMORIZING.getButtonText()};
        } else memorizingLine = new String[]{START_FORWARD_MEMORIZING.getButtonText()};

        commonLine = new String[]{GET_INFO.getButtonText(), GO_BACK.getButtonText()};

        if (testLine == null) {
            return getKeyboardByButtons(new String[][]{selfCheckLine, memorizingLine, commonLine});
        } else return getKeyboardByButtons(new String[][]{testLine, selfCheckLine, memorizingLine, commonLine});

    }

    @Override
    public String getInfoText() {
        return "▪ *Memorizing* - bot shows you a card key (\"memory\") and a card value (\"память\").\n" +
                "You try to remember it and go to the next.\n" +
                "\n" +
                "▪ *Self-check* - bot shows you a card key (\"memory\") and a hidden translation.\n" +
                "You have to remember the translation, push the hidden text and check it by your-self\n" +
                "\n" +
                "▪ *Testing* - bot shows you only a card key (\"memory\").\n" +
                "You have to remember a translation and send it (\"память\").\n" +
                "Bot check out it, updates the status and points for this card and shows you the next card key.\n" +
                "This mode has 2 submodes\n" +
                "\n" +
                "▪ *Forward* or *FromKey* (key -> value) - bot shows you the key.\n" +
                "You should send the value\n" +
                "▪ *Backward* or *ToKey* (value -> key) - bot shows you the value.\n" +
                "You should send the key\n" +
                "\n" +
                "If you need the *Backward* submode, specify it when you create a card stock or turn on this submode by updating the card stock\n" +
                "\n" +
                "The Card has two statuses and points for a both submodes.\n" +
                "\n" +
                "▪ `HARD` status means your last answer was incorrect.\n" +
                "▪ `NORMAL` status means you didn't make mistakes.\n" +
                "▪ `COMPLETED` status means that you increase points to card stock’s maxPoints and you're good!\n" +
                "This card won’t  be to show you during a studying \n" +
                "If your answer is correct this card increase points and set the status `NORMAL`\n" +
                "If your answer isn't correct this card decrease points and set the status `HARD`\n" +
                "The status `NORMAL` can have only positive numbers whilst the status HARD can have only negative numbers\n" +
                "\n" +
                "If you got a correct answer to card with `HARD` status (points = -5)\n" +
                "this card become `NORMAL` and will have points = 1. It works the other way, too.\n";
    }

    @Override
    public String getText() {
        return "Please choose the mode to start:\n";
    }

    @Override
    public String getTitle() {
        return "*Mode menu*\n";

    }
}
