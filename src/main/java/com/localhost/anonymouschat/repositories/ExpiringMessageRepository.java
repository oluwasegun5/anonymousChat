package com.localhost.anonymouschat.repositories;

import com.localhost.anonymouschat.models.ExpiringMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ExpiringMessageRepository extends MongoRepository<ExpiringMessage, String> {
    List<ExpiringMessage> findByChatRoomId(String chatRoomId);
    List<ExpiringMessage> findByExpirationTimeBefore(Instant time);
}
