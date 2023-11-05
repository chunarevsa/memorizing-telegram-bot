package org.memorizing.service;

import org.memorizing.model.menu.AMenu;
import org.memorizing.model.menu.EMenu;
import org.memorizing.resource.cardApi.CardDto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.List;

public class TestMenu extends AMenu {
    private final CardDto card;
    private final Boolean fromKey;
    private final String mode;
    private final List<Integer> ids;

    public TestMenu(CardDto card, Boolean fromKey, String mode, List<Integer> ids) {
        this.card = card;
        this.fromKey = fromKey;
        this.mode = mode;
        this.ids = ids;
    }

    public String getMode() {
        return mode;
    }

    public List<Integer> getIds() {
        return ids;
    }

    @Override
    public EMenu getCurrentMenu() {
        return EMenu.FORWARD_TESTING;
    }

    @Override
    public ReplyKeyboardMarkup getKeyboard() {
        return getKeyboardByButtons(new String[][]{
                {"skip"},
                {"info", "back"}
        });
    }

    @Override
    public String getInfoText() {
        return "Some info text from Test menu";
    }

    @Override
    public String getText() {
        if (fromKey) {
            return card.getCardKey();
        } else return card.getCardValue();
    }

    @Override
    public String getTitle() {
        return "*Test menu*\n";
    }
}
