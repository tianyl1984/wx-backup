package com.tianyl.wxbackup.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class Utils {

    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    public static URI getURI(String source) {
        try {
            return new URI(source);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, String> decodeQuery(String uri) {
        QueryStringDecoder decoder = new QueryStringDecoder(uri);
        Map<String, List<String>> map = decoder.parameters();
        Map<String, String> result = new HashMap<>();
        if (map == null) {
            return result;
        }
        for (String key : map.keySet()) {
            List<String> values = map.get(key);
            result.put(key, String.join(",", values));
        }
        return result;
    }

    public static boolean isEmpty(String value) {
        return value == null || value.trim().length() == 0;
    }

    public static boolean isEmpty(Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }

    public static <T> List<List<T>> splitList(List<T> list) {
        int size = 50;
        return splitList(list, size);
    }

    public static <T> List<List<T>> splitList(List<T> list, int size) {
        int count = list.size() / size;
        List<List<T>> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            int from = i * size;
            result.add(list.subList(from, from + size));
        }
        if (list.size() > count * size) {
            result.add(list.subList(count * size, list.size()));
        }
        return result;
    }

    public static String getSnakeCase(String name) {
        String result = "";
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                result += (i == 0 ? "" : "_") + Character.toLowerCase(c);
            } else {
                result += c;
            }
        }
        return result;
    }

    public static String toJSONString(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        //  ???????????? null ???
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("?????????json??????", e);
            throw new RuntimeException(e);
        }
    }

}
