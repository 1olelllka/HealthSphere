package com._olelllka.HealthSphere_Backend.controllers;

import com._olelllka.HealthSphere_Backend.AbstractTestContainers;
import com._olelllka.HealthSphere_Backend.TestDataUtil;
import com._olelllka.HealthSphere_Backend.domain.dto.doctors.DoctorDetailDto;
import com._olelllka.HealthSphere_Backend.domain.dto.JwtToken;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.LoginForm;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.RegisterDoctorForm;
import com._olelllka.HealthSphere_Backend.repositories.DoctorElasticRepository;
import com._olelllka.HealthSphere_Backend.service.SHA256;
import com._olelllka.HealthSphere_Backend.service.UserService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.text.ParseException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@Testcontainers
public class DoctorControllerIntegrationTest extends AbstractTestContainers {

    @BeforeEach
    void initEach() throws ParseException {
        elasticRepository.deleteAll();
        RegisterDoctorForm register = TestDataUtil.createRegisterDoctorForm();
        listenerRegistry.getListenerContainer("doctor.post").start();
        userService.doctorRegister(register);
    }

    private MockMvc mockMvc;
    private UserService userService;
    private ObjectMapper objectMapper;
    private DoctorElasticRepository elasticRepository;
    private RabbitListenerEndpointRegistry listenerRegistry;
    private RabbitAdmin admin;
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    public DoctorControllerIntegrationTest(MockMvc mockMvc,
                                           UserService userService,
                                           DoctorElasticRepository elasticRepository,
                                           RedisTemplate<String, String> redisTemplate,
                                           RabbitListenerEndpointRegistry listenerRegistry,
                                           RabbitAdmin admin) {
        this.mockMvc = mockMvc;
        this.userService = userService;
        this.objectMapper = new ObjectMapper();
        this.elasticRepository = elasticRepository;
        this.listenerRegistry = listenerRegistry;
        this.redisTemplate = redisTemplate;
        this.admin = admin;
    }


    @Test
    public void testThatGetAllDoctorsReturnsHttp200OkAndCorrespondingData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/doctors"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.pageable").exists());
        assertTrue(elasticRepository.findById(1L).isPresent());
    }

    @Test
    public void testThatGetAllDoctorsByParamsReturnsHttp200OkAndData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/doctors?search=First Last"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.pageable").exists());
        assertTrue(elasticRepository.findById(1L).isPresent());
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
        assertTrue(elasticRepository.findById(1L).isPresent());
        assertTrue(redisTemplate.hasKey("profile::" + SHA256.generateSha256Hash("doctor@email.com")));
    }

    @Test
    public void testThatGetDoctorByIdReturnsHttp404NotFoundIfDoctorWasNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/doctors/3"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testThatGetDoctorByIdReturnsHttp200OkIfDoctorWasFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/doctors/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("First Name"));
        assertTrue(elasticRepository.findById(1L).isPresent());
    }

    @Test
    public void testThatPatchDoctorReturnsHttp403ForbiddenIfCalledUnauthorizedOrWrongRole() throws Exception {
        DoctorDetailDto doctorDetailDto = TestDataUtil.createDoctorDetailDto(null);
        String doctorJson = objectMapper.writeValueAsString(doctorDetailDto);
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/doctors/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(doctorJson))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testThatPatchDoctorReturnsHttp200OkAndCorrespondingData() throws Exception {
        String accessToken = getAccessToken();
        DoctorDetailDto doctorDetailDto = TestDataUtil.createDoctorDetailDto(null);
        String json = objectMapper.writeValueAsString(doctorDetailDto);
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/doctors/me")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(doctorDetailDto.getFirstName()));
        assertTrue(elasticRepository.findById(1L).isPresent());
        assertTrue(redisTemplate.hasKey("profile::" + SHA256.generateSha256Hash("doctor@email.com")));
    }

    @Test
    public void testThatDeleteDoctorReturnsHttp403ForbiddenIfCalledUnauthorizedOrWrongRole() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/doctors/me"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
        assertTrue(redisTemplate.hasKey("profile::" + SHA256.generateSha256Hash("doctor@email.com")));
    }

    @Test
    public void testThatDeleteDoctorByIdReturnHttp203Accepted() throws Exception {
        listenerRegistry.stop();
        assertEquals(0, Objects.requireNonNull(admin.getQueueInfo("doctor_index_delete_queue")).getMessageCount());
        String accessToken = getAccessToken();
        listenerRegistry.getListenerContainer("doctor.delete").start();
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/doctors/me")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isAccepted());
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .until(
                () -> Objects.requireNonNull(admin.getQueueInfo("doctor_index_delete_queue")).getMessageCount() == 0
        );
        assertTrue(elasticRepository.findById(1L).isEmpty());
        assertFalse(redisTemplate.hasKey("profile::" + SHA256.generateSha256Hash("doctor@email.com")));
    }

    private String getAccessToken() throws Exception {
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
