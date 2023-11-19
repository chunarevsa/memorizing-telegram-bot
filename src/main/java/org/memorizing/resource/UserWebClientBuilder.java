package org.memorizing.resource;

import org.apache.log4j.Logger;
import org.memorizing.resource.cardApi.UserDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserWebClientBuilder {
    private static final Logger log = Logger.getLogger(CardWebClientBuilder.class);
    // TODO edit url
    // TODO add api map
    private static final String baseUrl = "http://localhost:8098/";
    private static final String serviceName = "";

    public UserDto getUserByChatId(Long chatId) {
        try {
            UserDto req = new UserDto(null, chatId);
            log.debug("retrieve req to serviceName + \"/user/getByChatId\" with req: " + req);
//            return WebClient.create(baseUrl)
//                    .post()
//                    .uri(serviceName + "/user/getByChatId")
//                    .bodyValue(req)
//                    .retrieve()
//                    .bodyToFlux(UserDto.class)
//                    .blockFirst();
            // TODO: TEMP
            return new UserDto(1, chatId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<UserDto> getChatIdListWithUserId() {
        try {
            log.debug("retrieve req to serviceName + \"/user/getChatIdListWithUserId\" with req: ");

//            return WebClient.create(baseUrl)
//                    .post()
//                    .uri(serviceName + "/user/getByChatId")
//                    .bodyValue(req)
//                    .retrieve()
//                    .bodyToFlux(UserDto.class)
//                    .blockFirst();
            // TODO: TEMP
            UserDto userDto = new UserDto(1, 1L);
            List<UserDto> list = new ArrayList<>();
            list.add(userDto);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
