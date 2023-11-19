package org.memorizing.service;

import org.apache.log4j.Logger;
import org.memorizing.entity.UserState;
import org.memorizing.model.EMode;
import org.memorizing.model.menu.*;
import org.memorizing.resource.StorageResource;
import org.memorizing.resource.cardApi.CardDto;
import org.memorizing.resource.cardApi.CardStockDto;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MenuService { //TODO: Add interface
    private static final Logger log = Logger.getLogger(MenuService.class);
    private final StorageResource storageResource;
    private final UserStateService userStateService;

    public MenuService(StorageResource storageResource, UserStateService userStateService) {
        this.storageResource = storageResource;
        this.userStateService = userStateService;
    }

    public MenuFactory createMenu(Integer storageId, UserState state, EMenu menu) {
        log.debug("createMenu. req:" + storageId + ", " + state + ", " + menu.name());
        MenuFactory menuFactory = null;
        EMode mode;
        List<Integer> ids;
        Optional<Integer> firstId;

        switch (menu) {
            case MAIN:
            case CARD_STOCKS:
                List<CardStockDto> cardStocks = storageResource.getCardStocksByStorageId(storageId);
                List<String> names = new ArrayList<>();
                if (cardStocks != null) {
                    names = cardStocks.stream().map(CardStockDto::getCardStockName).collect(Collectors.toList());
                }
                menuFactory = new CardStocksMenu(names);
                break;
            case CARD_STOCK_ADD:
                menuFactory = new CardStockAddMenu();
                break;
            case CARD_STOCK:
                menuFactory = new CardStockMenu(storageResource.getCardStockById(state.getCardStockId()));
                break;
            case MODE:
                menuFactory = new ModeMenu(storageResource.getCardStockById(state.getCardStockId()));
                break;
            case FORWARD_TESTING:
            case BACKWARD_TESTING:
                mode = EMode.getModeByMenu(menu);

                ids = userStateService.getCardIdsByMode(state, mode.name());
                if (ids.isEmpty()) {
                    ids = getCardIdsForStudyingByRequest(state.getCardStockId());
                }

                firstId = ids.stream().findFirst();
                if (firstId.isPresent()) {
                    CardDto card = storageResource.getCardById(firstId.get());
                    menuFactory = new TestMenu(card, mode, ids);
                }
                break;
            case FORWARD_SELF_CHECK:
            case BACKWARD_SELF_CHECK:
                mode = EMode.getModeByMenu(menu);

                ids = userStateService.getCardIdsByMode(state, mode.name());
                if (ids.isEmpty()) {
                    ids = getCardIdsForStudyingByRequest(state.getCardStockId());
                }

                firstId = ids.stream().findFirst();
                if (firstId.isPresent()) {
                    CardDto card = storageResource.getCardById(firstId.get());
                    menuFactory = new SelfCheckMenu(card, mode, ids);
                }
                break;
            case FORWARD_MEMORIZING:
            case BACKWARD_MEMORIZING:
                mode = EMode.getModeByMenu(menu);

                ids = userStateService.getCardIdsByMode(state, mode.name());
                if (ids.isEmpty()) {
                    ids = getCardIdsForStudyingByRequest(state.getCardStockId());
                }

                firstId = ids.stream().findFirst();
                if (firstId.isPresent()) {
                    CardDto card = storageResource.getCardById(firstId.get());
                    menuFactory = new MemorizingMenu(card, mode, ids);
                }
                break;

            case CARD_STOCK_UPDATE:
                menuFactory = new CardStockUpdateMenu(storageResource.getCardStockById(state.getCardStockId()));
                break;
            case CARDS:
                List<CardDto> cards = storageResource.getCardsByCardStockId(state.getCardStockId());
                List<String> keys = cards.stream().map(CardDto::getCardKey).collect(Collectors.toList());
                menuFactory = new CardsMenu(state.getCardStockId(), keys);
                break;
            case CARD_ADD:
                menuFactory = new CardAddMenu();
                break;
            case CARD:
                int maxPoint = storageResource.getCardStockById(state.getCardStockId()).getMaxPoint();
                menuFactory = new CardMenu(state.getCardStockId(), maxPoint, storageResource.getCardById(state.getCardId()));
                break;
            case CARD_UPDATE:
                menuFactory = new CardUpdateMenu(storageResource.getCardById(state.getCardId()));
                break;
            default:
                break;
        }

        userStateService.updateUserStateByMenu(state, menuFactory);
        return menuFactory;
    }

    private List<Integer> getCardIdsForStudyingByRequest(Integer cardStockId) {
        List<Integer> ids = new ArrayList<>();
        List<CardDto> allCards = storageResource.getCardsByCardStockId(cardStockId);
        if (!allCards.isEmpty()) {
            ids = allCards.stream().map(CardDto::getId).collect(Collectors.toList());
            Collections.shuffle(ids);
        }
        return ids;
    }

    public MenuFactory createMenuByCallback(Integer storageId, UserState userState, EMenu currentMenu, String callback) {
        log.debug("createMenuByCallback. req:" + storageId + ", " + userState + ", " + currentMenu.name());
        List<CardStockDto> cardStocks = storageResource.getCardStocksByStorageId(storageId);
        MenuFactory menuFactory = null;

        switch (currentMenu) {
            case MAIN:
            case CARD_STOCKS:
                Optional<CardStockDto> cardStock = Optional.empty();
                if (!cardStocks.isEmpty()) {
                    cardStock = cardStocks.stream()
                            .filter(cardStockDto -> Objects.equals(cardStockDto.getCardStockName(), callback))
                            .findFirst();
                }

                if (cardStock.isPresent()) {
                    menuFactory = new CardStockMenu(cardStock.get());
                    userStateService.updateUserStateByMenu(userState, menuFactory);
                }
                break;
            case CARDS:
                List<CardDto> cards = storageResource.getCardsByCardStockId(userState.getCardStockId());
                Optional<CardDto> card = Optional.empty();
                if (!cards.isEmpty()) {
                    card = cards.stream()
                            .filter(it -> Objects.equals(it.getCardKey(), callback))
                            .findFirst();
                }

                if (card.isPresent()) {
                    int maxPoint = storageResource.getCardStockById(userState.getCardStockId()).getMaxPoint();
                    menuFactory = new CardMenu(userState.getCardStockId(), maxPoint, card.get());
                    userStateService.updateUserStateByMenu(userState, menuFactory);
                }
                break;
            default:
                break;
        }
        return menuFactory;
    }


}
