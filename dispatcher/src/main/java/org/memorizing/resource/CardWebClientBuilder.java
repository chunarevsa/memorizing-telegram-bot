package org.memorizing.resource;

import org.apache.log4j.Logger;
import org.memorizing.resource.cardApi.CardDto;
import org.memorizing.resource.cardApi.CardStockDto;
import org.memorizing.resource.cardApi.StorageDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class CardWebClientBuilder {
    private static final Logger log = Logger.getLogger(CardWebClientBuilder.class);
    private static final String baseUrl = "http://localhost:8095/";
    private static final String serviceName = "";

    public StorageDto getStorageByUserId(Integer userId) {
        try {
            StorageDto req = new StorageDto(null, userId, null);
            log.debug("retrieve req to serviceName + \"/storage/getByUserId\" with req: " + req);
            return WebClient.create(baseUrl)
                    .post()
                    .uri(serviceName + "/storage/getByUserId")
                    .bodyValue(req)
                    .retrieve()
                    .bodyToFlux(StorageDto.class)
                    .blockFirst();
            // TODO: TEMP
//            return new StorageDto(1, userId, "Some name");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<CardStockDto> getCardStocksByStorageId(Integer storageId) {
        try {
            log.debug("retrieve req to serviceName + \"/cardStorages\" with req: " + storageId);
            return WebClient.create(baseUrl)
                    .post()
                    .uri(serviceName + "/cardStocks")
                    .bodyValue(storageId)
                    .retrieve()
                    .bodyToFlux( CardStockDto.class )
                    .buffer()
                    .blockFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

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
}
