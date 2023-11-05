package org.memorizing.model.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum EMenu {
    MAIN,
    CARD_STOCKS,
    CARD_STOCK_ADD("add card stock"),
    CARD_STOCK,
    MODE("start studying"),
    FORWARD_TESTING("forward  testing mode"),
    BACKWARD_TESTING("backward testing mode"),
    FORWARD_SELF_CHECK("forward  self-check mode"),
    BACKWARD_SELF_CHECK("backward self-check mode"),
    FORWARD_MEMORIZING("forward  memorizing mode"),
    BACKWARD_MEMORIZING("backward memorizing mode"),
    CARD_STOCK_UPDATE("update card stock"),
    CARDS("show cards"),
    CARD_ADD("add card"),
    CARD,
    CARD_UPDATE("update card"),
    ;
    private String callButton;

    EMenu(String callButton) {
        this.callButton = callButton;
    }

    EMenu() {
    }

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

    public static EMenu getMenuByCallButton(String callButton) {
        return Arrays.stream(EMenu.values())
                .filter(it -> it.callButton != null && it.callButton.equals(callButton))
                .findFirst().orElse(null);

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
