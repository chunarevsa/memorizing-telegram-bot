package org.memorizing.resource;

import org.memorizing.resource.cardApi.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StorageResourceImpl implements StorageResource {

    private final CardWebClientBuilder cardWebClientBuilder;

    public StorageResourceImpl(CardWebClientBuilder cardWebClientBuilder) {
        this.cardWebClientBuilder = cardWebClientBuilder;
    }

    // /storage

    @Override
    public StorageDto getStorageByUserId(Integer userId) {
        return cardWebClientBuilder.getStorageByUserId(userId);
    }


    // / cardStock
    @Override
    public List<CardStockDto> getCardStocksByStorageId(Integer storageId) {
        return cardWebClientBuilder.getCardStocksByStorageId(storageId);
    }

    @Override
    public CardStockDto createCardStock(CardStockFieldsDto req) {
        return cardWebClientBuilder.createCardStock(req);
    }

    @Override
    public CardStockDto updateCardStock(CardStockFieldsDto req, Integer cardStockId) {
        return cardWebClientBuilder.updateCardStock(req, cardStockId);
    }

    @Override
    public void deleteCardStock(Integer cardStockId) {
        cardWebClientBuilder.deleteCardStock(cardStockId);
    }


    // /card
    @Override
    public List<CardDto> getCardsByCardStockId(Integer id) {
        return cardWebClientBuilder.getCardsByCardStockId(id);
    }

    @Override
    public CardStockDto getCardStockById(Integer cardStockId) {
        return cardWebClientBuilder.getCardStockById(cardStockId);
    }

    @Override
    public CardDto getCardById(Integer cardId) {
        return cardWebClientBuilder.getCardById(cardId);
    }

    @Override
    public CardDto createCard(CardFieldsDto req) {
        return cardWebClientBuilder.createCard(req);
    }

    @Override
    public CardDto updateCard(CardFieldsDto req, Integer cardId) {
        return cardWebClientBuilder.updateCard(req, cardId);
    }

    @Override
    public void deleteCard(Integer cardId) {
        cardWebClientBuilder.deleteCard(cardId);
    }

    // TODO: TEMP
    @Override
    public StorageDto createStorage(StorageFieldsDto req) {
        return cardWebClientBuilder.createStorage(req);
    }
}