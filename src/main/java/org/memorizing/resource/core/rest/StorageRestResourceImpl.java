package org.memorizing.resource.core.rest;

import org.memorizing.model.storage.Storage;
import org.memorizing.resource.core.StorageResource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

//@Component
//@Profile("rest") // TODO: add rest profile
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
