package com.jimmy.swiftwheels.controller;

import com.jimmy.swiftwheels.service.AuthenticationService;
import com.jimmy.swiftwheels.util.AuthenticationRequest;
import com.jimmy.swiftwheels.util.AuthenticationResponse;
import com.jimmy.swiftwheels.util.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        System.out.println("request: " + request);
        return authService.register(request);
    }
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return authService.authenticate(request);
    }
}