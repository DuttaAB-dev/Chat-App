package com.anisala.chat.tcp;
// Made mostly with the help of AI
import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.time.Instant;
import java.nio.charset.*;
import java.util.Base64;

import com.anisala.chat.tcp.util.CheckDto;

public class Deserialiser {

    public static <T> T deserialize(byte[] payload, Class<T> clazz) {
        try {
            String payloadStr = new String(payload, StandardCharsets.UTF_8);
            
            Constructor<?>[] constructors = clazz.getConstructors();
            if (constructors.length == 0) {
                throw new RuntimeException("No public constructor found for " + clazz.getSimpleName());
            }
            
            Constructor<?> targetConstructor = constructors[0];
            int expectedParamCount = targetConstructor.getParameterCount();

            String[] parts = payloadStr.split("\\|", expectedParamCount);

            if (parts.length != expectedParamCount) {
                throw new RuntimeException("Payload parts (" + parts.length + ") does not match constructor for " + 
                        clazz.getSimpleName() + " (" + expectedParamCount + "). Payload: " + payloadStr);
            }

            Class<?>[] paramTypes = targetConstructor.getParameterTypes();
            Object[] args = new Object[expectedParamCount];

            for (int i = 0; i < expectedParamCount; i++) {
                String decodedPart = "";

                if (!parts[i].isEmpty()) {
                    decodedPart = new String(Base64.getDecoder().decode(parts[i]), StandardCharsets.UTF_8);
                }

                
                args[i] = parseValue(decodedPart, paramTypes[i]);
            }

            // Dynamically instantiate the object! 
            return clazz.cast(targetConstructor.newInstance(args));

        } catch (Exception e) {
            throw new RuntimeException("Deserialisation failed for " + clazz.getSimpleName(), e);
        }
    }

    /**
     * Helper method to convert Strings into the exact type your class constructors need.
     * I noticed your DTOs use String, boolean, and LocalDateTime. 
     */
    private static Object parseValue(String value, Class<?> type) {

        if ((value == null || value.isEmpty()) && type != String.class) return null;
        
        if (CheckDto.isDTO(type)) return deserialize(value.getBytes(StandardCharsets.UTF_8), type);
            
        if (type == String.class) return value;

        if (type == int.class || type == Integer.class) return Integer.parseInt(value);

        if (type == long.class || type == Long.class) return Long.parseLong(value);

        if (type == boolean.class || type == Boolean.class) return Boolean.parseBoolean(value);

        if (type == LocalDateTime.class) return LocalDateTime.parse(value); // Needs ISO-8601 string, e.g. "2023-10-01T15:30"

        if (type == Instant.class) return Instant.parse(value);

        throw new IllegalArgumentException("Deserialiser doesn't know how to parse field type: " + type.getName());
    }
}
