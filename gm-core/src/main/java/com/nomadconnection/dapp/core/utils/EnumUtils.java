package com.nomadconnection.dapp.core.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnumUtils {

    public static <T extends Enum<?>> List<T> getList(Class<T> clazz) {
        return Arrays.asList(clazz.getEnumConstants());
    }

    public static <T extends Enum<?>> Map<Object, T> getMap(Class<T> clazz) {
        List<T> list = getList(clazz);
        Map<Object, T> map = new HashMap<>();
        for(T t : list) {
            Object key = (t instanceof EnumValue) ? ((EnumValue<?>) t).getValue() : t.toString();
            map.put(key, t);
        }
        return map;
    }

}
