package com.anisala.chat.tcp.util;

import java.lang.reflect.Field;
// import java.util.List;

import com.anisala.chat.tcp.dto.*;

public class CheckDto {

    // private final static List<Class<?>> DTO_CLASSES;

    // static {
    //     DTO_CLASSES = List.of(
    //         ChatMessage.class,
    //         CreateUserResponse.class,
    //         DownloadRequest.class,
    //         FileChunk.class,
    //         GetUserRequest.class,
    //         LogInRequest.class,
    //         LogInResponse.class,
    //         LogOutRequest.class,
    //         LogOutResponse.class,
    //         SendMessageRequest.class,
    //         SendMessageResponse.class,
    //         UploadResponse.class,
    //         UserObj.class
    //     );
    // }


    public static boolean isDTO(Field field) {
        if (field == null) {
            return false;
        }
        return DtoMarker.class.isAssignableFrom(field.getType());
    }

    public static boolean isDTO(Class<?> clazz) {
        return DtoMarker.class.isAssignableFrom(clazz);
    }
}