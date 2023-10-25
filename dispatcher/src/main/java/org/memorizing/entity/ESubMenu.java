package org.memorizing.entity;

public enum ESubMenu {
    ADDING_CARD_STOCK_MENU("add"),
    CARD_STOCKS("Card stocks"),
    CARD_STOCK("Card stock"),
    CARDS("Cards"),
    CARD("Card"),
    STUDYING("Studying");

    private final String text;

    ESubMenu(String text) {
        this.text = text;
    }

    public ESubMenu getNext() {
        ESubMenu[] values = ESubMenu.values();
        ESubMenu next = null;
        for (int i = 0; i < values.length - 1; i++) {
            if (this == values[i]) next = values[i+1];
        }
        return next;
    }

    public ESubMenu getPrevious() {
        ESubMenu[] values = ESubMenu.values();
        ESubMenu next = values[0];
        for (int i = 0; i < values.length - 1; i++) {
            if (this == values[i] && i != 0) next = values[i-1];
        }
        return next;
    }

    @Override
    public String toString() {
        return "EMenu{" +
                ", text='" + text + '\'' +
                '}';
    }
}
