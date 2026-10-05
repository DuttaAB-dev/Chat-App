package com.anisala.chat.client.view;

import java.io.File;
import java.util.Scanner;

public class TerminalUi implements ChatView{

    
    private final Scanner sc = new Scanner(System.in);

    @Override

    public void showText(String text){
        System.out.print(text);
    }
    public void showSystemMessage(String message){
         System.out.println("[System] " + message);
    }
    
    @Override
    public void showIncomingMessage(String sender, String message){
         System.out.print("\r[" + sender + "]: " + message + "\n> ");
    }
    
    @Override
    public void showFileRequest(String sender, String fileName){
        System.out.print("\r\n[System] " + sender + " wants to send you a file: '" + fileName + "'\n");
        System.out.print("[System] Type '/accept' to receive or '/reject' to decline.\n> ");
    }
    
    @Override
    public void showFileTransferProgress(String fileName, int progress){
        progress = Math.max(0, Math.min(100, progress));
        int barLength = 50;
        int filledLength = (int) (barLength * (progress / 100.0));

        StringBuilder progressBar = new StringBuilder();
        for (int i = 0; i < barLength; i++) {
            if (i < filledLength) {
                progressBar.append("█");
            } else {
                progressBar.append("░");
            }

        }
        System.out.print("\r[System] Transferring " + fileName + " " + progressBar + " " + progress + "%");
    }
    
    @Override
    public void showFileTransferComplete(String fileName){
        System.out.print("\r[System] Transfer complete: " + fileName + "\n");
    }
    
    @Override
    public void showPrompt(){
        System.out.print(" > ");
    }
    
    @Override
    public String getUserInput(){
         return sc.nextLine().trim();
    }
    
    @Override
    public void promptForUsername(){
        System.out.print("Enter your username: ");
    }
    
    @Override
    public void showWelcomeMessage(String username, String serverPort, String serverHost, String protocol){
        System.out.println("=== Welcome to the Chat App ===");
        System.out.println("Server: " + serverHost + ":" + serverPort);
        System.out.println("Protocol: " + protocol);
        System.out.println("Welcome, " + username + "!");
    }
    
    @Override
    public void promptForNewUser(){
        
    }
    

}
