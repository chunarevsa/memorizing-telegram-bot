package com.memorizing.resource.core.rest;

import com.memorizing.model.storage.CardStock;
import com.memorizing.resource.core.CardStockResource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("rest")
public class CardStockRestResourceImpl implements CardStockResource {

    private final CoreWebClientBuilder coreWebClientBuilder;

    public CardStockRestResourceImpl(CoreWebClientBuilder coreWebClientBuilder) {
        this.coreWebClientBuilder = coreWebClientBuilder;
    }

    @Override
    public List<CardStock> getCardStocksByStorageId(Integer storageId) {
        return coreWebClientBuilder.getCardStocksByStorageId(storageId);
    }

    @Override
    public CardStock getCardStockById(Integer cardStockId) {
        return coreWebClientBuilder.getCardStockById(cardStockId);
    }

    @Override
    public CardStock createCardStock(CardStock cardStock) {
        return coreWebClientBuilder.createCardStock(cardStock);
    }

    @Override
    public CardStock updateCardStock(CardStock req, Integer cardStockId) {
        return coreWebClientBuilder.updateCardStock(req, cardStockId);
    }

    @Override
    public void deleteCardStock(Integer cardStockId) {
        coreWebClientBuilder.deleteCardStock(cardStockId);
    }

}
