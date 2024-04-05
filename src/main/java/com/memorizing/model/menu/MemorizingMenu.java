package com.memorizing.model.menu;

import com.memorizing.model.EMode;
import com.memorizing.model.storage.Card;

import java.util.List;

import static com.memorizing.model.command.EKeyboardCommand.*;

public class MemorizingMenu extends AStudyingMenu {

    public MemorizingMenu(Card card, EMode mode, List<Integer> ids) {
        super(card, mode, ids);
    }

    @Override
    public EMenu getCurrentMenu() {
        return getMode().isFromKeyMode() ? EMenu.FORWARD_MEMORIZING : EMenu.BACKWARD_MEMORIZING;
    }

    @Override
    public String[] getNextButton() { return new String[]{NEXT.getButtonText()}; }

    @Override
    public String getInfoText() {
        return "Amount of cards:" + getIds().size();
    }

    @Override
    public String getText() {
        String key = getCard().getCardKey();
        String value = getCard().getCardValue();

        return getMode().isFromKeyMode()
                ? (key + "\n" +
                value)
                : (value + "\n" +
                key);
    }

    @Override
    public String getTitle() {
        return "*Memorizing menu*\n" +
                "Amount of cards:" + getIds().size();
    }
}
