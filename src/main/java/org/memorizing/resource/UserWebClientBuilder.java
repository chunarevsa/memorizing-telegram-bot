package org.memorizing.resource;

import org.apache.log4j.Logger;
import org.memorizing.resource.cardApi.StorageDto;
import org.memorizing.resource.cardApi.UserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class UserWebClientBuilder {
    private static final Logger log = Logger.getLogger(CardWebClientBuilder.class);

    private final String serviceName;
    private final String baseUrl;

    public UserWebClientBuilder(
            @Value("${localhost}")
            String localhost,
            @Value("${core-service.name}")
            String serviceName,
            @Value("${api-gateway.port}")
            String gatewayPort) {
        this.serviceName = serviceName;
        this.baseUrl = "http://" + localhost + ":" + gatewayPort + "/" + serviceName;
    }

    public UserDto getUserByChatId(Long chatId) {
        log.debug("getUserByChatId: REQ to " + serviceName + "/user/getByChatId with req: " + chatId);
        log.debug("!!! STUB: getStorageByUserId: REQ to " + serviceName + "/storage/getByUserId with req:" + chatId);
        try {
            StorageDto req = new StorageDto(null, chatId, null);
            StorageDto storageDto = WebClient.create(baseUrl)
                    .post()
                    .uri("/storage/getByUserId")
                    .bodyValue(req)
                    .retrieve()
                    .bodyToFlux(StorageDto.class)
                    .blockFirst();
            UserDto userDto = new UserDto(null, storageDto.getUserId(), null, storageDto.getId());
            log.debug("!!! STUB: we get userDto:" + userDto);
            return userDto;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isUserExistByChatId(Long chatId) {
        log.debug("isUserExistsByChatId: REQ to " + serviceName + "/user/getByChatId with req: " + chatId);
        log.debug("!!! STUB: getStorageByUserId: REQ to " + serviceName + "/storage/getByUserId with req:" + chatId);
        try {
            StorageDto req = new StorageDto(null, chatId, null);
            StorageDto storageDto = WebClient.create(baseUrl)
                    .post()
                    .uri("/storage/getByUserId")
                    .bodyValue(req)
                    .retrieve()
                    .bodyToFlux(StorageDto.class)
                    .blockFirst();

            log.debug("!!! STUB: Storage with chat id " + chatId + " exist");
            return storageDto != null;
        } catch (Exception e) {
            return false;
        }
    }
}
