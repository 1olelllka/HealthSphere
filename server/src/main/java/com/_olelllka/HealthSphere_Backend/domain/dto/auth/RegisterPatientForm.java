package com._olelllka.HealthSphere_Backend.domain.dto.auth;

import com._olelllka.HealthSphere_Backend.domain.entity.Gender;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterPatientForm {
    @Email(message = "Email must be a well-formed email address.")
    private String email;
    @Pattern(regexp = "^(?=.*\\d).{8,}$", message = "Your password should contain at least 8 characters including at least 1 number.")
    private String password;
    @NotBlank(message = "Your first name must not be empty.")
    private String firstName;
    @NotBlank(message = "Your last name must not be empty.")
    private String lastName;
    @Past(message = "Your date of birth must be in the past.")
    private Date dateOfBirth;
    private Gender gender;
}
