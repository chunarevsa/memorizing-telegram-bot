package com.memorizing.resource.core.rest;

import com.memorizing.model.storage.Card;
import com.memorizing.model.storage.TestResult;
import com.memorizing.resource.core.CardResource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("rest")
public class CardRestResourceImpl implements CardResource {
    private final CoreWebClientBuilder coreWebClientBuilder;

    public CardRestResourceImpl(CoreWebClientBuilder coreWebClientBuilder) {
        this.coreWebClientBuilder = coreWebClientBuilder;
    }

    @Override
    public List<Card> getCardsByCardStockId(Integer id) {
        return coreWebClientBuilder.getCardsByCardStockId(id);
    }

    @Override
    public Card getCardById(Integer cardId) {
        return coreWebClientBuilder.getCardById(cardId);
    }

    @Override
    public Card createCard(Card card, Boolean onlyFromKey) {
        return coreWebClientBuilder.createCard(card, onlyFromKey);
    }

    @Override
    public Card updateCard(Card card, Integer cardId) {
        return coreWebClientBuilder.updateCard(card, cardId);
    }

    @Override
    public void deleteCard(Integer cardId) {
        coreWebClientBuilder.deleteCard(cardId);
    }

    @Override
    public TestResult checkCard(Integer cardStockId, Integer cardId, String userValue, boolean fromKeyMode) {
        int maxPoint = coreWebClientBuilder.getCardStockById(cardStockId).getMaxPoint();
        return coreWebClientBuilder.checkCard(cardId, cardStockId, userValue, fromKeyMode, maxPoint);
    }

    @Override
    public TestResult skipCard(Integer cardStockId, Integer cardId, boolean fromKeyMode) {
        return checkCard(cardStockId, cardId, "!!!", fromKeyMode);
    }

}
