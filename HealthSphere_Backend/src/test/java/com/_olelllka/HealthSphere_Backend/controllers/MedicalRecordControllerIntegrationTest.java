package com._olelllka.HealthSphere_Backend.controllers;

import com._olelllka.HealthSphere_Backend.TestDataUtil;
import com._olelllka.HealthSphere_Backend.domain.dto.JwtToken;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.LoginForm;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.RegisterPatientForm;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class MedicalRecordControllerIntegrationTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private UserService userService;

    @Autowired
    public MedicalRecordControllerIntegrationTest(MockMvc mockMvc,
                                                  UserService userService) {
        this.mockMvc = mockMvc;
        this.userService = userService;
        this.objectMapper = new ObjectMapper();
    }

    @Test
    public void testThatGetAllMedicalRecordsReturnsHttp403IfAuthorizationHeaderNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/patient/medical-records"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testThatGetAllMedicalRecordsReturnsHttp200OkAndCorrespondingData() throws Exception {
        String accessToken = getAccessToken(); // for user
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/patient/medical-records")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk());
    } // it'll return empty list for now. But when I implement creation of records, I'll override this.

    @Test
    public void testThatGetMedicalRecordsReturnsHttp403ForbiddenIfUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/patient/medical-records/1"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testThatGetMedicalRecordsReturnsHttp404NotFoundIfCorrespondingRecordsDoesNotExist() throws Exception {
        String accessToken = getAccessToken();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/patient/medical-records/1")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    // the test will be created when medical-record creation will be created;
//    @Test
//    public void testThatGetMedicalRecordsReturnsHttp200OkIfEverythingGood() throws Exception {
//        String accessToken = getAccessToken();
//    }

    private String getAccessToken() throws Exception {
        RegisterPatientForm register = TestDataUtil.createRegisterForm();
        userService.register(register);
        LoginForm loginForm = TestDataUtil.createLoginForm();
        String loginFormJson = objectMapper.writeValueAsString(loginForm);
        Cookie cookieToken = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginFormJson)
                        .with(csrf()))
                .andReturn().getResponse().getCookie("accessToken");
        String token = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/get-jwt")
                        .cookie(cookieToken))
                .andReturn().getResponse().getContentAsString();
        String accessToken = objectMapper.readValue(token, JwtToken.class).getAccessToken();
        return accessToken;
    }
}
