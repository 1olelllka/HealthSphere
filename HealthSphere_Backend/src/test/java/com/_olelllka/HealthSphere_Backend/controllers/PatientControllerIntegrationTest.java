package com._olelllka.HealthSphere_Backend.controllers;

import com._olelllka.HealthSphere_Backend.TestDataUtil;
import com._olelllka.HealthSphere_Backend.domain.dto.LoginForm;
import com._olelllka.HealthSphere_Backend.domain.dto.PatientDto;
import com._olelllka.HealthSphere_Backend.domain.dto.RegisterPatientForm;
import com._olelllka.HealthSphere_Backend.domain.dto.UserDto;
import com._olelllka.HealthSphere_Backend.service.PatientService;
import com._olelllka.HealthSphere_Backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.text.SimpleDateFormat;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class PatientControllerIntegrationTest {

    private MockMvc mockMvc;
    private PatientService patientService;
    private UserService userService;
    private ObjectMapper objectMapper;

    @Autowired
    public PatientControllerIntegrationTest(MockMvc mockMvc,
                                            PatientService patientService,
                                            UserService userService) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper();
        this.patientService = patientService;
        this.userService = userService;
    }

    @Test
    @WithMockUser
    public void testThatGetPatientReturnsHttp401UnauthorizedIfCalledWithoutAccessCookie() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/patient/me")
                        .cookie(new Cookie("someCookie", "value")))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void testThatGetPatientReturnsHttp200OkAndCorrespondingDataIfCalledCorrectly() throws Exception {
        RegisterPatientForm registerPatientForm = TestDataUtil.createRegisterForm();
        userService.register(registerPatientForm);
        LoginForm loginForm = TestDataUtil.createLoginForm();
        String loginFormJson = objectMapper.writeValueAsString(loginForm);
        Cookie accessToken = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginFormJson)
                        .with(csrf()))
                .andReturn().getResponse().getCookie("accessToken");
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/patient/me")
                .cookie(accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(registerPatientForm.getFirstName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(registerPatientForm.getLastName()));
    }

    @Test
    @WithMockUser
    public void testThatPatchPatientReturnsHttp401UnauthorizedIfCalledWithoutAccessCookie() throws Exception {
        PatientDto patientDto = TestDataUtil.createPatientDto(null);
        String patientDtoJson = objectMapper.writeValueAsString(patientDto);
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/patient/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientDtoJson)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void testThatPatchPatientReturnsHttp400BadRequestIfDateOfBirthIsWrong() throws Exception {
        RegisterPatientForm register = TestDataUtil.createRegisterForm();
        userService.register(register);
        LoginForm loginForm = TestDataUtil.createLoginForm();
        String loginFormJson = objectMapper.writeValueAsString(loginForm);
        Cookie accessToken = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginFormJson)
                .with(csrf()))
                .andReturn().getResponse().getCookie("accessToken");
        PatientDto patientDto = TestDataUtil.createPatientDto(null);
        patientDto.setDateOfBirth(new SimpleDateFormat("dd-MM-yyyy").parse("11-11-2030"));
        String patientDtoJson = objectMapper.writeValueAsString(patientDto);
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/patient/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(patientDtoJson)
                        .cookie(accessToken)
                .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testThatPatchPatientReturnsHttp200OkIfEverythingIsOk() throws Exception {
        RegisterPatientForm register = TestDataUtil.createRegisterForm();
        userService.register(register);
        LoginForm loginForm = TestDataUtil.createLoginForm();
        String loginFormJson = objectMapper.writeValueAsString(loginForm);
        Cookie accessToken = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginFormJson)
                        .with(csrf()))
                .andReturn().getResponse().getCookie("accessToken");
        UserDto userDto = TestDataUtil.createUserDto();
        PatientDto patientDto = TestDataUtil.createPatientDto(userDto);
        patientDto.setFirstName("UPDATED");
        String patientDtoJson = objectMapper.writeValueAsString(patientDto);
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/patient/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientDtoJson)
                        .cookie(accessToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("UPDATED"));
    }
}
