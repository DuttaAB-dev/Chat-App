package com.anisala.chat.tcp.dto;
public class CheckOnlineResponse implements DtoMarker {
    private boolean isOnline;
    private UserObj user;
    public CheckOnlineResponse() {}
    public CheckOnlineResponse(boolean isOnline, UserObj user) { this.isOnline = isOnline; this.user = user; }
    public boolean getIsOnline() { return isOnline; }
    public UserObj getUser() { return user; }
}
