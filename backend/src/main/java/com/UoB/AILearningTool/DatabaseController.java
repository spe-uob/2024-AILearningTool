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

    public UserEntity getUser(String userID) {
        return userRepository.findById(userID).orElse(null);
    }

    public String addUser(String username, String password, boolean optionalConsent) {
        if (userRepository.findByUsername(username).isPresent()) {
            return null;
        }
        UserEntity user = new UserEntity(username, password, optionalConsent);
        UserEntity savedUser = userRepository.save(user);
        if (savedUser != null && savedUser.getId() != null) {
            return savedUser.getId();
        } else {
            return null; 
        }
    }

    public boolean removeUser(String id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public String createChat(UserEntity user, String initialMessage) {
        ChatEntity chat = new ChatEntity(user, initialMessage);
        chatRepository.save(chat);
        return chat.getChatID();
    }

    public ChatEntity getChat(UserEntity user, String chatID) {
        Optional<ChatEntity> chat = chatRepository.findById(chatID);
        return chat.orElse(null);
    }

    public Boolean deleteChat(UserEntity user, String chatID) {
        Optional<ChatEntity> chat = chatRepository.findById(chatID);
        if (chat.isPresent() && chat.get().getOwner().equals(user)) {
            chatRepository.deleteById(chatID);
            return true;
        }
        return false;
    }
}
