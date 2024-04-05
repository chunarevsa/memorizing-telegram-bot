package com.memorizing.model.menu;

import com.memorizing.model.EMode;
import com.memorizing.model.storage.Card;

import java.util.List;

import static com.memorizing.model.command.EKeyboardCommand.*;

public class TestMenu extends AStudyingMenu {

    public TestMenu(Card card, EMode mode, List<Integer> ids) {
        super(card, mode, ids);
    }

    @Override
    public EMenu getCurrentMenu() {
        return getMode().isFromKeyMode() ? EMenu.FORWARD_TESTING : EMenu.BACKWARD_TESTING;
    }

    @Override
    public String[] getNextButton() { return new String[]{SKIP.getButtonText()}; }

    @Override
    public String getInfoText() {
        return "Amount of cards:" + getIds().size();
    }

    @Override
    public String getText() {
        return getMode().isFromKeyMode() ? getCard().getCardKey() : getCard().getCardValue();
    }

    @Override
    public String getTitle() {
        return "*Test menu*\n" +
                "Amount of cards:" + getIds().size();
    }
}
