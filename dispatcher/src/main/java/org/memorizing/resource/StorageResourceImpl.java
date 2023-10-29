package org.memorizing.resource;

import org.memorizing.resource.cardApi.CardDto;
import org.memorizing.resource.cardApi.CardStockDto;
import org.memorizing.resource.cardApi.StorageDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StorageResourceImpl implements StorageResource {

    private final CardWebClientBuilder cardWebClientBuilder;

    public StorageResourceImpl(CardWebClientBuilder cardWebClientBuilder) {
        this.cardWebClientBuilder = cardWebClientBuilder;
    }

    @Override
    public StorageDto getStorageByUserId(Integer userId) {
        return cardWebClientBuilder.getStorageByUserId(userId);
    }

    @Override
    public List<CardStockDto> getCardStocksByStorageId(Integer storageId) {
        return cardWebClientBuilder.getCardStocksByStorageId(storageId);
    }

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
}
