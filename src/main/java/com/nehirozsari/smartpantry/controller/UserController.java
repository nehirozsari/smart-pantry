package com.nehirozsari.smartpantry.controller;

import com.nehirozsari.smartpantry.dto.request.UpdateProfileRequest;
import com.nehirozsari.smartpantry.dto.response.UserResponse;
import com.nehirozsari.smartpantry.security.UserPrincipal;
import com.nehirozsari.smartpantry.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Authenticated user profile management")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get the authenticated user's profile")
    public UserResponse getProfile(@AuthenticationPrincipal UserPrincipal principal) {
        return userService.getProfile(principal.getId());
    }

    @PutMapping("/me")
    @Operation(summary = "Update the authenticated user's profile")
    public UserResponse updateProfile(@AuthenticationPrincipal UserPrincipal principal,
                                      @Valid @RequestBody UpdateProfileRequest request) {
        return userService.updateProfile(principal.getId(), request);
    }
}
