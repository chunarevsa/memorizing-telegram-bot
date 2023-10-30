package org.memorizing.model.menu;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

public interface MenuFactory {

    EMenu getLastMenu();

    EMenu getCurrentMenu();

    ReplyKeyboardMarkup getKeyboard();
    String getInfoText();
    String getText();

    InlineKeyboardMarkup getInlineKeyboard();

    InlineKeyboardMarkup createInlineKeyboard(String[][] cardStockTypes);

    String getTitle();
}
