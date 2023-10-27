package org.memorizing.service;

import org.memorizing.model.menu.CardMenu;
import org.memorizing.model.menu.CardsMenu;
import org.memorizing.entity.*;
import org.memorizing.model.menu.CardStockMenu;
import org.memorizing.model.menu.CardStocksMenu;
import org.memorizing.model.menu.MenuFactory;
import org.memorizing.repository.UserStateRepository;
import org.springframework.stereotype.Service;

@Service
public class UserStateService {
    private UserStateRepository userStates;

    public UserStateService(UserStateRepository userStates) {
        this.userStates = userStates;
    }

    public void updateUserStateByMenu(Integer stateId, MenuFactory menu) throws Exception {
        UserState userState = userStates.findById(stateId).orElseThrow(() -> new Exception("not found"));
        userState.setCurrentMenu(menu.getCurrentMenu());

        if (menu instanceof CardStocksMenu) {
            userState.setCardStockId(null);
            userState.setCardId(null);
        } else if (menu instanceof CardStockMenu) {
            CardStockMenu cardStockMenu = (CardStockMenu) menu;
            userState.setCardStockId(cardStockMenu.getCardStock().getId());
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

    public UserState getUserStateById(Integer userStateId) throws Exception {
        return userStates.findById(userStateId).orElseThrow(() -> new Exception("fdfd"));
    }
}
