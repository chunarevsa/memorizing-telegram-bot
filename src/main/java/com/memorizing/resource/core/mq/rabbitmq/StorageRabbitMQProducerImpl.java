package com.memorizing.resource.core.mq.rabbitmq;

//import org.springframework.messaging.Message;


//@Configuration
////@Profile("rabbitMQ")
//public class StorageRabbitMQProducerImpl implements StorageMessageProducer {
//
//    Sinks.Many<Message<StorageFieldsDto>> innerBus = Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);
//
//    @Bean
//    public Supplier<Flux<Message<StorageFieldsDto>>> createStorage() {
//        Sinks.Many<Message<StorageFieldsDto>> innerBus1 = this.innerBus;
//
//        return () -> this.innerBus.asFlux();
//    }
//
//    @Bean
//    public Supplier<Flux<Message<Long>>> getStorageByUserId() {
//
//        return innerBus::asFlux;
//    }

//
//    @Bean
//    public Function<Flux<Message<StorageFieldsDto>>, Flux<Message<Storage>>> createStorageByFunc() {
//        return fluxMessages -> fluxMessages.map(message -> {
//            StorageFieldsDto storageFieldsDto = message.getPayload();
//            // Обработка полученного сообщения
//            // ...
//            Storage storage = new Storage(storageFieldsDto.)
//            return MessageBuilder.withPayload(storage).build();
//        });
//    }
//
//    @Bean
//    public Function<Mono<Message<StorageFieldsDto>>, Mono<Message<Storage>>> createStorageByFuncAndMono() {
//        return value -> value;
//    }

//    @Bean
//    public Function<Mono<Message<StorageFieldsDto>>, Mono<Message<Storage>>> storageMono() {
//        return monoMessage -> monoMessage.map(message -> {
//            StorageFieldsDto storageFieldsDto = message.getPayload();
//            // Обработка полученного сообщения
//            // ...
//
//
//            Storage storage = new Storage()
//            return MessageBuilder.withPayload(storage).build();
//        });
//    }

//    @Bean
//    public Function<Flux<Message<StorageFieldsDto>>, Flux<Message<Storage>>> storageFlux() {
//        return fluxMessages -> fluxMessages.map(message -> {
//            StorageFieldsDto storageFieldsDto = message.getPayload();
//            // Обработка полученного сообщения
//            // ...
//            Storage storage = convertToStorage(storageFieldsDto);
//            return MessageBuilder.withPayload(storage).build();
//        });
//    }

//}
