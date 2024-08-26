package com._olelllka.HealthSphere_Backend.controllers;

import com._olelllka.HealthSphere_Backend.TestDataUtil;
import com._olelllka.HealthSphere_Backend.domain.dto.DoctorDetailDto;
import com._olelllka.HealthSphere_Backend.domain.dto.JwtToken;
import com._olelllka.HealthSphere_Backend.domain.dto.LoginForm;
import com._olelllka.HealthSphere_Backend.domain.dto.RegisterDoctorForm;
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


@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class DoctorControllerIntegrationTest {

    private MockMvc mockMvc;
    private UserService userService;
    private ObjectMapper objectMapper;

    @Autowired
    public DoctorControllerIntegrationTest(MockMvc mockMvc,
                                           UserService userService) {
        this.mockMvc = mockMvc;
        this.userService = userService;
        this.objectMapper = new ObjectMapper();
    }

    @Test
    public void testThatGetAllDoctorsReturnsHttp403ForbiddenIfCalledUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/doctors"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testThatGetAllDoctorsReturnsHttp200OkAndCorrespondingData() throws Exception {
        RegisterDoctorForm doctor = TestDataUtil.createRegisterDoctorForm();
        doctor.setEmail("doctor@doctor.com");
        userService.doctorRegister(doctor);
        String accessToken = getAccessToken();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/doctors")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.pageable").exists());
    }

    @Test
    @WithMockUser(roles = {"PATIENT"})
    public void testThatGetDoctorByEmailReturnsHttp403ForbiddenIfCalledUnauthorizedOrWrongRole() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/doctors/me"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testThatGetDoctorByEmailReturnsHttp200OkAndCorrespondingData() throws Exception {
        String accessToken = getAccessToken();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/doctors/me")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("First Name"));
    }

    @Test
    public void testThatGetDoctorByIdReturnsHttp403ForbiddenIfCalledUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/doctors/1"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testThatGetDoctorByIdReturnsHttp404NotFoundIfDoctorWasNotFound() throws Exception {
        String accessToken = getAccessToken();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/doctors/3")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testThatGetDoctorByIdReturnsHttp200OkIfDoctorWasFound() throws Exception {
        String accessToken = getAccessToken();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/doctors/1")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("First Name"));
    }

    @Test
    @WithMockUser(roles = {"PATIENT"})
    public void testThatPatchDoctorByEmailReturnsHttp403ForbiddenIfCalledUnauthorizedOrWrongRole() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/doctors/me"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testThatPatchDoctorByEmailReturnsHttp200OkAndCorrespondingData() throws Exception {
        String accessToken = getAccessToken();
        DoctorDetailDto doctorDetailDto = TestDataUtil.createDoctorDetailDto(null);
        String json = objectMapper.writeValueAsString(doctorDetailDto);
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/doctors/me")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(doctorDetailDto.getFirstName()));
    }

    @Test
    @WithMockUser(roles = {"PATIENT"})
    public void testThatDeleteDoctorByEmailReturnsHttp403ForbiddenIfCalledUnauthorizedOrWrongRole() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/doctors/me"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testThatDeleteDoctorByEmailReturnHttp203Accepted() throws Exception {
        String accessToken = getAccessToken();
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/doctors/me")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isAccepted());
    }

    private String getAccessToken() throws Exception {
        RegisterDoctorForm register = TestDataUtil.createRegisterDoctorForm();
        userService.doctorRegister(register);
        LoginForm loginForm = TestDataUtil.createLoginForm();
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
