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
                Map<Integer, String> map1 = new HashMap();
                if (cardStocks != null) {
                    cardStocks.forEach(cardStock -> map1.put(cardStock.getId(), cardStock.getCardStockName()));
                }
                menuFactory = new CardStocksMenu(map1);
                break;
            case CARD_STOCK_ADD:
                menuFactory = new CardStockAddMenu();
                break;
            case CARD_STOCK:
                CardStockDto cardStock = storageResource.getCardStockById(state.getCardStockId());
                List<CardDto> listOfCards = storageResource.getCardsByCardStockId(state.getCardStockId());
                menuFactory = new CardStockMenu(cardStock, listOfCards);
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

                CardDto firstCard;
                for (Integer id: ids) {
                    firstCard = storageResource.getCardById(id);
                    if (firstCard == null) {
                        state = userStateService.deleteCardIdFromSessionAndGet(state);
                    } else {
                        menuFactory = new TestMenu(firstCard, mode, ids);
                        break;
                    }
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

                if (cards == null || cards.isEmpty()) {
                    menuFactory = new CardStockMenu(storageResource.getCardStockById(state.getCardStockId()), cards);
                } else {
                    Map<Integer, String> map = new HashMap();
                    cards.forEach(card -> map.put(card.getId(), card.getCardKey()));
                    menuFactory = new CardsMenu(state.getCardStockId(), map);
                }
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
                    Integer cardStockId = Integer.valueOf(callback);
                    cardStock = cardStocks.stream()
                            .filter(cardStockDto -> Objects.equals(cardStockDto.getId(), cardStockId))
                            .findFirst();
                }

                if (cardStock.isPresent()) {
                    List<CardDto> cards = storageResource.getCardsByCardStockId(cardStock.get().getId());
                    menuFactory = new CardStockMenu(cardStock.get(), cards);
                    userStateService.updateUserStateByMenu(userState, menuFactory);
                }
                break;
            case CARDS:
                List<CardDto> cards = storageResource.getCardsByCardStockId(userState.getCardStockId());
                Optional<CardDto> card = Optional.empty();
                if (!cards.isEmpty()) {
                    Integer cardId = Integer.valueOf(callback);
                    card = cards.stream()
                            .filter(it -> Objects.equals(it.getId(), cardId))
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
