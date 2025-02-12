package com.localhost.anonymouschat.services.inter;


import com.localhost.anonymouschat.payload.request.AuthenticationRequest;
import com.localhost.anonymouschat.payload.request.RegisterRequest;
import com.localhost.anonymouschat.payload.response.AuthenticationResponse;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest request);
    AuthenticationResponse authenticate(AuthenticationRequest request);
}
