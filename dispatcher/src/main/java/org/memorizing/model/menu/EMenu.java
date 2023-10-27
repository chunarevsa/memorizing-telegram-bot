package org.memorizing.model.menu;

import java.lang.reflect.GenericDeclaration;

public enum EMenu {
    MAIN(0),
    CARD_STOCKS(1),
    CARD_STOCK(2),
    CARDS(3),
    CARD(4),
    STUDYING(5);

    final int id;

    EMenu(int id) {
        this.id = id;
    }

    public MenuFactory getMenuFactoryByEMenu() {
        GenericDeclaration h;
        switch (this) {
            case MAIN:
            case CARD_STOCKS:
                h = CardStocksMenu.class;
                break;
            case CARD_STOCK:
                h = CardStockMenu.class;
                break;
            default:
                h = CardStocksMenu.class;
        }
        return (MenuFactory) h;
    }

}
