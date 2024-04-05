package com.memorizing.service;

import org.apache.log4j.Logger;
import com.memorizing.entity.UserState;
import com.memorizing.model.EMode;
import com.memorizing.model.menu.*;
import com.memorizing.resource.core.CardResource;
import com.memorizing.resource.core.CardStockResource;
import com.memorizing.model.storage.Card;
import com.memorizing.model.storage.CardStock;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MenuService { //TODO: Add interface
    private static final Logger log = Logger.getLogger(MenuService.class);
    private final CardStockResource cardStockResource;
    private final CardResource cardResource;
    private final UserStateService userStateService;

    public MenuService(CardStockResource cardStockResource, CardResource cardResource, UserStateService userStateService) {
        this.cardStockResource = cardStockResource;
        this.cardResource = cardResource;
        this.userStateService = userStateService;
    }

    public Menu createMenu(Integer storageId, UserState state, EMenu menuType) {
        log.debug("createMenu. req:" + storageId + ", " + state + ", " + menuType.name());
        Menu menu = null;

        switch (menuType) {
            case MAIN:
            case CARD_STOCKS:
                List<CardStock> cardStocks = cardStockResource.getCardStocksByStorageId(storageId);
                Map<Integer, String> map1 = new HashMap();
                if (cardStocks != null) {
                    cardStocks.forEach(cardStock -> map1.put(cardStock.getId(), cardStock.getCardStockName()));
                }
                menu = new CardStocksMenu(map1);
                break;
            case CARD_STOCK_ADD:
                menu = new CardStockAddMenu();
                break;
            case CARD_STOCK:
                CardStock cardStock = cardStockResource.getCardStockById(state.getCardStockId());
                List<Card> listOfCards = cardResource.getCardsByCardStockId(state.getCardStockId());
                menu = new CardStockMenu(cardStock, listOfCards);
                break;
            case MODE:
                menu = new ModeMenu(cardStockResource.getCardStockById(state.getCardStockId()));
                break;
            case FORWARD_TESTING:
            case BACKWARD_TESTING:
            case FORWARD_SELF_CHECK:
            case BACKWARD_SELF_CHECK:
            case FORWARD_MEMORIZING:
            case BACKWARD_MEMORIZING:
                EMode mode = EMode.getModeByMenu(menuType);

                List<Integer> ids = userStateService.getCardIdsByMode(state, mode.name());
                if (ids.isEmpty()) {
                    ids = getCardIdsForStudyingByRequest(state.getCardStockId(), mode);
                    // TODO add exception if ids.isEmpty
                }

                Optional<Card> mayBeCard = getCardForMenu(state, ids, mode);
                if (mayBeCard.isPresent()) {
                    menu = new StudyingMenuFactory().createStudyingMenu(mayBeCard.get(), menuType, ids);
                } else {
                    // take all cards when all ids were deleted
                    ids = getCardIdsForStudyingByRequest(state.getCardStockId(), mode);
                    mayBeCard = getCardForMenu(state, ids, mode);

                    if (mayBeCard.isPresent()) {
                        menu = new StudyingMenuFactory().createStudyingMenu(mayBeCard.get(), menuType, ids);
                    } else {
                        // TODO add exception if ids.isEmpty
                        menu = createMenu(storageId, state, menuType.getLastMenu());
                    }
                }

                break;
            case CARD_STOCK_UPDATE:
                menu = new CardStockUpdateMenu(cardStockResource.getCardStockById(state.getCardStockId()));
                break;
            case CARDS:
                List<Card> cards = cardResource.getCardsByCardStockId(state.getCardStockId());

                if (cards == null || cards.isEmpty()) {
                    menu = new CardStockMenu(cardStockResource.getCardStockById(state.getCardStockId()), cards);
                } else {
                    Map<Integer, String> map = new HashMap();
                    cards.forEach(card -> map.put(card.getId(), card.getCardKey()));
                    menu = new CardsMenu(state.getCardStockId(), map);
                }
                break;
            case CARD_ADD:
                menu = new CardAddMenu();
                break;
            case CARD:
                int maxPoint = cardStockResource.getCardStockById(state.getCardStockId()).getMaxPoint();
                menu = new CardMenu(state.getCardStockId(), maxPoint, cardResource.getCardById(state.getCardId()));
                break;
            case CARD_UPDATE:
                menu = new CardUpdateMenu(cardResource.getCardById(state.getCardId()));
                break;
            default:
                break;
        }

        userStateService.updateUserStateByMenu(state, menu);
        return menu;
    }

    private Optional<Card> getCardForMenu(UserState state, List<Integer> ids, EMode mode) {
        Card card = null;
        for (Integer id : ids) {
            // TODO: edit to optional
            Card mayBeCard = cardResource.getCardById(id);
            if (mayBeCard == null) {
                userStateService.deleteCardIdFromSessionAndGet(state, id);
                continue;
            }

            String status =  mode.isFromKeyMode() ? mayBeCard.getStatusFromKey() : mayBeCard.getStatusToKey();
            if (status.equals("COMPLETED")) {
                userStateService.deleteCardIdFromStudyingHistoryByMode(state, id, mode);
                continue;
            }
            card = mayBeCard;
            break;

        }

        return Optional.ofNullable(card);
    }

    private List<Integer> getCardIdsForStudyingByRequest(Integer cardStockId, EMode mode) {
        log.debug("getCardIdsForStudyingByRequest :" + cardStockId);
        List<Integer> ids = new ArrayList<>();
        List<Card> allCards = cardResource.getCardsByCardStockId(cardStockId);
        if (!allCards.isEmpty()) {
            ids = allCards.stream().filter(it -> (mode.isFromKeyMode() && !Objects.equals(it.getStatusFromKey(), "COMPLETED")) || (!mode.isFromKeyMode() && !Objects.equals(it.getStatusToKey(), "COMPLETED"))).map(Card::getId).collect(Collectors.toList());
            Collections.shuffle(ids);
        }
        return ids;
    }

    public Menu createMenuByCallback(Integer storageId, UserState userState, EMenu currentMenu, String callback) {
        log.debug("createMenuByCallback. req:" + storageId + ", " + userState + ", " + currentMenu.name());
        List<CardStock> cardStocks = cardStockResource.getCardStocksByStorageId(storageId);
        Menu menu = null;

        switch (currentMenu) {
            case MAIN:
            case CARD_STOCKS:
                Optional<CardStock> cardStock = Optional.empty();
                if (!cardStocks.isEmpty()) {
                    Integer cardStockId = Integer.valueOf(callback);
                    cardStock = cardStocks.stream().filter(cardStockDto -> Objects.equals(cardStockDto.getId(), cardStockId)).findFirst();
                }

                if (cardStock.isPresent()) {
                    List<Card> cards = cardResource.getCardsByCardStockId(cardStock.get().getId());
                    menu = new CardStockMenu(cardStock.get(), cards);
                    userStateService.updateUserStateByMenu(userState, menu);
                }
                break;
            case CARDS:
                List<Card> cards = cardResource.getCardsByCardStockId(userState.getCardStockId());
                Optional<Card> card = Optional.empty();
                if (!cards.isEmpty()) {
                    Integer cardId = Integer.valueOf(callback);
                    card = cards.stream().filter(it -> Objects.equals(it.getId(), cardId)).findFirst();
                }

                if (card.isPresent()) {
                    int maxPoint = cardStockResource.getCardStockById(userState.getCardStockId()).getMaxPoint();
                    menu = new CardMenu(userState.getCardStockId(), maxPoint, card.get());
                    userStateService.updateUserStateByMenu(userState, menu);
                }
                break;
            default:
                break;
        }
        return menu;
    }


}
