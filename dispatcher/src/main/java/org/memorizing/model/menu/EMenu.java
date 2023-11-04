package org.memorizing.model.menu;

import java.util.Arrays;

public enum EMenu {
    MAIN,
    CARD_STOCKS,
    CARD_STOCK_ADD("add card stock"),
    CARD_STOCK,
    MODE("start studying"),
    TESTING("start testing mode"),
    SELF_CHECK("start self-check mode"),
    MEMORIZING("start memorizing mode"),
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
            case TESTING:
            case SELF_CHECK:
            case MEMORIZING:
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
}
