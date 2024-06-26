package com.memorizing.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import java.net.ProtocolException;

public class JsonObjectMapper implements IMapper {
    private static final Logger log = Logger.getLogger(JsonObjectMapper.class);
    private final ObjectMapper mapper = new ObjectMapper();


    @Override
    public String writeValueAsString(IMappable value) throws ProtocolException {
        String result = null;
        try {
            result = mapper.writeValueAsString(value);
        } catch (Exception e) {
            log.error("Bad request: " + value);
            e.printStackTrace();
            // TODO: Exception(e)
        }
        return result;
    }

    @Override
    public <T extends IMappable> T readValue(String content, Class<T> valueType) throws ProtocolException, JsonProcessingException {
        mapper.configure(
                JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(),
                true);
        return mapper.readValue(content, valueType);
    }

}
