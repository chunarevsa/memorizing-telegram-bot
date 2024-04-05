package com.memorizing.resource.core.rest;

import com.memorizing.model.storage.Storage;
import com.memorizing.resource.core.StorageResource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("rest")
public class StorageRestResourceImpl implements StorageResource {
    private final CoreWebClientBuilder coreWebClientBuilder;

    public StorageRestResourceImpl(CoreWebClientBuilder coreWebClientBuilder) {
        this.coreWebClientBuilder = coreWebClientBuilder;
    }

    // TODO: TEMP
    @Override
    @Deprecated
    public Storage createStorage(Storage storage) {
        return coreWebClientBuilder.createStorage(storage);
    }

    @Override
    public Storage getStorageByUserId(Long userId) {
        return coreWebClientBuilder.getStorageByUserId(userId);
    }

}
