package com.anisala.chat.client.presenter;

import com.anisala.chat.client.model.User;
import com.anisala.chat.client.service.ChatService;
import com.anisala.chat.client.service.UserService;
import com.anisala.chat.client.view.ChatView;

public class ChatPresenter {
    private final ChatView view;
    private final ChatService chatService;
    private final UserService userService;
    
    // State moves here, away from the UI
    private User currentUser;
    private String activeRecipient;
    private String pendingFileSender;
    // ... other state variables ...
    private boolean isChatting;

    public ChatPresenter(ChatView view, ChatService chatService, UserService userService) {
        this.view = view;
        this.chatService = chatService;
        this.userService = userService;
    }

    public void start() {
        while (true) {
            authenticate();
            chatLoop();
        }
    }

    private void chatLoop() {
        view.showSystemMessage("\n--- Chat Started ---");
        view.showSystemMessage("Commands: /chat <user>, /sendfile <path>, /accept, /reject, /logout, /quit\n");
        chatService.startMessageListener(currentUser.getUserName(), this::handleIncomingMessage);
        isChatting = true;
        
        while (isChatting) {
            view.showPrompt();
            String input = view.getUserInput();
            if (input == null || input.isEmpty()) continue;
            
            if (input.startsWith("/"))
                handleCommand(input);
            else
                if (activeRecipient == null)
                    view.showSystemMessage("You aren't chatting with anyone. Use '/chat <username>' first.");
                else
                    chatService.sendMessage(activeRecipient, input);
        }
        
    }

    private void authenticate() {
        currentUser = null;
        activeRecipient = null;

        while (currentUser == null) {
            view.showText("Enter your username: \n");
            String userName = view.getUserInput();
            if (userName == null || userName.isEmpty()) continue;

            User user = userService.logIn(userName); // FIX: logIn directly instead of getUser, as server doesn't implement getUser
            
            if (user != null && !user.getUserName().isEmpty()) {
                view.showText("Welcome back, " + user.getName() + "!\n");
                currentUser = user;
            } else {
                view.showText("Username '" + userName + "' not found.\n");
                view.showText("Do you want to create a new user? (y/n): ");
                if (view.getUserInput().equalsIgnoreCase("y")) {
                    view.showText("Enter your full name: ");
                    String fullName = view.getUserInput();
                    User newUser = new User(userName, fullName);
                    
                    if (userService.createUser(newUser)){
                        view.showText("User created successfully!\n");
                        currentUser = userService.logIn(userName);
                    } 
                    else 
                        view.showText("Failed to create user.\n");
                }
            }
        }
    }

    private void handleCommand(String input) {
        String[] parts = input.split("\\s+", 2);
        String command = parts[0].toLowerCase();

        switch (command) {
            case "/chat":
                if (parts.length > 1) {
                    String targetUser = parts[1];
                    view.showSystemMessage("Checking if " + targetUser + " is online...");
                    
                    if (chatService.isOnline(targetUser)) {
                        activeRecipient = targetUser;
                        view.showSystemMessage("You are now chatting with " + activeRecipient);
                    } else {
                        view.showSystemMessage("Cannot chat: User '" + targetUser + "' is offline.");
                    }
                } else {
                    view.showSystemMessage("Usage: /chat <username>");
                }
                break;
                
            case "/sendfile":
                // if (activeRecipient == null) {
                //     System.out.println("[System] Please select a user to chat with first using '/chat <username>'");
                // } else if (parts.length > 1) {
                //     File file = new File(parts[1]);
                //     if (!file.exists()) {
                //         System.out.println("[System] File not found: " + file.getAbsolutePath());
                //         break;
                //     }
                //     pendingUploadPath = file.getAbsolutePath();
                //     pendingUploadReceiver = activeRecipient;
                //     
                //     System.out.println("[System] Asking " + activeRecipient + " for permission to send " + file.getName() + "...");
                //     chatManager.sendMessage(currentUser.getUserName(), activeRecipient, "[FILE_REQ] " + file.getName());
                // } else {
                //     System.out.println("[System] Usage: /sendfile <path>");
                // }
                break;
                
            case "/accept":
                // if (pendingFileSender != null) {
                //     System.out.println("[System] Accepted file from " + pendingFileSender + ". Waiting for them to upload...");
                //     chatManager.sendMessage(currentUser.getUserName(), pendingFileSender, "[FILE_ACCEPT]");
                // } else {
                //     System.out.println("[System] No pending file requests.");
                // }
                break;

            case "/reject":
                // if (pendingFileSender != null) {
                //     System.out.println("[System] Rejected file from " + pendingFileSender + ".");
                //     chatManager.sendMessage(currentUser.getUserName(), pendingFileSender, "[FILE_REJECT]");
                //     pendingFileSender = null;
                //     pendingFileName = null;
                // } else {
                //     System.out.println("[System] No pending file requests.");
                // }
                break;

            case "/logout":
                System.out.println("Logging out...");
                userService.logOut(currentUser.getUserId());
                chatService.stopMessageListener();
                isChatting = false;
                break;
                
            case "/quit":
                System.out.println("Exiting...");
                userService.logOut(currentUser.getUserId());
                chatService.stopMessageListener();
                System.exit(0);
                break;
                
            default:
                System.out.println("[System] Unknown command.");
        }
    }

    private void handleIncomingMessage(String sender, String message) {
        if (message.equals("[PING]")) {
            return;
        }

        if (sender.equals("System") && message.startsWith("[ERROR]")) {
            view.showSystemMessage(message);
            // If the user we were trying to chat with is offline, maybe clear activeRecipient
            return;
        }

        // --- INTERCEPT CONTROL MESSAGES ---
        if (message.startsWith("[FILE_REQ] ")) {
            // String fileName = message.substring(11);
            // pendingFileSender = sender;
            // pendingFileName = fileName;
            // view.showFileRequest(sender, fileName);
            return;
        }

        else if (message.equals("[FILE_ACCEPT]")) {
            // if (sender.equals(pendingUploadReceiver) && pendingUploadPath != null) {
            //     view.showSystemMessage(sender + " accepted! Uploading to server...");
            //     
            //     new Thread(() -> {
            //         String transferId = chatService.uploadFile(pendingUploadPath);
            //         if (transferId != null) {
            //             chatService.sendMessage(sender, "[FILE_READY] " + transferId);
            //             view.showSystemMessage("Upload complete! Sent to " + sender);
            //         } else {
            //             view.showSystemMessage("Upload failed.");
            //         }
            //         pendingUploadPath = null;
            //         pendingUploadReceiver = null;
            //     }).start();
            // }
            return;
        }

        else if (message.equals("[FILE_REJECT]")) {
            // if (sender.equals(pendingUploadReceiver)) {
            //     view.showSystemMessage(sender + " rejected your file transfer.");
            //     pendingUploadPath = null;
            //     pendingUploadReceiver = null;
            // }
            return;
        }

        else if (message.startsWith("[FILE_READY] ")) {
            // if (sender.equals(pendingFileSender)) {
            //     String transferId = message.substring(13);
            //     view.showSystemMessage("File is ready! Downloading from server...");
            //     
            //     new Thread(() -> {
            //         boolean success = chatService.downloadFile(transferId, pendingFileName);
            //         if (success) {
            //             view.showFileTransferComplete(pendingFileName);
            //         } else {
            //             view.showSystemMessage("Failed to download file.");
            //         }
            //         pendingFileSender = null;
            //         pendingFileName = null;
            //     }).start();
            // }
            return;
        }

        // --- STANDARD TEXT MESSAGES ---
        view.showIncomingMessage(sender, message);
       }
}
