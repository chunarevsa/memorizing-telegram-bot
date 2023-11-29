package org.memorizing.model.menu;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.List;
import java.util.Map;

public interface MenuFactory {

    EMenu getLastMenu();

    EMenu getCurrentMenu();

    ReplyKeyboardMarkup getKeyboard();
    String getInfoText();
    String getText();

    InlineKeyboardMarkup getInlineKeyboard();

    InlineKeyboardMarkup createInlineKeyboard(Map<Integer, String> map);

    String getTitle();
}
