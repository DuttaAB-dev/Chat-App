package com.anisala.chat.tcp;

//Made with help from AI

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.StringJoiner;

import com.anisala.chat.tcp.util.CheckDto;

public class Serialiser {

    public static byte[] serialize(Object obj) {
        try {
            Field[] fields = obj.getClass().getDeclaredFields();
            StringJoiner joiner = new StringJoiner("|");

            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(obj);

                if (value == null) {
                    joiner.add("");
                } else {
                    // Turn value into a string, then safeproof it with Base64!
                    String strValue;
                    if (!CheckDto.isDTO(field)) {
                        strValue = value.toString();
                    } else {

                        byte[] serialized = serialize(value);
                        strValue = new String(serialized, StandardCharsets.UTF_8);
                    }
                    String encoded = Base64.getEncoder().encodeToString(strValue.getBytes(StandardCharsets.UTF_8));
                    joiner.add(encoded);
                }
            }

            return joiner.toString().getBytes(StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new RuntimeException("Serialization failed for " + obj.getClass().getSimpleName(), e);
        }
    }
}
