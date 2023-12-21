package org.memorizing.model.menu;

import org.memorizing.resource.cardApi.CardDto;

import java.util.List;

public interface MenuFactory {

    AStudyingMenu createStudyingMenu(CardDto card, EMenu menuType, List<Integer> ids) throws Exception;
}
