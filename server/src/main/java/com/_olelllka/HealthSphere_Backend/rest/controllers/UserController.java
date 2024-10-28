package com._olelllka.HealthSphere_Backend.rest.controllers;

import com._olelllka.HealthSphere_Backend.domain.dto.ErrorMessage;
import com._olelllka.HealthSphere_Backend.domain.dto.JwtToken;
import com._olelllka.HealthSphere_Backend.domain.dto.appointments.AppointmentDto;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.RegisterDoctorForm;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.RegisterPatientForm;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.LoginForm;
import com._olelllka.HealthSphere_Backend.domain.dto.doctors.DoctorDetailDto;
import com._olelllka.HealthSphere_Backend.domain.dto.patients.PatientDto;
import com._olelllka.HealthSphere_Backend.domain.entity.DoctorEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.PatientEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.UserEntity;
import com._olelllka.HealthSphere_Backend.mapper.impl.DoctorDetailMapper;
import com._olelllka.HealthSphere_Backend.mapper.impl.PatientMapper;
import com._olelllka.HealthSphere_Backend.rest.exceptions.AccessDeniedException;
import com._olelllka.HealthSphere_Backend.rest.exceptions.NotAuthorizedException;
import com._olelllka.HealthSphere_Backend.rest.exceptions.ValidationException;
import com._olelllka.HealthSphere_Backend.service.DoctorService;
import com._olelllka.HealthSphere_Backend.service.JwtService;
import com._olelllka.HealthSphere_Backend.service.PatientService;
import com._olelllka.HealthSphere_Backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletException;
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
@Tag(name="Users", description = "Login, register, get, update, delete and logout users. Applicable for both doctors and patients.")
public class UserController {

    private UserService userService;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;
    private DoctorService doctorService;
    private PatientService patientService;
    private PatientMapper patientMapper;
    private DoctorDetailMapper doctorMapper;

    @Autowired
    public UserController(UserService userService,
                          DoctorService doctorService,
                          PatientService patientService,
                          AuthenticationManager authenticationManager,
                          PatientMapper patientMapper,
                          DoctorDetailMapper doctorMapper,
                          JwtService jwtService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.doctorService = doctorService;
        this.patientService = patientService;
        this.patientMapper = patientMapper;
        this.doctorMapper = doctorMapper;
    }

    @Operation(summary = "Create an appointment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegisterPatientForm.class)
                    )}
            ),
            @ApiResponse(responseCode = "403", description = "Access Denied",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "400", description = "Validation Errors",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "409", description = "Patient or Doctor with such email already exists",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))})
    })
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

    @Operation(summary = "Create an appointment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegisterDoctorForm.class)
                    )}
            ),
            @ApiResponse(responseCode = "403", description = "Access Denied",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "400", description = "Validation Errors",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "409", description = "Patient or Doctor with such email exists",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))})
    })
//     @PreAuthorize("hasRole('ADMIN')")
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
                        .gender(registerDoctorForm.getGender())
                        .build(), HttpStatus.CREATED
        );
    }

    @Operation(summary = "Create an appointment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logged in successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppointmentDto.class)
                    )}
            ),
            @ApiResponse(responseCode = "403", description = "Access Denied. Password may be incorrect",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "400", description = "Validation Errors",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "404", description = "Patient or Doctor with email specified in json was not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))})
    })
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
                    .path("/")
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            return new ResponseEntity<>(JwtToken.builder().accessToken(jwt).build(), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }

    @Operation(summary = "Get profile information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Got successfully",
            content = {
                    @Content(mediaType = "application/json",
                    schema = @Schema(allOf = {DoctorDetailDto.class, PatientDto.class}),
                    examples = {
                            @ExampleObject(
                                    name = "Patient",
                                    value = """
                                            {
                                              "id": 123,
                                              "user": {
                                                "email": "johndoe@example.com",
                                                "role": "ROLE_USER",
                                                "createdAt": "2023-01-01T12:00:00Z",
                                                "updatedAt": "2024-01-01T12:00:00Z"
                                              },
                                              "firstName": "John",
                                              "lastName": "Doe",
                                              "dateOfBirth": "1990-05-15",
                                              "gender": "MALE",
                                              "bloodType": "OPlus",
                                              "allergies": "Peanuts",
                                              "address": "123 Main St, Springfield, USA",
                                              "phoneNumber": "1234567890",
                                              "createdAt": "2023-01-01T12:00:00Z",
                                              "updatedAt": "2024-01-01T12:00:00Z"
                                            }"""

                            ),
                            @ExampleObject(
                                    name="Doctor",
                                    value = """
                                            {
                                              "id": 123,
                                              "user": {
                                                "email": "janedoe@example.com",
                                                "role": "DOCTOR",
                                                "createdAt": "2022-02-02T12:00:00Z",
                                                "updatedAt": "2023-02-02T12:00:00Z"
                                              },
                                              "firstName": "Jane",
                                              "lastName": "Doe",
                                              "specializations": [
                                                {
                                                  "id": 1,
                                                  "specializationName": "Cardiology"
                                                },
                                                {
                                                  "id": 2,
                                                  "specializationName": "Neurology"
                                                }
                                              ],
                                              "licenseNumber": "123456789",
                                              "experienceYears": 10,
                                              "gender": "FEMALE",
                                              "phoneNumber": "+9876543210",
                                              "clinicAddress": "456 Clinic St, Health City, USA",
                                              "createdAt": "2022-02-02T12:00:00Z",
                                              "updatedAt": "2023-02-02T12:00:00Z"
                                            }"""
                            )
                    }),
            }),
            @ApiResponse(responseCode = "403", description = "Access Denied",
                    content = {
                    @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))
                    }
            )
    })
    @GetMapping("/profile")
    public ResponseEntity<?> getProfileInfo(HttpServletRequest request,
                                            @RequestHeader(name="Authorization") String header) {
        if (request.isUserInRole("PATIENT")) {
            PatientEntity patient = patientService.getPatient(header.substring(7));
            return new ResponseEntity<>(patientMapper.toDto(patient), HttpStatus.OK);
        } else if (request.isUserInRole("DOCTOR")) {
            DoctorEntity doctor = doctorService.getDoctorByEmail(header.substring(7));
            return new ResponseEntity<>(doctorMapper.toDto(doctor), HttpStatus.OK);
        }
        throw new AccessDeniedException("An error occurred while loading your profile.");
    }

    @Operation(summary = "Update the patient.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PatientDto.class))
                    }
            ),
            @ApiResponse(responseCode = "400", description = "Validation Error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "403", description = "Access Denied",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
    })
    @PatchMapping("/profile/patient")
    public ResponseEntity<PatientDto> patchPatientProfile(@RequestHeader(name="Authorization") String header,
                                          @Valid @RequestBody PatientDto updated,
                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String msg = bindingResult.getAllErrors().stream().map(err -> err.getDefaultMessage()).collect(Collectors.joining(" "));
            throw new ValidationException(msg);
        }
        PatientEntity updatedPatientEntity = patientMapper.toEntity(updated);
        PatientEntity patient = patientService.patchPatient(header.substring(7), updatedPatientEntity);
        return new ResponseEntity<>(patientMapper.toDto(patient), HttpStatus.OK);
    }

    @Operation(summary = "Update doctor's profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DoctorDetailDto.class)
                    )}
            ),
            @ApiResponse(responseCode = "403", description = "Access Denied",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "400", description = "Validation Error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "404", description = "Doctor was not found.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
    })
    @PatchMapping("/profile/doctor")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DoctorDetailDto> patchDoctorByMyEmail(@RequestHeader(name="Authorization") String header,
                                                                @Valid @RequestBody DoctorDetailDto doctor,
                                                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String msg = bindingResult.getAllErrors().stream().map(err -> err.getDefaultMessage()).collect(Collectors.joining(" "));
            throw new ValidationException(msg);
        }
        DoctorEntity doctorEntity = doctorMapper.toEntity(doctor);
        DoctorEntity patchedDoctor = doctorService.patchDoctor(header.substring(7), doctorEntity);
        return new ResponseEntity<>(doctorMapper.toDto(patchedDoctor), HttpStatus.OK);
    }

    @Operation(summary = "Delete profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access Denied",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "404", description = "Doctor was not found.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
    })
    @DeleteMapping("/profile")
    public ResponseEntity deleteProfile(HttpServletRequest request,
                                        @RequestHeader(name = "Authorization") String header) throws ServletException {
        if (request.isUserInRole("PATIENT")) {
            patientService.deletePatient(header.substring(7));
        } else if (request.isUserInRole("DOCTOR")) {
            doctorService.deleteDoctor(header.substring(7));
        }
        request.logout();
        return new ResponseEntity(null, HttpStatus.ACCEPTED);
    }


    @Operation(summary = "Get a jwt token from cookie.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = JwtToken.class)
                    )}
            ),
            @ApiResponse(responseCode = "403", description = "Access Denied or no cookies at all.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "401", description = "No such cookie.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
    })
    @GetMapping("/jwt")
    public ResponseEntity<JwtToken> getJwtToken(HttpServletRequest request) {
        if (request.getCookies() != null && request.getCookies().length > 0) {
        Cookie accessToken = Arrays.stream(request.getCookies())
                .filter(cookie -> "accessToken".equals(cookie.getName()))
                .findFirst().orElseThrow(() -> new NotAuthorizedException("You need to log in to proceed."));
        return new ResponseEntity<>(JwtToken.builder().accessToken(accessToken.getValue()).build(), HttpStatus.OK);
        }
        throw new NotAuthorizedException("You need to log in to proceed.");
    }
}
