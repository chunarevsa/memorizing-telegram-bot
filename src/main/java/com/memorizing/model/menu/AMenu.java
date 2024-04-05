package com.memorizing.model.menu;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AMenu implements Menu {

    @Override
    public EMenu getLastMenu() {
        return this.getCurrentMenu().getLastMenu();
    }

    public InlineKeyboardMarkup createInlineKeyboard(Map<Integer, String> map) {
        if (map == null || map.isEmpty()) return null;

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        final List<InlineKeyboardButton>[] keyboardButtonsRow = new List[]{new ArrayList<>()};

        map.forEach((key, value) -> {

            if (!isEnoughSpaceInKeyboardButtonRow(keyboardButtonsRow[0], value)) {
                // create next line
                rowList.add(keyboardButtonsRow[0]);
                keyboardButtonsRow[0] = new ArrayList<>();
            }

            InlineKeyboardButton inlineKeyboardButton = InlineKeyboardButton.builder()
                    .text(value.length() > 40 ? value.substring(0, 40) + "..." : value)
                    .callbackData(key.toString())
                    .build();
            keyboardButtonsRow[0].add(inlineKeyboardButton);
        });

        rowList.add(keyboardButtonsRow[0]);

        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    private boolean isEnoughSpaceInKeyboardButtonRow(List<InlineKeyboardButton> keyboardButtonsRow, String name) {
        if (keyboardButtonsRow.isEmpty()) return true;
        if (keyboardButtonsRow.size() >= 5) return false;

        List<String> names = keyboardButtonsRow.stream().map(InlineKeyboardButton::getText).collect(Collectors.toList());
        if (name.length() > 40 ) name = name.substring(0, 40) + "...";
        names.add(name);

        int maxOneRowSize;
        if (names.size() == 1) {
            maxOneRowSize = 40;
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
