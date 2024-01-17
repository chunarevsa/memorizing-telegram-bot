package org.memorizing.resource;

import org.memorizing.resource.cardApi.*;

import java.util.List;

public interface StorageResource {

    // /storage
    StorageDto getStorageByUserId(Long userId);

    // /cardStock
    List<CardStockDto> getCardStocksByStorageId(Integer storageId);
    CardStockDto createCardStock(CardStockFieldsDto req);
    CardStockDto updateCardStock(CardStockFieldsDto cardStockFieldsDto, Integer cardStockId);
    void deleteCardStock(Integer cardStockId);

    // /card
    List<CardDto> getCardsByCardStockId(Integer id);

    CardStockDto getCardStockById(Integer cardStockId);

    CardDto getCardById(Integer cardId);
    CardDto createCard(CardFieldsDto cardFieldsDto);

    CardDto updateCard(CardFieldsDto cardFieldsDto, Integer cardId);

    void deleteCard(Integer cardId);

    @Deprecated
    StorageDto createStorage(StorageFieldsDto storageFieldsDto);

    TestResultDto checkCard(Integer cardStockId, Integer cardId, String userValue, boolean fromKeyMode);

    TestResultDto skipCard(Integer cardStockId, Integer cardId, boolean fromKeyMode);

}
