package org.memorizing.repository;

import org.memorizing.utils.cardApi.StorageDto;

public interface StorageResource {
    StorageDto getStorageByUserId(Integer userId);
}
