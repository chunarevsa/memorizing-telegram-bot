package org.memorizing.resource.core;

import org.memorizing.model.storage.Card;
import org.memorizing.model.storage.TestResult;

import java.util.List;

public interface CardResource {

    List<Card> getCardsByCardStockId(Integer id);

    Card getCardById(Integer cardId);

    Card createCard(Card card, Boolean onlyFromKey);

    Card updateCard(Card card, Integer cardId);

    void deleteCard(Integer cardId);

    TestResult checkCard(Integer cardStockId, Integer cardId, String userValue, boolean fromKeyMode);

    TestResult skipCard(Integer cardStockId, Integer cardId, boolean fromKeyMode);
}
