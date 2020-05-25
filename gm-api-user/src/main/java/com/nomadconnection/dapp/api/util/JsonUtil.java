package com.nomadconnection.dapp.api.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class JsonUtil {

    private static final ObjectMapper mapper;
    private static final ObjectMapper ignoreAnnotaionMapper;

    static {
        mapper = new ObjectMapper() {
            {
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            }
        };
        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE)
        );

        ignoreAnnotaionMapper = new ObjectMapper() {
            {
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            }
        };
        ignoreAnnotaionMapper.configure(MapperFeature.USE_ANNOTATIONS, false);
        ignoreAnnotaionMapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE)
        );
    }

    public static String generateClassToJson(Object obj) throws IOException {
        if (obj == null) {
            return null;
        }
        String json;
        try {
            json = mapper.writeValueAsString(obj);
        } catch (IOException e) {
            log.error("generateClassToJson json Exception error : " + e.getMessage() + ", toString : " + obj.toString());
            e.printStackTrace();
            throw e;
        }
        return json;
    }


    public static <T> T generateJsonToClass(String jsonData, TypeReference<T> valueTypeRef) throws IOException {
        if (jsonData == null) {
            return null;
        }
        T object;
        try {
            object = mapper.readValue(jsonData, valueTypeRef);
        } catch (IOException e) {
            log.error("## Failed to parse the response! : " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return object;
    }

    public static <T> String convertJson(T obj) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(obj);
    }


}
