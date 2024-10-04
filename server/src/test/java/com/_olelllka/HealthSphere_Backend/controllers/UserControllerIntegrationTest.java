package com._olelllka.HealthSphere_Backend.controllers;

import com._olelllka.HealthSphere_Backend.AbstractTestContainers;
import com._olelllka.HealthSphere_Backend.TestDataUtil;
import com._olelllka.HealthSphere_Backend.domain.dto.JwtToken;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.LoginForm;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.RegisterDoctorForm;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.RegisterPatientForm;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.UserDto;
import com._olelllka.HealthSphere_Backend.domain.dto.doctors.DoctorDetailDto;
import com._olelllka.HealthSphere_Backend.domain.dto.patients.PatientDto;
import com._olelllka.HealthSphere_Backend.domain.entity.DoctorEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.Role;
import com._olelllka.HealthSphere_Backend.domain.entity.UserEntity;
import com._olelllka.HealthSphere_Backend.repositories.DoctorElasticRepository;
import com._olelllka.HealthSphere_Backend.repositories.PatientElasticRepository;
import com._olelllka.HealthSphere_Backend.repositories.UserRepository;
import com._olelllka.HealthSphere_Backend.service.SHA256;
import com._olelllka.HealthSphere_Backend.service.UserService;
import com._olelllka.HealthSphere_Backend.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class UserControllerIntegrationTest extends AbstractTestContainers {

    private UserService userService;
    private UserRepository userRepository;
    private PatientElasticRepository patientElasticRepository;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private RabbitListenerEndpointRegistry listenerRegistry;
    private RabbitAdmin admin;
    private DoctorElasticRepository doctorElasticRepository;
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    public UserControllerIntegrationTest(UserService userService,
                                         MockMvc mockMvc,
                                         UserRepository userRepository,
                                         PatientElasticRepository patientElasticRepository,
                                         DoctorElasticRepository doctorElasticRepository,
                                         RedisTemplate<String, String> redisTemplate,
                                         RabbitListenerEndpointRegistry listenerRegistry,
                                         RabbitAdmin admin) {
        this.mockMvc = mockMvc;
        this.userService = userService;
        this.objectMapper = new ObjectMapper();
        this.userRepository = userRepository;
        this.admin = admin;
        this.listenerRegistry = listenerRegistry;
        this.patientElasticRepository = patientElasticRepository;
        this.redisTemplate = redisTemplate;
        this.doctorElasticRepository = doctorElasticRepository;
    }

    @AfterEach
    void tearDown() {
        doctorElasticRepository.deleteAll();
        patientElasticRepository.deleteAll();
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
    public void testThatUserRegisterReturnsHttp409ConflictIfAlreadyExists() throws Exception {
        RegisterPatientForm registerPatientForm = TestDataUtil.createRegisterForm();
        userService.register(registerPatientForm);
        String registerFromJson = objectMapper.writeValueAsString(registerPatientForm);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/register/patient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerFromJson))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void testThatUserRegisterReturnsHttp201CreatedAndCorrectData() throws Exception {
        listenerRegistry.stop();
        RegisterPatientForm registerPatientForm = TestDataUtil.createRegisterForm();
        String registerFormJson = objectMapper.writeValueAsString(registerPatientForm);
        listenerRegistry.getListenerContainer("patient.post").start();
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/register/patient")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerFormJson))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(registerPatientForm.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").exists());
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .until(() -> admin.getQueueInfo("patient_index_queue").getMessageCount() == 0);
        assertTrue(StreamSupport.stream(patientElasticRepository.findAll().spliterator(), false).toList().size() == 1);
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
    public void testThatDoctorRegisterReturnsHttp409ConflictIfAlreadyExists() throws Exception {
        String accessToken = getAccessTokenForAdmin();
        RegisterDoctorForm registerDoctorForm = TestDataUtil.createRegisterDoctorForm();
        userService.doctorRegister(registerDoctorForm);
        String registerFromJson = objectMapper.writeValueAsString(registerDoctorForm);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/register/doctor")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerFromJson))
                .andExpect(MockMvcResultMatchers.status().isConflict());
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
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                        .until(() -> admin.getQueueInfo("doctors_index_queue").getMessageCount() == 0);
        assertTrue(doctorElasticRepository.findById(1L).isPresent());
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
        String token = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/jwt")
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
    public void testThatGetProfileReturnsHttp403ForbiddenIfUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/profile"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testThatGetProfileReturnsHttp200OkAndPatientData() throws Exception {
        String accessToken = getPatientAccessToken();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/profile")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.email").value("patient2@email.com"));
        assertTrue(patientElasticRepository.findById(1L).isPresent());
        assertTrue(redisTemplate.hasKey("profile::" + SHA256.generateSha256Hash("patient2@email.com")));
    }

    @Test
    public void testThatPatchPatientReturnsHttp403ForbiddenIfCalledWithoutAccessToken() throws Exception {
        PatientDto patientDto = TestDataUtil.createPatientDto(null);
        String patientDtoJson = objectMapper.writeValueAsString(patientDto);
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/profile/patient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientDtoJson))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testThatPatchPatientReturnsHttp400BadRequestIfDateOfBirthIsWrong() throws Exception {
        String accessToken = getPatientAccessToken();
        PatientDto patientDto = TestDataUtil.createPatientDto(null);
        patientDto.setDateOfBirth(new SimpleDateFormat("dd-MM-yyyy").parse("11-11-2030"));
        String patientDtoJson = objectMapper.writeValueAsString(patientDto);
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/profile/patient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientDtoJson)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testThatPatchPatientReturnsHttp200OkIfEverythingIsOk() throws Exception {
        listenerRegistry.stop();
        String accessToken = getPatientAccessToken();
        UserDto userDto = TestDataUtil.createUserDto();
        PatientDto patientDto = TestDataUtil.createPatientDto(userDto);
        patientDto.setFirstName("UPDATED");
        String patientDtoJson = objectMapper.writeValueAsString(patientDto);
        listenerRegistry.getListenerContainer("patient.post").start();
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/profile/patient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientDtoJson)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("UPDATED"));
        Awaitility.await().atMost(5, TimeUnit.SECONDS)
                .until(() -> admin.getQueueInfo("patient_index_queue").getMessageCount() == 0);
        assertTrue(redisTemplate.hasKey("profile::" + SHA256.generateSha256Hash("patient2@email.com")));
    }

    @Test
    public void testThatDeletePatientReturnsHttp403IfNotAuthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/profile"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testThatDeletePatientReturnsHttp202Accepted() throws Exception {
        listenerRegistry.stop();
        String accessToken = getPatientAccessToken();
        listenerRegistry.getListenerContainer("patient.delete").start();
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/profile")
                        .header("Authorization", "Bearer " +accessToken))
                .andExpect(MockMvcResultMatchers.status().isAccepted());
        Awaitility.await().atMost(5, TimeUnit.SECONDS)
                .until(() -> admin.getQueueInfo("patient_index_delete_queue").getMessageCount() == 0);
        assertFalse(StreamSupport.stream(patientElasticRepository.findAll().spliterator(), false).toList().size() == 1);
        assertFalse(redisTemplate.hasKey("profile::" + SHA256.generateSha256Hash("patient2@email.com")));
    }

    @Test
    public void testThatGetProfileReturnsHttp200OkAndDoctorData() throws Exception {
        String accessToken = getDoctorAccessToken();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/profile")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.email").value("doctor@email.com"));
        assertTrue(StreamSupport.stream(doctorElasticRepository.findAll().spliterator(), false).toList().size() == 1);
        assertTrue(redisTemplate.hasKey("profile::" + SHA256.generateSha256Hash("doctor@email.com")));
    }

    @Test
    public void testThatPatchDoctorReturnsHttp403ForbiddenIfCalledUnauthorizedOrWrongRole() throws Exception {
        DoctorDetailDto doctorDetailDto = TestDataUtil.createDoctorDetailDto(null);
        String doctorJson = objectMapper.writeValueAsString(doctorDetailDto);
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/profile/doctor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(doctorJson))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testThatPatchDoctorReturnsHttp400BadRequestIfInvalidData() throws Exception {
        String accessToken = getDoctorAccessToken();
        DoctorDetailDto doctorDetailDto = TestDataUtil.createDoctorDetailDto(null);
        doctorDetailDto.setClinicAddress("");
        String json = objectMapper.writeValueAsString(doctorDetailDto);
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/profile/doctor")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testThatPatchDoctorReturnsHttp200OkAndCorrespondingData() throws Exception {
        listenerRegistry.stop();
        String accessToken = getDoctorAccessToken();
        DoctorDetailDto doctorDetailDto = TestDataUtil.createDoctorDetailDto(null);
        String json = objectMapper.writeValueAsString(doctorDetailDto);
        listenerRegistry.getListenerContainer("doctor.post").start();
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/profile/doctor")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(doctorDetailDto.getFirstName()));
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                        .until(() -> admin.getQueueInfo("doctors_index_queue").getMessageCount() == 0);
        assertTrue(StreamSupport.stream(doctorElasticRepository.findAll().spliterator(), false).toList().size() == 1);
        assertTrue(redisTemplate.hasKey("profile::" + SHA256.generateSha256Hash("doctor@email.com")));
    }

    @Test
    public void testThatDeleteDoctorReturnHttp203Accepted() throws Exception {
        listenerRegistry.stop();
        assertEquals(0, Objects.requireNonNull(admin.getQueueInfo("doctor_index_delete_queue")).getMessageCount());
        String accessToken = getDoctorAccessToken();
        listenerRegistry.getListenerContainer("doctor.delete").start();
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/profile")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isAccepted());
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .until(
                        () -> Objects.requireNonNull(admin.getQueueInfo("doctor_index_delete_queue")).getMessageCount() == 0
                );
        assertTrue(StreamSupport.stream(doctorElasticRepository.findAll().spliterator(), false).toList().size() == 0);
        assertFalse(redisTemplate.hasKey("profile::" + SHA256.generateSha256Hash("doctor@email.com")));
    }

    @Test
    public void testThatGetJwtReturnsHttp401UnauthorizedIfAccessTokenWasNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/jwt")
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
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/jwt")
                        .cookie(cookieToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").exists());
    }

    private String getPatientAccessToken() throws Exception {
        listenerRegistry.getListenerContainer("patient.post").start();
        RegisterPatientForm register = TestDataUtil.createRegisterForm();
        register.setEmail("patient2@email.com");
        userService.register(register);
        LoginForm loginForm = TestDataUtil.createLoginForm();
        loginForm.setEmail("patient2@email.com");
        String loginFormJson = objectMapper.writeValueAsString(loginForm);
        Cookie cookieToken = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginFormJson))
                .andReturn().getResponse().getCookie("accessToken");
        String token = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/jwt")
                        .cookie(cookieToken))
                .andReturn().getResponse().getContentAsString();
        listenerRegistry.stop();
        return objectMapper.readValue(token, JwtToken.class).getAccessToken();
    }

    private String getDoctorAccessToken() throws Exception {
        listenerRegistry.getListenerContainer("doctor.post").start();
        RegisterDoctorForm doctorForm = TestDataUtil.createRegisterDoctorForm();
        userService.doctorRegister(doctorForm);
        LoginForm loginForm = TestDataUtil.createLoginForm();
        loginForm.setEmail("doctor@email.com");
        String loginFormJson = objectMapper.writeValueAsString(loginForm);
        Cookie cookieToken = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginFormJson))
                .andReturn().getResponse().getCookie("accessToken");
        String token = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/jwt")
                        .cookie(cookieToken))
                .andReturn().getResponse().getContentAsString();
        listenerRegistry.stop();
        return objectMapper.readValue(token, JwtToken.class).getAccessToken();
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
        String token = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/jwt")
                        .cookie(cookieToken))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(token, JwtToken.class).getAccessToken();
    }
}
