package com.UoB.AILearningTool;

import com.UoB.AILearningTool.model.ChatEntity;
import com.UoB.AILearningTool.model.UserEntity;
import com.UoB.AILearningTool.repository.ChatRepository;
import com.UoB.AILearningTool.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

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

    public String addUser(String username, String password, boolean optionalConsent) {
        UserEntity user = new UserEntity(username, password, optionalConsent);
        userRepository.save(user);
        return username;
    }

    public boolean removeUser(String username) {
        if (userRepository.existsById(username)) {
            userRepository.deleteById(username);
            return true;
        }
        return false;
    }


    public String createChat(String username, String initialMessage) {
        UserEntity user = userRepository.findById(username).orElse(null);
        if (user == null) {
            return null;
        }

        ChatEntity chat = new ChatEntity(user, initialMessage);
        chatRepository.save(chat);

        return chat.getChatID();
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
