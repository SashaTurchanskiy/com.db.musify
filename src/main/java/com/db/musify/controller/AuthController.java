package com.db.musify.controller;

import com.db.musify.dto.request.ForgotPasswordRequest;
import com.db.musify.dto.request.LoginUserRequest;
import com.db.musify.dto.request.RefreshTokenRequest;
import com.db.musify.dto.request.RegisterUserRequest;
import com.db.musify.dto.response.AppUserResponse;
import com.db.musify.dto.response.MessageResponse;
import com.db.musify.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody RegisterUserRequest request){
        MessageResponse response = authService.registerUser(request);
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }
    @PostMapping("/login")
    public ResponseEntity<AppUserResponse> loginUser(@Valid @RequestBody LoginUserRequest request){
        AppUserResponse response = authService.loginUser(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refreshAccessToken")
    public ResponseEntity<AppUserResponse> refreshAccessToken(@Valid @RequestBody RefreshTokenRequest request){
        AppUserResponse response = authService.refreshAccessToken(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request){
        MessageResponse response = authService.forgotPassword(request);
        return ResponseEntity.ok(response);
    }

}
