package org.memorizing.service;

import org.memorizing.entity.UserState;
import org.memorizing.model.menu.*;
import org.memorizing.repository.UserStateRepository;
import org.springframework.stereotype.Service;

@Service
public class UserStateService {
    private final UserStateRepository userStates;

    public UserStateService(UserStateRepository userStates) {
        this.userStates = userStates;
    }

    public void updateUserStateByMenu(UserState userState, MenuFactory menu) {
        userState.setCurrentMenu(menu.getCurrentMenu());

        if (menu instanceof CardStocksMenu) {
            userState.setCardStockId(null);
            userState.setCardId(null);

        } else if (menu instanceof CardStockMenu) {
            CardStockMenu cardStockMenu = (CardStockMenu) menu;
            userState.setCardStockId(cardStockMenu.getCardStock().getId());
            userState.setCardId(null);

        } else if (menu instanceof TestMenu) {
            TestMenu testMenu = (TestMenu) menu;
            userState.setCardId(null);

        } else if (menu instanceof CardsMenu) {
            CardsMenu cardsMenu = (CardsMenu) menu;
            userState.setCardStockId(cardsMenu.getCardStockId());
            userState.setCardId(null);

        } else if (menu instanceof CardMenu) {
            CardMenu cardMenu = (CardMenu) menu;
            userState.setCardStockId(cardMenu.getCardStockId());
            userState.setCardId(cardMenu.getCard().getId());
        }

        userStates.save(userState);
    }

}
