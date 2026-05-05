package com.example.homely.ui.chat.viewmodel;

import com.example.homely.data.model.*;
import com.example.homely.data.repository.*;

import java.util.*;

/**
 * ViewModel cho chat.
 */

public class ChatViewModel {
    private final ChatRepository repository;

    public ChatViewModel(ChatRepository repository) {
        this.repository = repository;
    }

    public void loadChatList() {
        // TODO: get current user id and call repository.getChatList
    }

    public LiveData<Resource<List<Chat>>> getChatList() {
        return null;
    }

    public void sendMessage(String chatId, String text) {
        // TODO: create Message with senderId = current user, then call repository.sendMessage
    }

    public LiveData<Resource<List<Message>>> getMessages(String chatId) {
        // TODO: return repository.getMessages
        return null;
    }
}
