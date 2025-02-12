package com.localhost.anonymouschat.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class RefreshToken {

    @Id
    private String id;

    private User user;

    private String token;

    private Instant expiryDate;

    public boolean revoked;

}
