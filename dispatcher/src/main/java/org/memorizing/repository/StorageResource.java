package org.memorizing.repository;

import org.memorizing.controller.CardDto;
import org.memorizing.utils.cardApi.CardStockDto;
import org.memorizing.utils.cardApi.StorageDto;

import java.util.List;

public interface StorageResource {
    StorageDto getStorageByUserId(Integer userId);

    List<CardStockDto> getCardStocksByStorageId(Integer storageId);

    List<CardDto> getCardsByCardStockId(Integer id);
}
