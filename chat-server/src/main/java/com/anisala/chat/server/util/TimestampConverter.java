package com.anisala.chat.server.util;

import java.time.Instant;
import com.google.protobuf.Timestamp;

public class TimestampConverter {

    public static Timestamp toProtoTimestamp(Instant instant) {
        return Timestamp.newBuilder()
            .setSeconds(instant.getEpochSecond())
            .setNanos(instant.getNano())
            .build();
    }
    
    public static Instant fromProtoTimestamp(Timestamp timestamp) {
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }
}