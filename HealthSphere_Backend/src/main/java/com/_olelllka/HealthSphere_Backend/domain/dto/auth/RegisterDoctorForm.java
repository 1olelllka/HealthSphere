package com._olelllka.HealthSphere_Backend.domain.dto.auth;

import com._olelllka.HealthSphere_Backend.domain.dto.doctors.SpecializationDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterDoctorForm {
    @Email(message = "Email must be a well-formed email address.")
    private String email;
    @Pattern(regexp = "^(?=.*\\d).{8,}$", message = "Your password should contain at least 8 characters including at least 1 number.")
    private String password;
    @NotBlank(message = "Your first name must not be empty.")
    private String firstName;
    @NotBlank(message = "Your last name must not be empty.")
    private String lastName;
    @NotBlank(message = "Your license number must not be empty.")
    private String licenseNumber;
    @Pattern(regexp = "^\\d{8,}$", message = "Your phone number must not be empty.")
    private String phoneNumber;
    @NotBlank(message = "Your clinic address must not be empty.")
    private String clinicAddress;
    private List<SpecializationDto> specializations;

}
