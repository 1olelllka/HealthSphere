package com._olelllka.HealthSphere_Backend.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginForm {
    @NotBlank(message = "Email must not be empty.")
    private String email;
    @NotBlank(message = "Password must not be empty.")
    private String password;
}
