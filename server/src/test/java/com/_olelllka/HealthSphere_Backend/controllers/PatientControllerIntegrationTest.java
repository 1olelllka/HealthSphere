package com._olelllka.HealthSphere_Backend.controllers;

import com._olelllka.HealthSphere_Backend.AbstractTestContainers;
import com._olelllka.HealthSphere_Backend.TestDataUtil;
import com._olelllka.HealthSphere_Backend.domain.dto.*;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.LoginForm;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.RegisterDoctorForm;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.RegisterPatientForm;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.UserDto;
import com._olelllka.HealthSphere_Backend.domain.dto.patients.PatientDto;
import com._olelllka.HealthSphere_Backend.repositories.PatientElasticRepository;
import com._olelllka.HealthSphere_Backend.service.SHA256;
import com._olelllka.HealthSphere_Backend.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class PatientControllerIntegrationTest extends AbstractTestContainers {

    @BeforeEach
    void initEach() throws ParseException {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
        patientElasticRepository.deleteAll();
        RegisterPatientForm register = TestDataUtil.createRegisterForm();
        listenerRegistry.getListenerContainer("doctor.post").start();
        userService.register(register);
    }


    private MockMvc mockMvc;
    private UserServiceImpl userService;
    private ObjectMapper objectMapper;
    private PatientElasticRepository patientElasticRepository;
    private RabbitListenerEndpointRegistry listenerRegistry;
    private RabbitAdmin admin;
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    public PatientControllerIntegrationTest(MockMvc mockMvc,
                                            UserServiceImpl userService,
                                            PatientElasticRepository patientElasticRepository,
                                            RabbitListenerEndpointRegistry listenerRegistry,
                                            RedisTemplate<String, String> redisTemplate,
                                            RabbitAdmin admin) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper();
        this.userService = userService;
        this.patientElasticRepository = patientElasticRepository;
        this.listenerRegistry = listenerRegistry;
        this.redisTemplate = redisTemplate;
        this.admin = admin;
    }

    @Test
    public void testThatGetPatientReturnsHttp403ForbiddenIfSentWithoutAccessToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/patients/me"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testThatGetPatientReturnsHttp200OkAndCorrespondingDataIfCalledCorrectly() throws Exception {
        String accessToken = getPatientAccessToken();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/patients/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("First Name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Last Name"));
        assertTrue(redisTemplate.hasKey("profile::" + SHA256.generateSha256Hash("patient2@email.com")));
    }

    @Test
    public void testThatGetAllPatientsReturnsHttp200OkAndPageOfResults() throws Exception {
        String accessToken = getDoctorAccessToken();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/patients")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").exists());
    }

    @Test
    public void testThatGetAllPatientsByParamsReturnsHttp200OkAndPageOfResults() throws Exception {
        String accessToken = getDoctorAccessToken();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/patients?search=First")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").exists());
    }

    @Test
    public void testThatGetDetailedPatientInfoReturnsHttp403ForbiddenIfCalledWithoutAccessToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/patients/1"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testThatGetDetailedPatientInfoReturnsHttp404NotFoundIfUserWithSuchIdWasNotFound() throws Exception {
        String accessToken = getPatientAccessToken();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/patients/4512")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testThatGetDetailedPatientInfoReturnsHttp200OkAndCorrespondingInfo() throws Exception {
        String accessToken = getPatientAccessToken();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/patients/1")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("First Name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Last Name"));
    }

    @Test
    public void testThatPatchPatientReturnsHttp403ForbiddenIfCalledWithoutAccessToken() throws Exception {
        PatientDto patientDto = TestDataUtil.createPatientDto(null);
        String patientDtoJson = objectMapper.writeValueAsString(patientDto);
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/patients/1")
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
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/patients/me")
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
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/patients/me")
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
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/patients/me"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testThatDeletePatientReturnsHttp202Accepted() throws Exception {
        listenerRegistry.stop();
        String accessToken = getPatientAccessToken();
        listenerRegistry.getListenerContainer("patient.delete").start();
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/patients/me")
                .header("Authorization", "Bearer " +accessToken))
                .andExpect(MockMvcResultMatchers.status().isAccepted());
        Awaitility.await().atMost(5, TimeUnit.SECONDS)
                .until(() -> admin.getQueueInfo("patient_index_delete_queue").getMessageCount() == 0);
        assertFalse(patientElasticRepository.existsById(1L));
        assertFalse(redisTemplate.hasKey("profile::" + SHA256.generateSha256Hash("patient2@email.com")));
    }

    private String getPatientAccessToken() throws Exception {
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
        String token = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/get-jwt")
                        .cookie(cookieToken))
                .andReturn().getResponse().getContentAsString();
        String accessToken = objectMapper.readValue(token, JwtToken.class).getAccessToken();
        return accessToken;
    }

    private String getDoctorAccessToken() throws Exception {
        RegisterDoctorForm doctorForm = TestDataUtil.createRegisterDoctorForm();
        userService.doctorRegister(doctorForm);
        LoginForm loginForm = TestDataUtil.createLoginForm();
        loginForm.setEmail("doctor@email.com");
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
