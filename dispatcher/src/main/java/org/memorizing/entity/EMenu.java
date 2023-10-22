package org.memorizing.entity;

public enum EMenu {
    MAIN(1, "Main menu"),
    CARD_STOCK(2, "Card stock"),
    CARD(3, "Card"),
    STUDYING(4, "Studying");

    private final int id;
    private final String text;

    EMenu(int id, String text) {
        this.id = id;
        this.text = text;
    }

    @Override
    public String toString() {
        return "EMenu{" +
                "id=" + id +
                ", text='" + text + '\'' +
                '}';
    }
}
