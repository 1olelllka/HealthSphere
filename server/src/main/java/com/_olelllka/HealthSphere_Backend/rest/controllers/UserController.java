package com._olelllka.HealthSphere_Backend.rest.controllers;

import com._olelllka.HealthSphere_Backend.domain.dto.JwtToken;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.RegisterDoctorForm;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.RegisterPatientForm;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.LoginForm;
import com._olelllka.HealthSphere_Backend.domain.entity.UserEntity;
import com._olelllka.HealthSphere_Backend.rest.exceptions.NotAuthorizedException;
import com._olelllka.HealthSphere_Backend.rest.exceptions.ValidationException;
import com._olelllka.HealthSphere_Backend.service.JwtService;
import com._olelllka.HealthSphere_Backend.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path="/api/v1")
public class UserController {

    private UserService userService;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;

    @Autowired
    public UserController(UserService userService,
                          AuthenticationManager authenticationManager,
                          JwtService jwtService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register/patient")
    public ResponseEntity<RegisterPatientForm> registerUser(@RequestBody @Valid RegisterPatientForm registerPatientForm,
                                                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String msg = bindingResult.getAllErrors().stream().map(err -> err.getDefaultMessage()).collect(Collectors.joining(" "));
            throw new ValidationException(msg);
        }
        UserEntity createdUser = userService.register(registerPatientForm);
        return new ResponseEntity<>(
                RegisterPatientForm.builder()
                        .email(createdUser.getEmail())
                        .password(createdUser.getPassword())
                        .firstName(registerPatientForm.getFirstName())
                        .lastName(registerPatientForm.getLastName())
                        .gender(registerPatientForm.getGender())
                        .dateOfBirth(registerPatientForm.getDateOfBirth())
                        .build(), HttpStatus.CREATED
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register/doctor")
    public ResponseEntity<RegisterDoctorForm> registerDoctor(@RequestBody @Valid RegisterDoctorForm registerDoctorForm,
                                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String msg = bindingResult.getAllErrors().stream().map(err -> err.getDefaultMessage()).collect(Collectors.joining(" "));
            throw new ValidationException(msg);
        }
        UserEntity createdUser = userService.doctorRegister(registerDoctorForm);
        return new ResponseEntity<>(
                RegisterDoctorForm.builder()
                        .email(createdUser.getEmail())
                        .password(createdUser.getPassword())
                        .firstName(registerDoctorForm.getFirstName())
                        .lastName(registerDoctorForm.getLastName())
                        .clinicAddress(registerDoctorForm.getClinicAddress())
                        .phoneNumber(registerDoctorForm.getPhoneNumber())
                        .licenseNumber(registerDoctorForm.getLicenseNumber())
                        .build(), HttpStatus.CREATED
        );
    }

    @PostMapping("/login")
    public ResponseEntity<JwtToken> loginUser(@RequestBody @Valid LoginForm loginForm,
                                              BindingResult bindingResult,
                                              HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            String msg = bindingResult.getAllErrors().stream().map(err -> err.getDefaultMessage()).collect(Collectors.joining(" "));
            throw new ValidationException(msg);
        }
        Authentication authorization = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginForm.getEmail(),
                        loginForm.getPassword()
                )
        );
        if (authorization.isAuthenticated()) {
            String jwt = jwtService.generateJwt(loginForm.getEmail());
            ResponseCookie cookie = ResponseCookie.from("accessToken", jwt)
                    .maxAge(60 * 60)
                    .secure(false)
                    .httpOnly(true)
                    .sameSite("Lax")
                    .path("/")
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            return new ResponseEntity<>(JwtToken.builder().accessToken(jwt).build(), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }
    @GetMapping("/get-jwt")
    public ResponseEntity<JwtToken> getJwtToken(HttpServletRequest request) {
        Cookie accessToken = Arrays.stream(request.getCookies())
                .filter(cookie -> "accessToken".equals(cookie.getName()))
                .findFirst().orElseThrow(() -> new NotAuthorizedException("You need to log in to proceed."));
        return new ResponseEntity<>(JwtToken.builder().accessToken(accessToken.getValue()).build(), HttpStatus.OK);
    }
}