package com._olelllka.HealthSphere_Backend.controllers;

import com._olelllka.HealthSphere_Backend.TestDataUtil;
import com._olelllka.HealthSphere_Backend.domain.dto.*;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.LoginForm;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.RegisterPatientForm;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.UserDto;
import com._olelllka.HealthSphere_Backend.domain.dto.patients.PatientDto;
import com._olelllka.HealthSphere_Backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.text.SimpleDateFormat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class PatientControllerIntegrationTest {

    private MockMvc mockMvc;
    private UserService userService;
    private ObjectMapper objectMapper;

    @Autowired
    public PatientControllerIntegrationTest(MockMvc mockMvc,
                                            UserService userService) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper();
        this.userService = userService;
    }

    @Test
    public void testThatGetPatientReturnsHttp403ForbiddenIfSentWithoutAccessToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/patient/me"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testThatGetPatientReturnsHttp200OkAndCorrespondingDataIfCalledCorrectly() throws Exception {
        String accessToken = getAccessToken();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/patient/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("First Name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Last Name"));
    }

    @Test
    public void testThatGetDetailedPatientInfoReturnsHttp403ForbiddenIfCalledWithoutAccessToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/patient/1"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testThatGetDetailedPatientInfoReturnsHttp404NotFoundIfUserWithSuchIdWasNotFound() throws Exception {
        String accessToken = getAccessToken();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/patient/4512")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testThatGetDetailedPatientInfoReturnsHttp200OkAndCorrespondingInfo() throws Exception {
        String accessToken = getAccessToken();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/patient/1")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("First Name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Last Name"));
    }

    @Test
    public void testThatPatchPatientReturnsHttp403ForbiddenIfCalledWithoutAccessToken() throws Exception {
        PatientDto patientDto = TestDataUtil.createPatientDto(null);
        String patientDtoJson = objectMapper.writeValueAsString(patientDto);
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/patient/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientDtoJson))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testThatPatchPatientReturnsHttp400BadRequestIfDateOfBirthIsWrong() throws Exception {
        String accessToken = getAccessToken();
        PatientDto patientDto = TestDataUtil.createPatientDto(null);
        patientDto.setDateOfBirth(new SimpleDateFormat("dd-MM-yyyy").parse("11-11-2030"));
        String patientDtoJson = objectMapper.writeValueAsString(patientDto);
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/patient/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(patientDtoJson)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testThatPatchPatientReturnsHttp200OkIfEverythingIsOk() throws Exception {
        String accessToken = getAccessToken();
        UserDto userDto = TestDataUtil.createUserDto();
        PatientDto patientDto = TestDataUtil.createPatientDto(userDto);
        patientDto.setFirstName("UPDATED");
        String patientDtoJson = objectMapper.writeValueAsString(patientDto);
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/patient/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientDtoJson)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("UPDATED"));
    }

    @Test
    public void testThatDeleteByIdReturnsHttp403IfNotAuthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/patient/1"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testThatDeleteByIdReturnsHttp202Accepted() throws Exception {
        String accessToken = getAccessToken();
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/patient/1")
                .header("Authorization", "Bearer " +accessToken))
                .andExpect(MockMvcResultMatchers.status().isAccepted());
    }

    private String getAccessToken() throws Exception {
        RegisterPatientForm register = TestDataUtil.createRegisterForm();
        userService.register(register);
        LoginForm loginForm = TestDataUtil.createLoginForm();
        loginForm.setEmail("patient@email.com");
        String loginFormJson = objectMapper.writeValueAsString(loginForm);
        Cookie cookieToken = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginFormJson))
                .andReturn().getResponse().getCookie("accessToken");
        String token = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/get-jwt")
                        .cookie(cookieToken))
                .andReturn().getResponse().getContentAsString();
        String accessToken = objectMapper.readValue(token, JwtToken.class).getAccessToken();
        return accessToken;
    }
}
