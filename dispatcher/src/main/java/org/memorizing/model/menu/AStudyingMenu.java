package org.memorizing.model.menu;

import org.memorizing.model.EMode;
import org.memorizing.resource.cardApi.CardDto;

import java.util.List;

public abstract class AStudyingMenu extends AMenu {
    private final CardDto card;
    private final EMode mode;
    private final List<Integer> ids;

    protected AStudyingMenu(CardDto card, EMode mode, List<Integer> ids) {
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
}
