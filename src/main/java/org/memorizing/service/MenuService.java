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

    public Menu createMenu(Integer storageId, UserState state, EMenu menuType) {
        log.debug("createMenu. req:" + storageId + ", " + state + ", " + menuType.name());
        Menu menu = null;

        switch (menuType) {
            case MAIN:
            case CARD_STOCKS:
                List<CardStockDto> cardStocks = storageResource.getCardStocksByStorageId(storageId);
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
                CardStockDto cardStock = storageResource.getCardStockById(state.getCardStockId());
                List<CardDto> listOfCards = storageResource.getCardsByCardStockId(state.getCardStockId());
                menu = new CardStockMenu(cardStock, listOfCards);
                break;
            case MODE:
                menu = new ModeMenu(storageResource.getCardStockById(state.getCardStockId()));
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

                Optional<CardDto> mayBeCard = getCardForMenu(state, ids, mode.isFromKeyMode());
                if (mayBeCard.isPresent()) {
                    menu = new StudyingMenuFactory().createStudyingMenu(mayBeCard.get(), menuType, ids);
                } else createMenu(storageId, state, menuType.getLastMenu());

                break;
            case CARD_STOCK_UPDATE:
                menu = new CardStockUpdateMenu(storageResource.getCardStockById(state.getCardStockId()));
                break;
            case CARDS:
                List<CardDto> cards = storageResource.getCardsByCardStockId(state.getCardStockId());

                if (cards == null || cards.isEmpty()) {
                    menu = new CardStockMenu(storageResource.getCardStockById(state.getCardStockId()), cards);
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
                int maxPoint = storageResource.getCardStockById(state.getCardStockId()).getMaxPoint();
                menu = new CardMenu(state.getCardStockId(), maxPoint, storageResource.getCardById(state.getCardId()));
                break;
            case CARD_UPDATE:
                menu = new CardUpdateMenu(storageResource.getCardById(state.getCardId()));
                break;
            default:
                break;
        }

        userStateService.updateUserStateByMenu(state, menu);
        return menu;
    }

    private Optional<CardDto> getCardForMenu(UserState state, List<Integer> ids, boolean fromKey) {
        CardDto card = null;
        for (Integer id : ids) {
            // TODO: edit to optional
            CardDto mayBeCard = storageResource.getCardById(id);

            if (mayBeCard != null &&
                    (fromKey && !Objects.equals(mayBeCard.getStatusFromKey(), "COMPLETED")) ||
                    (!fromKey && !Objects.equals(mayBeCard.getStatusToKey(), "COMPLETED"))) {

                card = mayBeCard;
                break;
            } else state = userStateService.deleteCardIdFromSessionAndGet(state);
        }

        return Optional.ofNullable(card);
    }

    private List<Integer> getCardIdsForStudyingByRequest(Integer cardStockId, EMode mode) {
        log.debug("getCardIdsForStudyingByRequest :" + cardStockId);
        List<Integer> ids = new ArrayList<>();
        List<CardDto> allCards = storageResource.getCardsByCardStockId(cardStockId);
        if (!allCards.isEmpty()) {
            ids = allCards.stream().filter(it -> (mode.isFromKeyMode() && !Objects.equals(it.getStatusFromKey(), "COMPLETED")) || (!mode.isFromKeyMode() && !Objects.equals(it.getStatusToKey(), "COMPLETED"))).map(CardDto::getId).collect(Collectors.toList());
            Collections.shuffle(ids);
        }
        return ids;
    }

    public Menu createMenuByCallback(Integer storageId, UserState userState, EMenu currentMenu, String callback) {
        log.debug("createMenuByCallback. req:" + storageId + ", " + userState + ", " + currentMenu.name());
        List<CardStockDto> cardStocks = storageResource.getCardStocksByStorageId(storageId);
        Menu menu = null;

        switch (currentMenu) {
            case MAIN:
            case CARD_STOCKS:
                Optional<CardStockDto> cardStock = Optional.empty();
                if (!cardStocks.isEmpty()) {
                    Integer cardStockId = Integer.valueOf(callback);
                    cardStock = cardStocks.stream().filter(cardStockDto -> Objects.equals(cardStockDto.getId(), cardStockId)).findFirst();
                }

                if (cardStock.isPresent()) {
                    List<CardDto> cards = storageResource.getCardsByCardStockId(cardStock.get().getId());
                    menu = new CardStockMenu(cardStock.get(), cards);
                    userStateService.updateUserStateByMenu(userState, menu);
                }
                break;
            case CARDS:
                List<CardDto> cards = storageResource.getCardsByCardStockId(userState.getCardStockId());
                Optional<CardDto> card = Optional.empty();
                if (!cards.isEmpty()) {
                    Integer cardId = Integer.valueOf(callback);
                    card = cards.stream().filter(it -> Objects.equals(it.getId(), cardId)).findFirst();
                }

                if (card.isPresent()) {
                    int maxPoint = storageResource.getCardStockById(userState.getCardStockId()).getMaxPoint();
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
