package org.memorizing.service;

import org.memorizing.model.menu.AMenu;
import org.memorizing.model.menu.EMenu;
import org.memorizing.model.menu.EMode;
import org.memorizing.resource.cardApi.CardDto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.List;

import static org.memorizing.model.menu.EKeyboardCommand.*;

public class TestMenu extends AMenu {
    private final CardDto card;
    private final EMode mode;
    private final List<Integer> ids;

    public TestMenu(CardDto card, EMode mode, List<Integer> ids) {
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
        return EMenu.FORWARD_TESTING;
    }

    @Override
    public ReplyKeyboardMarkup getKeyboard() {
        return getKeyboardByButtons(new String[][]{
                {SKIP_WORD.getButtonText()},
                {GET_INFO.getButtonText(), GO_BACK.getButtonText()}
        });
    }

    @Override
    public String getInfoText() {
        return "Some info text from Test menu";
    }

    @Override
    public String getText() {
        if (mode.isFromKeyMode()) {
            return card.getCardKey();
        } else return card.getCardValue();
    }

    @Override
    public String getTitle() {
        return "*Test menu*\n";
    }
}
