package com.memorizing.resource.core;

import com.memorizing.model.storage.CardStock;

import java.util.List;

public interface CardStockResource {

    CardStock getCardStockById(Integer cardStockId);

    List<CardStock> getCardStocksByStorageId(Integer storageId);

    CardStock createCardStock(CardStock req);

    CardStock updateCardStock(CardStock cardStock, Integer cardStockId);

    void deleteCardStock(Integer cardStockId);
}
