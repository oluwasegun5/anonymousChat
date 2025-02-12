package com.localhost.anonymouschat.repositories;

import com.localhost.anonymouschat.models.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    Collection<ChatRoom> findByParticipantIdsContains(String id);
}
