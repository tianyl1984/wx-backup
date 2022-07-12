package com.tianyl.wxbackup;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static String toJSONString(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        //  不序列化 null 值
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("序列化json出错", e);
            throw new RuntimeException(e);
        }
    }

}
