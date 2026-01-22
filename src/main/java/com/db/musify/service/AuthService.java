package com.db.musify.service;

import com.db.musify.dto.request.ForgotPasswordRequest;
import com.db.musify.dto.request.LoginUserRequest;
import com.db.musify.dto.request.RefreshTokenRequest;
import com.db.musify.dto.request.RegisterUserRequest;
import com.db.musify.dto.response.AppUserResponse;
import com.db.musify.dto.response.MessageResponse;
import jakarta.validation.Valid;

public interface AuthService {

    MessageResponse registerUser( RegisterUserRequest request);

    AppUserResponse loginUser(LoginUserRequest request);

    AppUserResponse refreshAccessToken(RefreshTokenRequest request);

    MessageResponse forgotPassword(ForgotPasswordRequest request);
}
