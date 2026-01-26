package com.db.musify.service;

import com.db.musify.dto.request.AppUserRequest;
import com.db.musify.dto.response.AppUserResponse;
import com.db.musify.dto.response.PaginatedResponse;

public interface AppUserService {
    AppUserResponse getUserProfile(String email);

    AppUserResponse updateUserProfile(AppUserRequest request, String email);

    PaginatedResponse<AppUserResponse> getAllUser(int page, int size);

    AppUserResponse updateUserRole(Long userId, String role, String email);
}
