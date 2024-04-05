package com.memorizing.model.menu;

import java.util.ArrayList;
import java.util.List;

public enum EMenu {
    MAIN,
    CARD_STOCKS, CARD_STOCK, CARD_STOCK_ADD, CARD_STOCK_UPDATE,
    MODE,
    FORWARD_TESTING,
    BACKWARD_TESTING,
    FORWARD_SELF_CHECK,
    BACKWARD_SELF_CHECK,
    FORWARD_MEMORIZING,
    BACKWARD_MEMORIZING,

    CARDS, CARD, CARD_ADD, CARD_UPDATE;

    public EMenu getLastMenu() {
        switch (this) {
            case CARD_STOCK:
            case CARD_STOCK_ADD:
                return EMenu.CARD_STOCKS;
            case CARDS:
            case CARD_STOCK_UPDATE:
            case MODE:
            case FORWARD_TESTING:
            case FORWARD_SELF_CHECK:
            case FORWARD_MEMORIZING:
            case BACKWARD_TESTING:
            case BACKWARD_SELF_CHECK:
            case BACKWARD_MEMORIZING:
                return EMenu.CARD_STOCK;
            case CARD:
            case CARD_ADD:
                return EMenu.CARDS;
            case CARD_UPDATE:
                return EMenu.CARD;

            default:
                return EMenu.MAIN;
        }
    }

    public static List<EMenu> getOnlyStudyingMenu() {
        ArrayList<EMenu> list = new ArrayList<>();
        list.add(FORWARD_TESTING);
        list.add(FORWARD_SELF_CHECK);
        list.add(FORWARD_MEMORIZING);
        list.add(BACKWARD_TESTING);
        list.add(BACKWARD_SELF_CHECK);
        list.add(BACKWARD_MEMORIZING);
        return list;
    }
}
