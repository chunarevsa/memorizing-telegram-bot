package org.memorizing.service;

import org.apache.log4j.Logger;
import org.memorizing.model.menu.*;
import org.memorizing.entity.*;
import org.memorizing.resource.StorageResource;
import org.memorizing.resource.cardApi.CardDto;
import org.memorizing.resource.cardApi.CardStockDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class MenuService { //TODO: Add interface
    private static final Logger log = Logger.getLogger(MenuService.class);
    private final StorageResource storageResource;
    private final UserStateService userStateService;

    public MenuService(StorageResource storageResource, UserStateService userStateService) {
        this.storageResource = storageResource;
        this.userStateService = userStateService;
    }

    public MenuFactory createLastMenu(Integer storageId, Integer userStateId, EMenu currentMenu) throws Exception {
        log.debug("createLastMenu. req:" + storageId + ", " + userStateId + ", " + currentMenu.name());
        UserState state = userStateService.getUserStateById(userStateId);
        return createMenu(storageId, userStateId, state.getLastMenu());
    }

    public MenuFactory createMenu(Integer storageId, Integer userStateId, EMenu menu) throws Exception {
        log.debug("createMenu. req:" + storageId + ", " + userStateId + ", " + menu.name());
        MenuFactory menuFactory = null;
        UserState state = userStateService.getUserStateById(userStateId);

        switch (menu) {
            case MAIN:
            case CARD_STOCKS:
                List<CardStockDto> cardStocks = storageResource.getCardStocksByStorageId(storageId);
                String[][] cardStockTypes = new String[0][];
                if (cardStocks != null && !cardStocks.isEmpty()) {
                    cardStockTypes = new String[1][cardStocks.size()];
                    for (int i = 0; i < cardStocks.size(); i++) {
                        CardStockDto cardStock = cardStocks.get(i);
                        cardStockTypes[0][i] = cardStock.getKeyType() + "/" + cardStock.getValueType();
                    }
                }
                menuFactory = new CardStocksMenu(cardStockTypes);
                break;
            case CARD_STOCK_ADD:
                menuFactory = new CardStockAddMenu();
                break;
            case CARD_STOCK:
                CardStockDto cardStock = storageResource.getCardStockById(state.getCardStockId());
                menuFactory = new CardStockMenu(cardStock);
                break;
            case CARD_STOCK_UPDATE:
                CardStockDto oldCardStock = storageResource.getCardStockById(state.getCardStockId());
                menuFactory = new CardStockUpdateMenu(oldCardStock);
                break;
            case CARDS:
                List<CardDto> cards = storageResource.getCardsByCardStockId(state.getCardStockId());
                String[][] cardKeys = new String[0][];
                if (cards != null && !cards.isEmpty()) {
                    cardKeys = new String[1][cards.size()];
                    for (int i = 0; i < cards.size(); i++) {
                        CardDto card = cards.get(i);
                        cardKeys[0][i] = card.getCardKey();
                    }
                }
                menuFactory = new CardsMenu(state.getCardStockId(), cardKeys);
                break;
            case CARD_ADD:
                menuFactory = new CardAddMenu();
                break;
            case CARD:
                CardDto card = storageResource.getCardById(state.getCardId());
                menuFactory = new CardMenu(state.getCardStockId(), card);
                break;
            case CARD_UPDATE:
                CardDto oldCard = storageResource.getCardById(state.getCardId());
                menuFactory = new CardUpdateMenu(oldCard);
                break;
            default:
                break;

        }

        userStateService.updateUserStateByMenu(userStateId, menuFactory);
        return menuFactory;
    }


    public MenuFactory createMenuByCallback(Integer storageId, Integer userStateId, EMenu currentMenu, String callback) throws Exception {
        log.debug("createMenuByCallback. req:" + storageId + ", " + userStateId + ", " + currentMenu.name());
        List<CardStockDto> cardStocks = storageResource.getCardStocksByStorageId(storageId);
        MenuFactory menuFactory = null;

        switch (currentMenu) {
            case MAIN:
            case CARD_STOCKS:
                Optional<CardStockDto> cardStock = Optional.empty();
                if (!cardStocks.isEmpty()) {
                    String keyType = callback.split("/")[0];
                    cardStock = cardStocks.stream()
                            .filter(cardStockDto -> Objects.equals(cardStockDto.getKeyType(), keyType))
                            .findFirst();
                }

                if (cardStock.isPresent()) {
                    menuFactory = new CardStockMenu(cardStock.get());
                    userStateService.updateUserStateByMenu(userStateId, menuFactory);
                }
                break;
            case CARDS:
                UserState userState = userStateService.getUserStateById(userStateId);

                List<CardDto> cards = storageResource.getCardsByCardStockId(userState.getCardStockId());
                Optional<CardDto> card = Optional.empty();
                if (!cards.isEmpty()) {
                    card = cards.stream()
                            .filter(it -> Objects.equals(it.getCardKey(), callback))
                            .findFirst();
                }

                if (card.isPresent()) {
                    menuFactory = new CardMenu(userState.getCardStockId(), card.get());
                    userStateService.updateUserStateByMenu(userStateId, menuFactory);
                }
                break;
            default:
                break;
        }
        return menuFactory;
    }
}
