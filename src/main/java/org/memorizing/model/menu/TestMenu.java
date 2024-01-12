package org.memorizing.model.menu;

import org.memorizing.model.EMode;
import org.memorizing.resource.cardApi.CardDto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.List;

import static org.memorizing.model.command.EKeyboardCommand.*;

public class TestMenu extends AStudyingMenu {

    public TestMenu(CardDto card, EMode mode, List<Integer> ids) {
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
