package com.localhost.anonymouschat.services.inter;

import com.localhost.anonymouschat.models.User;
import com.localhost.anonymouschat.payload.response.UserDetailResponse;

import java.util.Collection;
import java.util.List;

public interface UserService {
    UserDetailResponse getUserDetail(String userId);
    UserDetailResponse getUserDetail(User user);
    List<UserDetailResponse> getUsersDetails(Collection<String> userIds);
}
