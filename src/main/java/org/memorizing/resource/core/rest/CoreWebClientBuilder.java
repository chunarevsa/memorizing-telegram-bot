package org.memorizing.resource.core.rest;

import com.memorizing.commonapi.model.CheckCardDto;
import org.apache.log4j.Logger;
import org.memorizing.config.ApplicationEnvironmentConfig;
import org.memorizing.model.storage.Card;
import org.memorizing.resource.core.rest.dto.card.CardFieldsDto;
import org.memorizing.model.storage.TestResult;
import org.memorizing.model.storage.CardStock;
import org.memorizing.resource.core.rest.dto.cardStock.CardStockFieldsDto;
import org.memorizing.model.storage.Storage;
import org.memorizing.resource.core.rest.dto.storage.StorageFieldsDto;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Component
@Profile("rest")
public class CoreWebClientBuilder {
    private static final Logger log = Logger.getLogger(CoreWebClientBuilder.class);
    private final String baseUrl;
    private final String serviceName;
    public CoreWebClientBuilder(ApplicationEnvironmentConfig config) {
        this.serviceName = config.getCoreServiceName();
        this.baseUrl = UriComponentsBuilder.newInstance()
                .scheme("http").host(config.getLocalhost()).port(config.getGatewayPort())
                .path("/" + config.getCoreServiceName())
                .build().toUriString();
    }

    // /storage
    @Deprecated
    // TODO: remove it after adding user-service and auth-service
    public Storage createStorage(Storage storage) {
        StorageFieldsDto req = new StorageFieldsDto();
        req.setStorageName(storage.getStorageName());
        req.setUserId(storage.getUserId());

        try {
            log.debug("createStorage: REQ to : " + serviceName + "/storage/create with data" + req);
            return WebClient.create(baseUrl)
                    .post()
                    .uri("/storage/create")
                    .bodyValue(req)
                    .retrieve()
                    .bodyToFlux(Storage.class)
                    .blockFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Storage getStorageByUserId(Long userId) {
        StorageFieldsDto req = new StorageFieldsDto();
        req.setUserId(userId);

        try {
            log.debug("getStorageByUserId: REQ to " + serviceName + "/storage/getByUserId with req: " + req);
            return WebClient.create(baseUrl)
                    .post()
                    .uri("/storage/getByUserId")
                    .bodyValue(req)
                    .retrieve()
                    .bodyToFlux(Storage.class)
                    .blockFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // /cardStock

    public CardStock getCardStockById(Integer cardStockId) {
        try {
            log.debug("getCardStockById: REQ to " + serviceName + "/cardStock/ with data: " + cardStockId);
            return WebClient.create(baseUrl)
                    .get()
                    .uri("/cardStock/" + cardStockId)
                    .retrieve()
                    .bodyToFlux(CardStock.class)
                    .blockFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<CardStock> getCardStocksByStorageId(Integer storageId) {
        CardStockFieldsDto req = new CardStockFieldsDto();
        req.setStorageId(storageId);

        try {
            log.debug("getCardStocksByStorageId: REQ to " + serviceName + "/cardStock/getAllByStorageId with req: " + req);
            return WebClient.create(baseUrl)
                    .post()
                    .uri("/cardStock/getAllByStorageId")
                    .bodyValue(req)
                    .retrieve()
                    .bodyToFlux(CardStock.class)
                    .buffer()
                    .blockFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public CardStock createCardStock(CardStock cardStock) {
        CardStockFieldsDto req = new CardStockFieldsDto();
        req.setStorageId(cardStock.getStorageId());
        req.setCardStockName(cardStock.getCardStockName());
        req.setDescription(cardStock.getDescription());
        req.setKeyType(cardStock.getKeyType());
        req.setValueType(cardStock.getValueType());

        try {
            log.debug("createCardStock: REQ to " + serviceName + "/cardStock/create with req: " + req);
            return WebClient.create(baseUrl)
                    .post()
                    .uri("/cardStock/create")
                    .bodyValue(req)
                    .retrieve()
                    .bodyToFlux(CardStock.class)
                    .blockFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public CardStock updateCardStock(CardStock cardStock, Integer cardStockId) {
        CardStockFieldsDto req = new CardStockFieldsDto(
                null,
                cardStock.getCardStockName(),
                cardStock.getDescription(),
                cardStock.getKeyType(),
                cardStock.getValueType(),
                cardStock.getMaxPoint(),
                cardStock.getTestModeIsAvailable(),
                cardStock.getOnlyFromKey()
        );

        try {
            log.debug("updateCardStock: REQ to " + serviceName + "/cardStock/ with req: " + req);
            return WebClient.create(baseUrl)
                    .post()
                    .uri("/cardStock/" + cardStockId + "/update")
                    .bodyValue(req)
                    .retrieve()
                    .bodyToFlux(CardStock.class)
                    .blockFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteCardStock(Integer cardStockId) {
        try {
            log.debug("deleteCardStock: REQ to " + serviceName + "/cardStock/delete with data: " + cardStockId);
            WebClient.create(baseUrl)
                    .post()
                    .uri("/cardStock/" + cardStockId + "/delete")
                    .retrieve()
                    .bodyToFlux(Void.class)
                    .blockFirst();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // /card
    public List<Card> getCardsByCardStockId(Integer cardStockId) {
        CardFieldsDto req = new CardFieldsDto();
        req.setCardStockId(cardStockId);

        try {
            log.debug("getCardsByCardStockId: REQ to " + serviceName + "/card/getAllByCardStockId with req: " + req);
            return WebClient.create(baseUrl)
                    .post()
                    .uri("/card/getAllByCardStockId")
                    .bodyValue(req)
                    .retrieve()
                    .bodyToFlux(Card.class)
                    .buffer()
                    .blockFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Card getCardById(Integer cardId) {
        try {
            log.debug("getCardById: REQ to " + serviceName + "/card/ with data:" + cardId);
            return WebClient.create(baseUrl)
                    .get()
                    .uri("/card/" + cardId)
                    .retrieve()
                    .bodyToFlux(Card.class)
                    .blockFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Card createCard(Card card, Boolean onlyFromKey) {
        CardFieldsDto req = new CardFieldsDto();
        req.setCardStockId(card.getCardStockId());
        req.setCardKey(card.getCardKey());
        req.setCardValue(card.getCardValue());
        req.setOnlyFromKey(onlyFromKey);

        try {
            log.debug("createCard: REQ to " + serviceName + "/card/create with data: " + req);
            return WebClient.create(baseUrl)
                    .post()
                    .uri("/card/create")
                    .bodyValue(req)
                    .retrieve()
                    .bodyToFlux(Card.class)
                    .blockFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Card updateCard(Card card, Integer cardId) {
        try {
            CardFieldsDto req = new CardFieldsDto();
            req.setCardValue(card.getCardValue());

            log.debug("updateCard: REQ to " + serviceName + "/card/" + cardId + "/update with data: " + req);
            return WebClient.create(baseUrl)
                    .post()
                    .uri("/card/" + cardId + "/update")
                    .bodyValue(req)
                    .retrieve()
                    .bodyToFlux(Card.class)
                    .blockFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteCard(Integer cardId) {
        try {
            log.debug("deleteCard: REQ to " + serviceName + "/card/" + cardId + "/delete with data: " + cardId);
            WebClient.create(baseUrl)
                    .post()
                    .uri("/card/" + cardId + "/delete")
                    .retrieve()
                    .bodyToFlux(Void.class)
                    .blockFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TestResult checkCard(Integer cardId, Integer cardStockId, String userValue, boolean fromKeyMode, int maxPoint) {
        CheckCardDto req = new CheckCardDto();
        req.setCardStockId(cardStockId);
        req.setUserValue(userValue);
        req.setFromKey(fromKeyMode);
        req.setMaxPoint(maxPoint);

        try {
            log.debug("checkCard: REQ to : " + serviceName + "/card/" + cardId + "/check");
            return WebClient.create(baseUrl)
                    .post()
                    .uri("/card/" + cardId + "/check")
                    .bodyValue(req)
                    .retrieve()
                    .bodyToFlux(TestResult.class)
                    .blockFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
