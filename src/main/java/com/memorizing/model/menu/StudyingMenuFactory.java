package com.memorizing.model.menu;

import com.memorizing.model.EMode;
import com.memorizing.model.storage.Card;

import java.util.List;

public class StudyingMenuFactory implements MenuFactory {

    @Override
    public AStudyingMenu createStudyingMenu(Card card, EMenu menuType, List<Integer> ids) {
        EMode mode = EMode.getModeByMenu(menuType);

        switch (mode) {
            case FORWARD_TESTING:
            case BACKWARD_TESTING:
                return new TestMenu(card, mode, ids);
            case FORWARD_SELF_CHECK:
            case BACKWARD_SELF_CHECK:
                return new SelfCheckMenu(card, mode, ids);
            case FORWARD_MEMORIZING:
            case BACKWARD_MEMORIZING:
                return new MemorizingMenu(card, mode, ids);
            default:
                // TODO: add throw exception
                return new TestMenu(card, mode, ids);
        }
    }
}
