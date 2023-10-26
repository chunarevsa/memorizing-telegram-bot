package org.memorizing.controller;

import org.memorizing.entity.CardStockMenu;
import org.memorizing.entity.MenuFactory;
import org.memorizing.entity.UserState;
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
        userState.setLastMenu(menu.getLastMenu());

        if (menu instanceof CardStockMenu) {
            CardStockMenu cardStockMenu = (CardStockMenu) menu;
            userState.setCardStockId(cardStockMenu.getCardStock().getId());
        }
//        else if (menuR instanceof CardsMenu) {
//            CardsMenu cardsMenu = (CardsMenu) menuR;
//            userState.setCardStockId(cardsMenu.getCardStockId());
//        } else if (menuR instanceof CardMenu) {
//            CardMenu cardMenu = (CardMenu) menuR;
//            userState.setCardStockId(cardMenu.getCardStockId());
//            userState.setCardId(cardMenu.getCardId());
//        }

        userStates.save(userState);
    }

    public UserState getUserStateById(Integer userStateId) throws Exception {
        return userStates.findById(userStateId).orElseThrow(() -> new Exception("fdfd"));
    }
}
