package org.memorizing.service;

import org.apache.log4j.Logger;
import org.memorizing.entity.CardStockHistory;
import org.memorizing.entity.UserState;
import org.memorizing.model.EMode;
import org.memorizing.model.menu.*;
import org.memorizing.repository.CardStockHistoryRepository;
import org.memorizing.repository.UserStateRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserStateService {
    private static final Logger log = Logger.getLogger(UserStateService.class);
    private final UserStateRepository userStateRepository;
    private final CardStockHistoryRepository cardStockHistoryRepository;

    public UserStateService(UserStateRepository userStateRepository, CardStockHistoryRepository cardStockHistoryRepository) {
        this.userStateRepository = userStateRepository;
        this.cardStockHistoryRepository = cardStockHistoryRepository;
    }

    public void updateUserStateByMenu(UserState userState, Menu menu) {
        log.debug("updateUserStateByMenu. userStateId:" + userState.getId());
        if (menu == null) return;

        userState.setCurrentMenu(menu.getCurrentMenu());

        if (menu instanceof CardStocksMenu) {
            userState.setCardStockId(null);
            userState.setCardId(null);

        } else if (menu instanceof CardStockMenu) {
            CardStockMenu cardStockMenu = (CardStockMenu) menu;
            userState.setCardStockId(cardStockMenu.getCardStock().getId());
            userState.setCardId(null);

        } else if (menu instanceof AStudyingMenu) {
            AStudyingMenu testMenu = (AStudyingMenu) menu;

            Optional<CardStockHistory> cardStockHistory = findCardStockHistoryByCardStockId(userState.getCardStockId());
            CardStockHistory history;

            if (userState.getStudyingHistory().isEmpty() || cardStockHistory.isEmpty()) {
                history = new CardStockHistory(userState);
                history.updateStudyingStateIds(testMenu.getMode(), testMenu.getIds());
                userState.addStudyingHistory(history);
            } else {
                history = cardStockHistory.get();
                history.updateStudyingStateIds(testMenu.getMode(), testMenu.getIds());
                userState.updateStudyingHistory(history);
            }

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

        userStateRepository.save(userState);
    }

    public UserState deleteCardStockIdFromSessionAndGet(UserState userState) {
        log.debug("deleteCardStockIdFromSessionAndGet:" + userState.getCardStockId());
        List<CardStockHistory> listOfHistory = cardStockHistoryRepository.findAllByCardStockId(userState.getCardStockId());
        if (!listOfHistory.isEmpty()) {
            cardStockHistoryRepository.deleteAll(listOfHistory);
        }
        userState.setCardStockId(null);
        return userState;
    }

    public UserState deleteCardIdFromSessionAndGet(UserState userState, Integer cardId) {
        log.debug("deleteCardIdFromSessionAndGet:" + userState.getCardStockId());

        List<CardStockHistory> cardStockHistory = cardStockHistoryRepository.findAllByCardStockId(userState.getCardStockId());
        if (cardStockHistory.isEmpty()) return userState;

        cardStockHistory.forEach(history -> {
                    Map<String, List<Integer>> studyingState = history.getStudyingState();

                    studyingState.keySet().stream()
                            .filter(key -> studyingState.get(key).stream().anyMatch(it -> it.equals(cardId)))
                            .forEach(key -> {
                                List<Integer> ids = studyingState.get(key);
                                ids.remove(cardId);
                                history.updateStudyingStateIds(EMode.valueOf(key), ids);
                            });
                    cardStockHistoryRepository.save(history);
                }
        );

        userState.setCardId(null);
        return userState;
    }

    public void deleteCardIdFromStudyingHistoryByMode(UserState userState, Integer cardId, EMode mode) {
        log.debug("deleteCardIdFromStudyingHistoryByMode:" + userState.getCardStockId() + ", " + cardId + ", " + mode.name());

        List<CardStockHistory> studyingHistory = userState.getStudyingHistory();
        if (studyingHistory.isEmpty()) return;

        studyingHistory.forEach(history -> {
            List<Integer> ids = history.getStudyingState().get(mode.name());
            ids.remove(cardId);
            history.updateStudyingStateIds(mode, ids);
            cardStockHistoryRepository.save(history);
        });

    }

    public List<Integer> getCardIdsByMode(UserState userState, String modeName) {
        List<Integer> ids = new ArrayList<>();

        Optional<CardStockHistory> cardStockHistory = findCardStockHistoryByCardStockId(userState.getCardStockId());

        if (cardStockHistory.isPresent()) {
            ids = cardStockHistory.get().getStudyingState().get(modeName);
        }
        return ids;
    }

    public List<Integer> getCardIdsByHistory(CardStockHistory history, String modeName) {
        return history.getStudyingState().get(modeName);
    }

    public void updateHistoryByNewIds(CardStockHistory cardStockHistory, EMode mode, List<Integer> ids) {
        cardStockHistory.updateStudyingStateIds(mode, ids);
        cardStockHistoryRepository.save(cardStockHistory);
    }

    public Optional<CardStockHistory> findCardStockHistoryByCardStockId(Integer cardStockId) {
        return cardStockHistoryRepository.findAllByCardStockId(cardStockId).stream().findFirst();
    }

    public Optional<UserState> findUserStateById(Integer id) {
        return userStateRepository.findById(id);
    }
}
