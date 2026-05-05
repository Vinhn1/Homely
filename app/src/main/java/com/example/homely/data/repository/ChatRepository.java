package com.example.homely.data.repository;

import com.example.homely.data.model.*;
import com.example.homely.data.remote.firebase.firestore.*;

import java.util.*;

/**
 * Repository cho chat realtime.
 */

public class ChatRepository {
    private final FirestoreChatSource chatSource;

    public ChatRepository(FirestoreChatSource chatSource) {
        this.chatSource = chatSource;
    }

    /**
     * Tạo mới hoặc lấy chat đã tồn tại giữa 2 người dùng.
     * @param participants Danh sách UID (2 phần tử)
     * @param roomId       ID phòng liên quan
     * @return LiveData<Resource<Chat>>
     */
    public LiveData<Resource<Chat>> createOrGetChat(List<String> participants, String roomId) {
        // TODO: implement
        return null;
    }

    /**
     * Gửi tin nhắn, cập nhật lastMessage và timestamp.
     * @param chatId  ID chat
     * @param message Đối tượng Message
     * @return LiveData<Resource<Void>>
     */
    public LiveData<Resource<Void>> sendMessage(String chatId, Message message) {
        // TODO: implement
        return null;
    }

    /**
     * Lắng nghe tin nhắn realtime trong một chat.
     * @param chatId ID chat
     * @return LiveData<Resource<List<Message>>> (tự cập nhật)
     */
    public LiveData<Resource<List<Message>>> getMessages(String chatId) {
        // TODO: implement
        return null;
    }

    /**
     * Lấy danh sách các cuộc trò chuyện của người dùng.
     * @param userId UID người dùng
     * @return LiveData<Resource<List<Chat>>> (sắp xếp theo lastMessageTime)
     */
    public LiveData<Resource<List<Chat>>> getChatList(String userId) {
        // TODO: implement
        return null;
    }
}
