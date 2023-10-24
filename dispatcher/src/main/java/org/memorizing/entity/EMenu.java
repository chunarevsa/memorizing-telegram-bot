package org.memorizing.entity;

public enum EMenu {
    MAIN("Main menu"),
    CARD_STOCKS("Card stocks"),
    CARD_STOCK("Card stock"),
    CARDS("Cards"),
    CARD("Card"),
    STUDYING("Studying");

    private final String text;

    EMenu(String text) {
        this.text = text;
    }

    public EMenu getNext() {
        EMenu[] values = EMenu.values();
        EMenu next = null;
        for (int i = 0; i < values.length - 1; i++) {
            if (this == values[i]) next = values[i+1];
        }
        return next;
    }

    public EMenu getPrevious() {
        EMenu[] values = EMenu.values();
        EMenu next = values[0];
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
