package org.memorizing.resource;

import org.apache.log4j.Logger;
import org.memorizing.resource.cardApi.UserDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserWebClientBuilder {
    private static final Logger log = Logger.getLogger(CardWebClientBuilder.class);
    private static final String baseUrl = "http://localhost:8098/";
    private static final String serviceName = "";

    public UserDto getUserByChatId(Long chatId) {
        try {
            UserDto req = new UserDto(null, chatId);
            log.debug("getUserByChatId: REQ to " + serviceName + "/user/getByChatId with req: " + req);
            // TODO: update after creating user-service
//            return WebClient.create(baseUrl)
//                    .post()
//                    .uri(serviceName + "/user/getByChatId")
//                    .bodyValue(req)
//                    .retrieve()
//                    .bodyToFlux(UserDto.class)
//                    .blockFirst();
            UserDto userDto = new UserDto(1, chatId);
            log.debug("!!! CREATED STUB:" + userDto);
            return userDto;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<UserDto> getChatIdListWithUserId() {
        try {
            log.debug("getChatIdListWithUserId: REQ to " + serviceName + "/user/getChatIdListWithUserId with data: ");
            // TODO: update after creating user-service
//            return WebClient.create(baseUrl)
//                    .post()
//                    .uri(serviceName + "/user/getByChatId")
//                    .bodyValue(req)
//                    .retrieve()
//                    .bodyToFlux(UserDto.class)
//                    .blockFirst();
            UserDto userDto = new UserDto(1, 1L);
            log.debug("!!! CREATED STUB:" + userDto);
            List<UserDto> list = new ArrayList<>();
            list.add(userDto);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
