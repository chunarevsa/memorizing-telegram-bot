package org.memorizing.repository;

import org.memorizing.controller.CardDto;
import org.memorizing.utils.CardWebClientBuilder;
import org.memorizing.utils.cardApi.CardStockDto;
import org.memorizing.utils.cardApi.StorageDto;
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
}
