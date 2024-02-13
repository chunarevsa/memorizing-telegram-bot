package org.memorizing.resource.core;

import org.memorizing.model.storage.Storage;

public interface StorageResource {
    @Deprecated
    Storage createStorage(Storage storage);

    Storage getStorageByUserId(Long userId);

}
