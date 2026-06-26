package com.nehirozsari.smartpantry.dto.request;

import com.nehirozsari.smartpantry.validation.StrongPassword;
import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
        @NotBlank String currentPassword,
        @NotBlank @StrongPassword String newPassword
) {
}
