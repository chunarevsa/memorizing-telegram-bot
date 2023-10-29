package org.memorizing.model.menu;

public enum EMenu {
    MAIN,
    CARD_STOCKS,
    CARD_STOCK_ADD,
    CARD_STOCK,
    CARDS,
    CARD,
    STUDYING,
    CARD_STOCK_UPDATE, CARD_UPDATE, CARD_ADD;

    public EMenu getLastMenu() {
        switch (this) {
            case CARD_STOCK:
            case CARD_STOCK_ADD:
                return EMenu.CARD_STOCKS;
            case CARDS:
            case CARD_STOCK_UPDATE:
                return EMenu.CARD_STOCK;
            case CARD:
            case CARD_ADD:
                return EMenu.CARDS;
            case CARD_UPDATE: return EMenu.CARD;

            default: return EMenu.MAIN;
        }
    }
}
