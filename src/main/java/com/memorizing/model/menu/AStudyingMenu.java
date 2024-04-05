package com.memorizing.model.menu;

import com.memorizing.model.EMode;
import com.memorizing.model.storage.Card;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.List;

import static com.memorizing.model.command.EKeyboardCommand.*;

public abstract class AStudyingMenu extends AMenu {
    private final Card card;
    private final EMode mode;
    private final List<Integer> ids;

    protected AStudyingMenu(Card card, EMode mode, List<Integer> ids) {
        this.card = card;
        this.mode = mode;
        this.ids = ids;
    }

    public abstract String[] getNextButton();

    public Card getCard() {
        return card;
    }

    public EMode getMode() {
        return mode;
    }

    public List<Integer> getIds() {
        return ids;
    }

    @Override
    public ReplyKeyboardMarkup getKeyboard() {
        return getKeyboardByButtons(new String[][]{
                getNextButton(),
                {GO_TO_CARD.getButtonText()},
                {GET_INFO.getButtonText(), GO_BACK.getButtonText()}
        });
    }


}
