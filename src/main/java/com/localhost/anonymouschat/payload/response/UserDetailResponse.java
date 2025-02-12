package com.localhost.anonymouschat.payload.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserDetailResponse {
    private String id;
    private String firstname;
    private String lastname;
    private String email;
    private String username;
}
