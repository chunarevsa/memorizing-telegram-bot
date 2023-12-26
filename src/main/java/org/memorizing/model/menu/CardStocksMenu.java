package org.memorizing.model.menu;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.List;
import java.util.Map;

import static org.memorizing.model.command.EKeyboardCommand.ADD_CARD_STOCK;
import static org.memorizing.model.command.EKeyboardCommand.GET_INFO;

public class CardStocksMenu extends AMenu {

    InlineKeyboardMarkup inlineKeyboard;

    public CardStocksMenu(Map<Integer, String> map) {
        this.inlineKeyboard = createInlineKeyboard(map);
    }

    @Override
    public InlineKeyboardMarkup getInlineKeyboard() {
        return inlineKeyboard;
    }

    @Override
    public String getTitle() {
        return "*Card stocks menu*";
    }

    @Override
    public EMenu getCurrentMenu() {
        return EMenu.CARD_STOCKS;
    }

    @Override
    public ReplyKeyboardMarkup getKeyboard() {
        return getKeyboardByButtons(new String[][]{
                {ADD_CARD_STOCK.getButtonText()},
                {GET_INFO.getButtonText()}
        });
    }

    @Override
    public String getInfoText() {
        return "The Card stock is a card storage with a one topic.\n" +
                "For example:\n" +
                "The card stock \"English words\"  will have\n" +
                "`keyType` = \"English word\"\n" +
                "`valueType` = \"Russian translations\"\n" +
                "\n" +
                "You can add a Card to this Card stock\n" +
                "`key` = \"memory\"\n" +
                "`value` = \"память\"\n" +
                "\n" +
                "The card stock \"Answers for interview questions\"  will have\n" +
                "`keyType` = \"Question\"\n" +
                "`valueType` = \"Answer\"\n" +
                "\n" +
                "You can add a Card to this Card stock\n" +
                "`key` = \"Why do you leave your last job?\"\n" +
                "`value` = \"Your some interesting answer \"\n" +
                "etc.\n" +
                "\n" +
                "If you want to manage a card stock, Just open it.";
    }

    @Override
    public String getText() {
        if (!inlineKeyboard.getKeyboard().isEmpty()) {
            int size = inlineKeyboard.getKeyboard().stream().mapToInt(List::size).sum();
            return String.format("You have %s card stocks", size);
        } else return "You don't have a card stocks. " +
                "Push the button `add card stock`";
    }
}
