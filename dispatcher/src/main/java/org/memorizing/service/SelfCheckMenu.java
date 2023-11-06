package org.memorizing.service;

import org.memorizing.model.menu.AMenu;
import org.memorizing.model.menu.EMenu;
import org.memorizing.model.menu.EMode;
import org.memorizing.resource.cardApi.CardDto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.List;

public class SelfCheckMenu extends AMenu {
    private final CardDto card;
    private final EMode mode;
    private final List<Integer> ids;

    public SelfCheckMenu(CardDto card, EMode mode, List<Integer> ids) {
        this.card = card;
        this.mode = mode;
        this.ids = ids;
    }

    public CardDto getCard() {
        return card;
    }

    public EMode getMode() {
        return mode;
    }

    public List<Integer> getIds() {
        return ids;
    }

    @Override
    public EMenu getCurrentMenu() {
        return null;
    }

    @Override
    public ReplyKeyboardMarkup getKeyboard() {
        return null;
    }

    @Override
    public String getInfoText() {
        return null;
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public String getTitle() {
        return null;
    }
}
