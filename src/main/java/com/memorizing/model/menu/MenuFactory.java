package com.memorizing.model.menu;

import com.memorizing.model.storage.Card;

import java.util.List;

public interface MenuFactory {

    AStudyingMenu createStudyingMenu(Card card, EMenu menuType, List<Integer> ids) throws Exception;
}
