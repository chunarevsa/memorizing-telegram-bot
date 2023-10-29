package org.memorizing.resource;

import org.memorizing.resource.cardApi.CardDto;
import org.memorizing.resource.cardApi.CardStockDto;
import org.memorizing.resource.cardApi.StorageDto;

import java.util.List;

public interface StorageResource {
    StorageDto getStorageByUserId(Integer userId);

    List<CardStockDto> getCardStocksByStorageId(Integer storageId);

    List<CardDto> getCardsByCardStockId(Integer id);

    CardStockDto getCardStockById(Integer cardStockId);

    CardDto getCardById(Integer cardId);
}
