package org.memorizing.resource.core.mq;

import org.memorizing.model.storage.Storage;
import org.memorizing.resource.core.StorageResource;
import org.springframework.stereotype.Component;

@Component
//@Profile("mq")
public class StorageMQResourceImpl implements StorageResource {

    private final StorageMessageProducer producer;

    public StorageMQResourceImpl(StorageMessageProducer producer) {
        this.producer = producer;
    }

    @Override
    public Storage createStorage(Storage storage) {
        producer.

        return null;
    }

    @Override
    public Storage getStorageByUserId(Long userId) {
        return null;
    }
}
