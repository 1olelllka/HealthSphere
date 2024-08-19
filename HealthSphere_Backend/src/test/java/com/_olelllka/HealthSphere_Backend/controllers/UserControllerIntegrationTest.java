package com._olelllka.HealthSphere_Backend.controllers;

import com._olelllka.HealthSphere_Backend.TestDataUtil;
import com._olelllka.HealthSphere_Backend.domain.dto.LoginForm;
import com._olelllka.HealthSphere_Backend.domain.dto.RegisterForm;
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

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

    private UserService userService;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Autowired
    public UserControllerIntegrationTest(UserService userService, MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.userService = userService;
        this.objectMapper = new ObjectMapper();
    }

    @Test
    public void testThatUserRegisterReturnsHttp400BadRequestIfValidationFails() throws Exception {
        RegisterForm registerForm = TestDataUtil.createRegisterForm();
        registerForm.setEmail("wrongEmail");
        String registerFromJson = objectMapper.writeValueAsString(registerForm);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerFromJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists());
    }

    @Test
    public void testThatUserRegisterReturnsHttp201CreatedAndCorrectData() throws Exception {
        RegisterForm registerForm = TestDataUtil.createRegisterForm();
        String registerFormJson = objectMapper.writeValueAsString(registerForm);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerFormJson))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(registerForm.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").exists());
    }

    @Test
    public void testThatLoginUserReturnsHttp400BadRequestIfValidationFails() throws Exception {
        LoginForm loginForm = TestDataUtil.createLoginForm();
        loginForm.setEmail("");
        String loginFormJson = objectMapper.writeValueAsString(loginForm);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginFormJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists());
    }

    @Test
    public void testThatLoginUserReturnsHttp403ForbiddenIfPasswordIsIncorrect() throws Exception {
        RegisterForm registerForm = TestDataUtil.createRegisterForm();
        userService.register(registerForm);
        LoginForm loginForm = TestDataUtil.createLoginForm();
        loginForm.setPassword("wrongPassword");
        String loginFormJson = objectMapper.writeValueAsString(loginForm);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginFormJson))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testThatLoginUserReturnsHttp200OkWithJwtTokenAndCookieIfCorrectCredentials() throws Exception {
        RegisterForm registerForm = TestDataUtil.createRegisterForm();
        userService.register(registerForm);
        LoginForm loginForm = TestDataUtil.createLoginForm();
        String loginFormJson = objectMapper.writeValueAsString(loginForm);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginFormJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.cookie().exists("accessToken"))
                .andExpect(MockMvcResultMatchers.cookie().maxAge("accessToken", 60 * 60))
                .andExpect(MockMvcResultMatchers.cookie().httpOnly("accessToken", true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").exists()); // IMPORTANT: DELETE WHEN PRODUCTION.
    }

    @Test
    public void testThatLogoutUserReturnsHttp200OkIfUserIsNotLoggedIn() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/logout"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testThatLogoutUserReturnsHttp200OkIfUserIsLoggedId() throws Exception {
        RegisterForm registerForm = TestDataUtil.createRegisterForm();
        userService.register(registerForm);
        LoginForm loginForm = TestDataUtil.createLoginForm();
        String loginFormJson = objectMapper.writeValueAsString(loginForm);
        Cookie accessToken = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginFormJson))
                .andReturn().getResponse().getCookie("accessToken");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/logout")
                        .cookie(accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.cookie().maxAge("accessToken", 0));
    }
}
