package org.memorizing.resource;

import org.apache.log4j.Logger;
import org.memorizing.resource.cardApi.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class CardWebClientBuilder {
    private static final Logger log = Logger.getLogger(CardWebClientBuilder.class);
    private static final String baseUrl = "http://192.168.0.106:8095/";
    private static final String serviceName = "";

    // /storage
    public StorageDto getStorageByUserId(Long userId) {
        log.debug("getStorageByUserId with req: " + userId);
        try {
            StorageDto req = new StorageDto(null, userId, null);
            return WebClient.create(baseUrl)
                    .post()
                    .uri(serviceName + "/storage/getByUserId")
                    .bodyValue(req)
                    .retrieve()
                    .bodyToFlux(StorageDto.class)
                    .blockFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // /cardStock
    public List<CardStockDto> getCardStocksByStorageId(Integer storageId) {
        try {
            log.debug("retrieve req to serviceName + \"/cardStocks\" with req: " + storageId);
            return WebClient.create(baseUrl)
                    .post()
                    .uri(serviceName + "/cardStocks")
                    .bodyValue(storageId)
                    .retrieve()
                    .bodyToFlux(CardStockDto.class)
                    .buffer()
                    .blockFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public CardStockDto createCardStock(CardStockFieldsDto req) {
        try {
            log.debug("retrieve req to serviceName + \"/cardStock\" with req: " + req);
            return WebClient.create(baseUrl)
                    .post()
                    .uri(serviceName + "/cardStock")
                    .bodyValue(req)
                    .retrieve()
                    .bodyToFlux(CardStockDto.class)
                    .blockFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public CardStockDto updateCardStock(CardStockFieldsDto req, Integer cardStockId) {
        try {
            log.debug("retrieve req to serviceName + \"/cardStock\" with req: " + req);
            return WebClient.create(baseUrl)
                    .post()
                    .uri(serviceName + "/cardStock/" + cardStockId)
                    .bodyValue(req)
                    .retrieve()
                    .bodyToFlux(CardStockDto.class)
                    .blockFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteCardStock(Integer cardStockId) {
        try {
            log.debug("deleteCardStock req: " + cardStockId);
            WebClient.create(baseUrl)
                    .delete()
                    .uri(serviceName + "/cardStock/" + cardStockId)
                    .retrieve()
                    .bodyToFlux(Void.class)
                    .blockFirst();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // /card
    public List<CardDto> getCardsByCardStockId(Integer cardStockId) {
        try {
            log.debug("retrieve req to serviceName + \"/cards\" with req: " + cardStockId);
            return WebClient.create(baseUrl)
                    .post()
                    .uri(serviceName + "/cards")
                    .bodyValue(cardStockId)
                    .retrieve()
                    .bodyToFlux(CardDto.class)
                    .buffer()
                    .blockFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public CardStockDto getCardStockById(Integer cardStockId) {
        try {
            log.debug("retrieve req to serviceName + \"/cardStock\" with req: " + cardStockId);
            return WebClient.create(baseUrl)
                    .get()
                    .uri(serviceName + "/cardStock/" + cardStockId)
                    .retrieve()
                    .bodyToFlux(CardStockDto.class)
                    .blockFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public CardDto getCardById(Integer cardId) {
        try {
            log.debug("retrieve req to serviceName + \"/card/ " + cardId);
            return WebClient.create(baseUrl)
                    .get()
                    .uri(serviceName + "/card/" + cardId)
                    .retrieve()
                    .bodyToFlux(CardDto.class)
                    .blockFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public CardDto createCard(CardFieldsDto req) {
        try {
            log.debug("retrieve req to serviceName + \"/card\" with req: " + req);
            return WebClient.create(baseUrl)
                    .post()
                    .uri(serviceName + "/card")
                    .bodyValue(req)
                    .retrieve()
                    .bodyToFlux(CardDto.class)
                    .blockFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public CardDto updateCard(CardFieldsDto req, Integer cardId) {
        try {
            log.debug("retrieve req to serviceName + \"/card\" with req: " + req);
            return WebClient.create(baseUrl)
                    .post()
                    .uri(serviceName + "/card/" + cardId)
                    .bodyValue(req)
                    .retrieve()
                    .bodyToFlux(CardDto.class)
                    .blockFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteCard(Integer cardId) {
        try {
            log.debug("deleteCard req: " + cardId);
            WebClient.create(baseUrl)
                    .delete()
                    .uri(serviceName + "/card/" + cardId)
                    .retrieve()
                    .bodyToFlux(Void.class)
                    .blockFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public StorageDto createStorage(StorageFieldsDto req) {
        try {
            log.debug("createStorage req: " + req);
            return WebClient.create(baseUrl)
                    .post()
                    .uri(serviceName + "/storage")
                    .bodyValue(req)
                    .retrieve()
                    .bodyToFlux(StorageDto.class)
                    .blockFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public TestResultDto checkCard(Integer cardId, CheckCardDto req) {
        try {
            log.debug("checkCard req: " + req);
            return WebClient.create(baseUrl)
                    .post()
                    .uri(serviceName + "/card/" + cardId + "/check")
                    .bodyValue(req)
                    .retrieve()
                    .bodyToFlux(TestResultDto.class)
                    .blockFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
