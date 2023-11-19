package org.memorizing.model.menu;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AMenu implements MenuFactory {

    @Override
    public EMenu getLastMenu() {
        return this.getCurrentMenu().getLastMenu();
    }

    public InlineKeyboardMarkup createInlineKeyboard(List<String> strings) {
        if (strings == null || strings.isEmpty()) return null;

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();

        for (String name : strings) {

            if (!isEnoughSpaceInKeyboardButtonRow(keyboardButtonsRow, name)) {
                // create next line
                rowList.add(keyboardButtonsRow);
                keyboardButtonsRow = new ArrayList<>();
            }

            InlineKeyboardButton inlineKeyboardButton = InlineKeyboardButton.builder()
                    .text(name)
                    .callbackData(name)
                    .build();
            keyboardButtonsRow.add(inlineKeyboardButton);

        }
        rowList.add(keyboardButtonsRow);

        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    private boolean isEnoughSpaceInKeyboardButtonRow(List<InlineKeyboardButton> keyboardButtonsRow, String name) {
        if (keyboardButtonsRow.isEmpty()) return true;
        if (keyboardButtonsRow.size() >= 5) return false;

        List<String> names = keyboardButtonsRow.stream().map(InlineKeyboardButton::getText).collect(Collectors.toList());
        names.add(name);

        int maxOneRowSize;
        if (names.size() == 1) {
            maxOneRowSize = 30;
        } else if (names.size() == 2) {
            maxOneRowSize = 26;
        } else if (names.size() == 3) {
            maxOneRowSize = 17;
        } else if (names.size() == 4) {
            maxOneRowSize = 12;
        } else {
            maxOneRowSize = 7;
        }

        int maxLengthNewNames = names.stream().mapToInt(String::length).max().getAsInt();
        return maxLengthNewNames <= maxOneRowSize;
    }

    protected ReplyKeyboardMarkup getKeyboardByButtons(String[][] strings) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rowList = new ArrayList<>();

        for (String[] ss : strings) {
            KeyboardRow row = new KeyboardRow();
            row.addAll(Arrays.asList(ss));
            rowList.add(row);
        }

        keyboardMarkup.setKeyboard(rowList);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        return keyboardMarkup;
    }

    @Override
    public InlineKeyboardMarkup getInlineKeyboard() {
        return null;
    }
}
