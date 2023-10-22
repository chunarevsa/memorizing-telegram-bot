package org.memorizing.repository;

import org.memorizing.utils.cardApi.StorageDto;
import org.springframework.stereotype.Component;

@Component
public class StorageResourceImpl implements StorageResource {

    @Override
    public StorageDto getStorageByUserId(Integer userId) {
        return null;
    }
}
