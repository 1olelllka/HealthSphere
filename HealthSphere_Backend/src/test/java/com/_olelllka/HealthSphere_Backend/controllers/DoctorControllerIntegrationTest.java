package com._olelllka.HealthSphere_Backend.controllers;

import com._olelllka.HealthSphere_Backend.TestDataUtil;
import com._olelllka.HealthSphere_Backend.domain.dto.doctors.DoctorDetailDto;
import com._olelllka.HealthSphere_Backend.domain.dto.JwtToken;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.LoginForm;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.RegisterDoctorForm;
import com._olelllka.HealthSphere_Backend.repositories.DoctorElasticRepository;
import com._olelllka.HealthSphere_Backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
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
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@Testcontainers
public class DoctorControllerIntegrationTest {

    @Container
    static ElasticsearchContainer elasticsearchContainer =
            new ElasticsearchContainer(DockerImageName.parse("elasticsearch").withTag("7.17.23"));

    @Container
    static RabbitMQContainer container = new RabbitMQContainer(
            DockerImageName.parse("rabbitmq").withTag("3.13-management")
    );

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", container::getHost);
        registry.add("spring.rabbitmq.port", container::getAmqpPort);
        registry.add("spring.rabbitmq.username", container::getAdminUsername);
        registry.add("spring.rabbitmq.password", container::getAdminPassword);
        registry.add("spring.elasticsearch.uris", elasticsearchContainer::getHttpHostAddress);
    }

    @Autowired
    private RabbitListenerEndpointRegistry listenerRegistry;
    @Autowired
    private RabbitAdmin admin;


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

    @BeforeEach
    void initEach() {
        elasticRepository.deleteById(1L);
    }

    private MockMvc mockMvc;
    private UserService userService;
    private ObjectMapper objectMapper;
    private DoctorElasticRepository elasticRepository;

    @Autowired
    public DoctorControllerIntegrationTest(MockMvc mockMvc,
                                           UserService userService,
                                           DoctorElasticRepository elasticRepository) {
        this.mockMvc = mockMvc;
        this.userService = userService;
        this.objectMapper = new ObjectMapper();
        this.elasticRepository = elasticRepository;
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
        String accessToken = getAccessToken();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/doctors")
                .header("Authorization", "Bearer " + accessToken))
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
        assertTrue(elasticRepository.findById(1L).isPresent());
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
        assertTrue(elasticRepository.findById(1L).isPresent());
    }

    @Test
    @WithMockUser(roles = {"PATIENT"})
    public void testThatDeleteDoctorByEmailReturnsHttp403ForbiddenIfCalledUnauthorizedOrWrongRole() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/doctors/me"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testThatDeleteDoctorByEmailReturnHttp203Accepted() throws Exception {
        listenerRegistry.stop();
        assertEquals(0, Objects.requireNonNull(admin.getQueueInfo("doctor_index_delete_queue")).getMessageCount());
        String accessToken = getAccessToken();
        assertTrue(elasticRepository.findById(1L).isPresent());
        listenerRegistry.getListenerContainer("doctor.delete").start();
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/doctors/me")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isAccepted());
        assertTrue(elasticRepository.findById(1L).isEmpty());
    }

    private String getAccessToken() throws Exception {
        RegisterDoctorForm register = TestDataUtil.createRegisterDoctorForm();
        listenerRegistry.getListenerContainer("doctor.post").start();
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
