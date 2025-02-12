package com.localhost.anonymouschat.services.impl;

import com.localhost.anonymouschat.models.User;
import com.localhost.anonymouschat.payload.response.UserDetailResponse;
import com.localhost.anonymouschat.repositories.UserRepository;
import com.localhost.anonymouschat.services.inter.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDetailResponse getUserDetail(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        log.info("user name: {}", user.getUsername());
        return getUserDetailResponse(user);
    }

    @Override
    public UserDetailResponse getUserDetail(User user) {
        return getUserDetailResponse(user);
    }

    private static UserDetailResponse getUserDetailResponse(User user) {
        log.info("userName: {}", user.getUsername());
        return UserDetailResponse.builder()
                .id(user.getId())
                .username(user.getDisplayName())
                .email(user.getEmail())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .build();
    }

    @Override
    public List<UserDetailResponse> getUsersDetails(Collection<String> userIds) {
        List<User> users = userRepository.findAllById(userIds);
        return users.stream()
                .map(this::getUserDetail)
                .collect(Collectors.toList());
    }
}
