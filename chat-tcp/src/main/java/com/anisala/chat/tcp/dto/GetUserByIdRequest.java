package com.anisala.chat.tcp.dto;
public class GetUserByIdRequest implements DtoMarker {
    private String userId;
    public GetUserByIdRequest() {}
    public GetUserByIdRequest(String userId) { this.userId = userId; }
    public String getUserId() { return userId; }
}
