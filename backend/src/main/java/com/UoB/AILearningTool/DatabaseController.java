package com.UoB.AILearningTool;

import com.UoB.AILearningTool.model.ChatEntity;
import com.UoB.AILearningTool.model.UserEntity;
import com.UoB.AILearningTool.repository.ChatRepository;
import com.UoB.AILearningTool.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DatabaseController {
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;

    @Autowired
    public DatabaseController(UserRepository userRepository, ChatRepository chatRepository) {
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
    }

    public UserEntity getUser(String username) {
        return userRepository.findById(username).orElse(null);
    }

    // Create a new account
    public boolean addUser(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            return false;
        }

        UserEntity user = new UserEntity(username, password);
        userRepository.save(user);
        return true;
    }

    // Delete existing user account
    public boolean removeUser(String sessionID) {
        Optional<UserEntity> user = userRepository.findBySessionID(sessionID);

        // TODO: Delete chats as well

        if (user.isPresent()) {
            userRepository.delete(user.get());
            return true;
        } else {
            return false;
        }
    }

    // Creates a new chat
    public String createChat(UserEntity user, String initialMessage, String threadID) {
        String id = StringTools.RandomString(20);
        ChatEntity chat = new ChatEntity(user, initialMessage, threadID);
        chatRepository.save(chat);
        return chat.getChatID();
    }


    // Deletes an existing chat
    public boolean deleteChat(UserEntity user, String chatID) {
        Optional<ChatEntity> chatOpt = chatRepository.findById(chatID);
        if (chatOpt.isPresent() && chatOpt.get().getOwner().getUsername().equals(user.getUsername())) {
            chatRepository.deleteById(chatID);
            return true;
        }
        return false;
    }

    public ChatEntity createChat(String sessionID, String initialMessage) {
        Optional<UserEntity> userOpt = userRepository.findBySessionID(sessionID);
        if (userOpt.isEmpty()) {
            return null;
        }

        UserEntity user = userOpt.get();
        ChatEntity chat = new ChatEntity(user, initialMessage, sessionID);
        chatRepository.save(chat);

        return chat;
    }

    public ChatEntity getChat(String username, String chatID) {
        Optional<UserEntity> userOpt = userRepository.findById(username);
        if (userOpt.isEmpty()) {
            return null;
        }

        Optional<ChatEntity> chatOpt = chatRepository.findById(chatID);
        return chatOpt.filter(chat -> chat.getOwner().getUsername().equals(username)).orElse(null);
    }

    public boolean deleteChat(String username, String chatID) {
        Optional<ChatEntity> chatOpt = chatRepository.findById(chatID);
        if (chatOpt.isPresent() && chatOpt.get().getOwner().getUsername().equals(username)) {
            chatRepository.deleteById(chatID);
            return true;
        }
        return false;
    }
}