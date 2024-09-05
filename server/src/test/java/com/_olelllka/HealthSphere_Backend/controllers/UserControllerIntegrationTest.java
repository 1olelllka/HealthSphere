package com._olelllka.HealthSphere_Backend.controllers;

import com._olelllka.HealthSphere_Backend.TestContainers;
import com._olelllka.HealthSphere_Backend.TestDataUtil;
import com._olelllka.HealthSphere_Backend.domain.dto.JwtToken;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.LoginForm;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.RegisterDoctorForm;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.RegisterPatientForm;
import com._olelllka.HealthSphere_Backend.domain.entity.Role;
import com._olelllka.HealthSphere_Backend.domain.entity.UserEntity;
import com._olelllka.HealthSphere_Backend.repositories.UserRepository;
import com._olelllka.HealthSphere_Backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

    @Container
    static ElasticsearchContainer elasticsearchContainer = TestContainers.elasticsearchContainer;

    @Container
    static RabbitMQContainer container = TestContainers.rabbitMQContainer;

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", container::getHost);
        registry.add("spring.rabbitmq.port", container::getAmqpPort);
        registry.add("spring.rabbitmq.username", container::getAdminUsername);
        registry.add("spring.rabbitmq.password", container::getAdminPassword);
        registry.add("spring.elasticsearch.uris", elasticsearchContainer::getHttpHostAddress);
    }


    @BeforeAll
    static void setUp() {
        container.start();
        elasticsearchContainer.start();
    }

    @AfterAll
    static void tearDown() {
        container.stop();
        elasticsearchContainer.stop();
    }

    private UserService userService;
    private UserRepository userRepository;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private RabbitListenerEndpointRegistry listenerRegistry;
    private RabbitAdmin admin;

    @Autowired
    public UserControllerIntegrationTest(UserService userService,
                                         MockMvc mockMvc,
                                         UserRepository userRepository,
                                         RabbitListenerEndpointRegistry listenerRegistry,
                                         RabbitAdmin admin) {
        this.mockMvc = mockMvc;
        this.userService = userService;
        this.objectMapper = new ObjectMapper();
        this.userRepository = userRepository;
        this.admin = admin;
        this.listenerRegistry = listenerRegistry;
    }


    @Test
    public void testThatUserRegisterReturnsHttp400BadRequestIfValidationFails() throws Exception {
        RegisterPatientForm registerPatientForm = TestDataUtil.createRegisterForm();
        registerPatientForm.setEmail("wrongEmail");
        String registerFromJson = objectMapper.writeValueAsString(registerPatientForm);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/register/patient")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerFromJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists());
    }

    @Test
    public void testThatUserRegisterReturnsHttp201CreatedAndCorrectData() throws Exception {
        RegisterPatientForm registerPatientForm = TestDataUtil.createRegisterForm();
        String registerFormJson = objectMapper.writeValueAsString(registerPatientForm);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/register/patient")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerFormJson))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(registerPatientForm.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").exists());
    }

    @Test
    public void testThatDoctorRegisterReturnsHttp403ForbiddenIfUserHasWrongRole() throws Exception {
        RegisterDoctorForm registerDoctorForm = TestDataUtil.createRegisterDoctorForm();
        String registerFromJson = objectMapper.writeValueAsString(registerDoctorForm);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/register/doctor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerFromJson))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testThatDoctorRegisterReturnsHttp400BadRequestIfInvalidData() throws Exception {
        String accessToken = getAccessTokenForAdmin();
        RegisterDoctorForm registerDoctorForm = TestDataUtil.createRegisterDoctorForm();
        registerDoctorForm.setFirstName("");
        String registerFromJson = objectMapper.writeValueAsString(registerDoctorForm);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/register/doctor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerFromJson)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testThatDoctorRegisterReturnsHttp201CreatedAndCorrespondingDataIfEverythingOk() throws Exception {
        listenerRegistry.stop();
        assertEquals(0, Objects.requireNonNull(admin.getQueueInfo("doctors_index_queue")).getMessageCount());
        String accessToken = getAccessTokenForAdmin();
        RegisterDoctorForm registerDoctorForm = TestDataUtil.createRegisterDoctorForm();
        registerDoctorForm.setSpecializations(List.of());
        String registerFromJson = objectMapper.writeValueAsString(registerDoctorForm);
        listenerRegistry.getListenerContainer("doctor.post").start();
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/register/doctor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerFromJson)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(registerDoctorForm.getEmail()))
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
        RegisterPatientForm registerPatientForm = TestDataUtil.createRegisterForm();
        userService.register(registerPatientForm);
        LoginForm loginForm = TestDataUtil.createLoginForm();
        loginForm.setEmail("patient@email.com");
        loginForm.setPassword("wrongPassword");
        String loginFormJson = objectMapper.writeValueAsString(loginForm);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginFormJson))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testThatLoginUserReturnsHttp200OkWithJwtTokenAndCookieIfCorrectCredentials() throws Exception {
        RegisterPatientForm registerPatientForm = TestDataUtil.createRegisterForm();
        userService.register(registerPatientForm);
        LoginForm loginForm = TestDataUtil.createLoginForm();
        loginForm.setEmail("patient@email.com");
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
    public void testThatLogoutUserReturnsHttp200OkIfUserIsLoggedIn() throws Exception {
        RegisterPatientForm registerPatientForm = TestDataUtil.createRegisterForm();
        userService.register(registerPatientForm);
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

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/logout")
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.cookie().maxAge("accessToken", 0));
    }

    @Test
    public void testThatGetJwtReturnsHttp401UnauthorizedIfAccessTokenWasNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/get-jwt")
                .cookie(new Cookie("someName", "someValue")))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void testThatGetJwtReturnHttp200OkAndAccessToken() throws Exception {
        RegisterPatientForm registerPatientForm = TestDataUtil.createRegisterForm();
        userService.register(registerPatientForm);
        LoginForm loginForm = TestDataUtil.createLoginForm();
        loginForm.setEmail("patient@email.com");
        String loginFormJson = objectMapper.writeValueAsString(loginForm);
        Cookie cookieToken = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginFormJson))
                .andReturn().getResponse().getCookie("accessToken");
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/get-jwt")
                        .cookie(cookieToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").exists());
    }

    private String getAccessTokenForAdmin() throws Exception {
        UserEntity user = UserEntity.builder()
                .email("email@email.com")
                .password(new BCryptPasswordEncoder().encode("password123")).role(Role.ROLE_ADMIN).build();
        userRepository.save(user);
        LoginForm loginForm = TestDataUtil.createLoginForm();
        String loginFormJson = objectMapper.writeValueAsString(loginForm);
        Cookie cookieToken = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginFormJson))
                .andReturn().getResponse().getCookie("accessToken");
        String token = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/get-jwt")
                        .cookie(cookieToken))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(token, JwtToken.class).getAccessToken();
    }
}
