package com.brainy.model.dto;

import com.brainy.model.entity.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdatePasswordDto(
    @NotBlank(message = "missing")
    @Size(min = 8, message = "must be at least 8 characters")
    @Pattern(regexp = User.userPasswordValidationRegExpr, message =
        "must contain one small letter, capital letter and a number")
    String newPassword
) {
}
