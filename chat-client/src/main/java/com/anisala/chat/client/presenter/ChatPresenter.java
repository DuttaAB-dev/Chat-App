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
    private String activeRecipientId;
    private java.util.Map<String, String> idToNameMap = new java.util.HashMap<>();
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
        chatService.startMessageListener(currentUser.getUserId(), this::handleIncomingMessage);
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
                    chatService.sendMessage(activeRecipientId, input);
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
                    
                    User userObj = userService.getUser(targetUser);
                    if (userObj != null && userService.isOnline(targetUser)) {
                        activeRecipientId = userObj.getUserId();
                        idToNameMap.put(activeRecipientId, targetUser);
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

    private void handleIncomingMessage(String senderId, String message) {
        if (message.equals("[PING]")) {
            return;
        }
        if (senderId.equals("System") && message.startsWith("[ERROR]")) {
            view.showSystemMessage(message);
            return;
        }

        String displaySender = idToNameMap.get(senderId);
        if (displaySender == null && !senderId.equals("System")) {
            new Thread(() -> {
                User senderUser = userService.getUserById(senderId);
                String name = senderUser != null ? senderUser.getUserName() : senderId;
                idToNameMap.put(senderId, name);
                view.showIncomingMessage(name, message);
            }).start();
            return;
        } else if (senderId.equals("System")) {
            displaySender = "System";
        }
        view.showIncomingMessage(displaySender, message);
    }
}
