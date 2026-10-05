package com.anisala.chat.client.view;

public interface ChatView {


    void showText(String text);
    void showSystemMessage(String message);
    void showIncomingMessage(String sender, String message);
    void showFileRequest(String sender, String fileName);
    void showFileTransferProgress(String fileName, int progress);
    void showFileTransferComplete(String fileName);
    void showPrompt();
    String getUserInput();

    void promptForUsername();
    void showWelcomeMessage(String username, String serverPort, String serverHost, String protocol);
    void promptForNewUser();
    
}
