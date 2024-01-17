package org.memorizing.resource;

import org.apache.log4j.Logger;
import org.memorizing.resource.cardApi.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class CardWebClientBuilder {
    private static final Logger log = Logger.getLogger(CardWebClientBuilder.class);

    private final String serviceName;
    private final String baseUrl;

    public CardWebClientBuilder(
            @Value("${localhost}")
            String localhost,
            @Value("${core-service.name}")
            String serviceName,
            @Value("${api-gateway.port}")
            String gatewayPort) {
        this.serviceName = serviceName;
        this.baseUrl = "http://"+localhost+":"+gatewayPort+"/"+serviceName;
    }

    // /storage
    public StorageDto getStorageByUserId(Long userId) {
        log.debug("getStorageByUserId: REQ to "+ serviceName +"/storage/getByUserId with req: " + userId);
        try {
            StorageDto req = new StorageDto(null, userId, null);
            return WebClient.create(baseUrl)
                    .post()
                    .uri("/storage/getByUserId")
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
            log.debug("getCardStocksByStorageId: REQ to " + serviceName +"/cardStocks with req: " + storageId);
            return WebClient.create(baseUrl)
                    .post()
                    .uri("/cardStocks")
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
            log.debug("createCardStock: REQ to " + serviceName +"/cardStock with req: " + req);
            return WebClient.create(baseUrl)
                    .post()
                    .uri("/cardStock")
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
            log.debug("updateCardStock: REQ to " + serviceName +"/cardStock/ with req: " + req);
            return WebClient.create(baseUrl)
                    .post()
                    .uri("/cardStock/" + cardStockId)
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
            log.debug("deleteCardStock: REQ to " + serviceName + "/cardStock/ with data: " + cardStockId);
            WebClient.create(baseUrl)
                    .delete()
                    .uri("/cardStock/" + cardStockId)
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
            log.debug("getCardsByCardStockId: REQ to " + serviceName +"/cards with data: " + cardStockId);
            return WebClient.create(baseUrl)
                    .post()
                    .uri("/cards")
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
            log.debug("getCardStockById: REQ to " + serviceName +"/cardStock/ with data: " + cardStockId);
            return WebClient.create(baseUrl)
                    .get()
                    .uri("/cardStock/" + cardStockId)
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
            log.debug("getCardById: REQ to " + serviceName +"/card/ with data:" + cardId);
            return WebClient.create(baseUrl)
                    .get()
                    .uri("/card/" + cardId)
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
            log.debug("createCard: REQ to " + serviceName +"/card with data: " + req);
            return WebClient.create(baseUrl)
                    .post()
                    .uri("/card")
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
            log.debug("updateCard: REQ to " + serviceName +"/card/ with data: " + req);
            return WebClient.create(baseUrl)
                    .post()
                    .uri("/card/" + cardId)
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
            log.debug("deleteCard: REQ to " + serviceName + "/card/ with data: " + cardId);
            WebClient.create(baseUrl)
                    .delete()
                    .uri("/card/" + cardId)
                    .retrieve()
                    .bodyToFlux(Void.class)
                    .blockFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    // TODO: remove it after adding user-service and auth-service
    public StorageDto createStorage(StorageFieldsDto req) {
        try {
            log.debug("createStorage: REQ to : " + serviceName + "/storage with data" + req);
            return WebClient.create(baseUrl)
                    .post()
                    .uri("/storage")
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
            log.debug("checkCard: REQ to : " + serviceName + "/card/" + cardId + "/check");
            return WebClient.create(baseUrl)
                    .post()
                    .uri("/card/" + cardId + "/check")
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
