package com.memorizing.resource.core;

import com.memorizing.model.storage.Storage;

public interface StorageResource {
    @Deprecated
    Storage createStorage(Storage storage);

    Storage getStorageByUserId(Long userId);

}
