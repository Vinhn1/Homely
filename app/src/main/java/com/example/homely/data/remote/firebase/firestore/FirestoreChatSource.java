package com.example.homely.data.remote.firebase.firestore;

import com.example.homely.data.model.*;
import com.google.android.gms.tasks.*;
import com.google.firebase.firestore.*;

import java.util.*;

/**
 * Nguồn dữ liệu Firestore cho chat.
 */

public class FirestoreChatSource {
    private FirebaseFirestore firestore;

    public FirestoreChatSource(){
        firestore = FirebaseFirestore.getInstance();
    }

    /**
     * Tạo mới một cuộc trò chuyện.
     * @param chat Đối tượng Chat
     * @return Task<Void>
     */
    public Task<Void> createChat(Chat chat) {
        // TODO: implement
        return null;
    }

    /**
     * Tìm kiếm chat theo danh sách participants (2 người).
     * @param participants Danh sách UID (2 phần tử)
     * @return Task<QuerySnapshot>
     */
    public Task<QuerySnapshot> findChatByParticipants(List<String> participants) {
        // TODO: implement
        return null;
    }

    /**
     * Lấy danh sách chat của một người dùng.
     * @param userId UID người dùng
     * @return Task<QuerySnapshot> sắp xếp theo lastMessageTime giảm dần
     */
    public Task<QuerySnapshot> getChatsForUser(String userId) {
        // TODO: implement
        return null;
    }

    /**
     * Gửi tin nhắn vào subcollection messages của một chat.
     * @param chatId  ID của chat
     * @param message Đối tượng Message
     * @return Task<Void>
     */
    public Task<Void> sendMessage(String chatId, Message message) {
        // TODO: implement
        return null;
    }

    /**
     * Lấy tin nhắn của một chat (realtime thông qua snapshot listener, nhưng phương thức này trả về Task để lấy một lần).
     * Thực tế Repository sẽ dùng addSnapshotListener, ở đây chỉ định nghĩa Task.
     * @param chatId ID chat
     * @return Task<QuerySnapshot>
     */
    public Task<QuerySnapshot> getMessages(String chatId) {
        // TODO: implement
        return null;
    }

    /**
     * Cập nhật lastMessage và lastMessageTime cho chat.
     * @param chatId       ID chat
     * @param lastMessage  Nội dung tin nhắn cuối
     * @param lastMessageTime Thời gian
     * @return Task<Void>
     */
    public Task<Void> updateLastMessage(String chatId, String lastMessage, Object lastMessageTime) {
        // TODO: implement
        return null;
    }
}
