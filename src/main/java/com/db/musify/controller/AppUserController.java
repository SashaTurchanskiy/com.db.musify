package com.db.musify.controller;

import com.db.musify.dto.request.AppUserRequest;
import com.db.musify.dto.response.AppUserResponse;
import com.db.musify.service.AppUserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appUser")
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService appUserService;

    @GetMapping("/getUserProfile")
    public ResponseEntity<AppUserResponse> getUserProfile(Authentication authentication){
        String email = authentication.getName();
        AppUserResponse response = appUserService.getUserProfile(email);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/updateUserProfile")
    public ResponseEntity<AppUserResponse> updateUser(@Valid @RequestBody AppUserRequest request, Authentication authentication){
        String email = authentication.getName();
        AppUserResponse response = appUserService.updateUserProfile(request, email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAllUser")
    public ResponseEntity<?> getAllUser(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        Authentication authentication){

        String email = authentication.getName();
        return ResponseEntity.ok(appUserService.getAllUser(page, size));
    }

    @PatchMapping("/updateUserRole/{userId}")
    public ResponseEntity<AppUserResponse> updateUserRole(@PathVariable Long userId,
                                                          @RequestParam @NotBlank(message = "Role is required") @Pattern(regexp = "^(USER|ADMIN)$",
                                                          message = "Role must be either USER or ADMIN") String role,
                                                          Authentication authentication){

        String email = authentication.getName();

        AppUserResponse response = appUserService.updateUserRole(userId, role, email);
        return ResponseEntity.ok(response);
    }
}
