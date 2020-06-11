package com.nomadconnection.dapp.api.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggingUtils {

    /**
     * Convert VO to pretty json string
     */
    public static String getPrettyJsonString(Object vo) {
        try {
            return getPrettyJsonStringIfErrorThrowEx(vo);
        } catch (Exception e) {
            log.warn("Failed, VO format to JSON. VO => {}", vo.toString());
            return null;
        }
    }

    private static String getPrettyJsonStringIfErrorThrowEx(Object vo) throws JsonProcessingException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);    // multipart type 등은 자동직렬화가 안되므로 직렬화 될수 있도록 추가.
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonParser jp = new JsonParser();
            JsonElement je = jp.parse(mapper.writeValueAsString(vo));
            return gson.toJson(je);
        } catch (JsonSyntaxException | JsonProcessingException e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
            throw e;
        }
    }


}
