package com.anisala.chat.tcp.dto;
public class CheckOnlineRequest implements DtoMarker {
    private String userName;
    public CheckOnlineRequest() {}
    public CheckOnlineRequest(String userName) { this.userName = userName; }
    public String getUserName() { return userName; }
}
