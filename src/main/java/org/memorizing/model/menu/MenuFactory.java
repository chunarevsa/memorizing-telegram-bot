package org.memorizing.model.menu;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.List;

public interface MenuFactory {

    EMenu getLastMenu();

    EMenu getCurrentMenu();

    ReplyKeyboardMarkup getKeyboard();
    String getInfoText();
    String getText();

    InlineKeyboardMarkup getInlineKeyboard();

    InlineKeyboardMarkup createInlineKeyboard(List<String> strings);

    String getTitle();
}
