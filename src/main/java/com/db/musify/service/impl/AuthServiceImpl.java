package com.db.musify.service.impl;

import com.db.musify.dto.request.ForgotPasswordRequest;
import com.db.musify.dto.request.LoginUserRequest;
import com.db.musify.dto.request.RefreshTokenRequest;
import com.db.musify.dto.request.RegisterUserRequest;
import com.db.musify.dto.response.AppUserResponse;
import com.db.musify.dto.response.MessageResponse;
import com.db.musify.entity.AppUser;
import com.db.musify.exception.*;
import com.db.musify.repository.AppUserRepository;
import com.db.musify.service.AuthService;
import com.db.musify.service.EmailService;
import com.db.musify.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AppUserRepository appUserRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public MessageResponse registerUser(RegisterUserRequest request) {
        if (appUserRepository.existsByEmail(request.getEmail())){
            throw new EmailAlreadyExistsException("Email already exists");
        }

        String tempPassword = generateTemporaryPassword();

        AppUser appUser = new AppUser();
        appUser.setName(request.getName());
        appUser.setEmail(request.getEmail());
        appUser.setPassword(passwordEncoder.encode(tempPassword));
        appUser.setRole(request.getRole() != null ? request.getRole() : "USER");

        appUserRepository.save(appUser);
        emailService.sendWelcomeEmail(appUser.getEmail(), appUser.getName(), tempPassword);
        return new MessageResponse("Account created successfully. A temporary password has been sent to your email");
    }

    @Override
    public AppUserResponse loginUser(LoginUserRequest request) {
        AppUser appUser = appUserRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), appUser.getPassword())){
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String accessToken = jwtUtil.generateAccessToken(appUser.getId(), appUser.getName(), appUser.getEmail(), appUser.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(appUser.getId(), appUser.getEmail());

        appUser.setRefreshToken(refreshToken);
        appUserRepository.save(appUser);

        return AppUserResponse.fromEntity(appUser, accessToken, refreshToken);
    }

    @Override
    public AppUserResponse refreshAccessToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        String email = jwtUtil.extractEmail(refreshToken);

        if (!jwtUtil.isRefreshToken(refreshToken)){
            throw new InvalidCredentialsException("Invalid token type");
        }

        AppUser appUser = appUserRepository.findByRefreshToken(refreshToken)
                .orElseThrow(()-> new InvalidTokenException("Invalid refresh token"));

        if (!jwtUtil.validateToken(refreshToken, email)){
            throw new TokenExpiredException("Refresh token expired or invalid");
        }

        String newAccessToken = jwtUtil.generateAccessToken(appUser.getId(), appUser.getName(), appUser.getEmail(), appUser.getRole());

        return AppUserResponse.fromEntity(appUser, newAccessToken, refreshToken);
    }

    @Override
    public MessageResponse forgotPassword(ForgotPasswordRequest request) {
        AppUser appUser = appUserRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

        String tempPassword = generateTemporaryPassword();

        appUser.setPassword(passwordEncoder.encode(tempPassword));
        appUserRepository.save(appUser);
        emailService.sendCredentials(appUser.getEmail(), appUser.getName(), tempPassword);

        return new MessageResponse("Temporary password has been sent to your email");
    }

    private String generateTemporaryPassword(){
        String chars = "AFHDIUHFUNGOIDJFOIJHOIgjoijgsdfmsklgniuerngoe12314nuvgdiufnveibnaspSAW#@";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(10);

        for (int i=0; i<10; i++){
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }
}
