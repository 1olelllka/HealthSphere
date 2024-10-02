package com._olelllka.HealthSphere_Backend.controllers;

import com._olelllka.HealthSphere_Backend.AbstractTestContainers;
import com._olelllka.HealthSphere_Backend.TestDataUtil;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.RegisterDoctorForm;
import com._olelllka.HealthSphere_Backend.repositories.DoctorElasticRepository;
import com._olelllka.HealthSphere_Backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.text.ParseException;

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
    private DoctorElasticRepository elasticRepository;
    private RabbitListenerEndpointRegistry listenerRegistry;

    @Autowired
    public DoctorControllerIntegrationTest(MockMvc mockMvc,
                                           UserService userService,
                                           DoctorElasticRepository elasticRepository,
                                           RabbitListenerEndpointRegistry listenerRegistry) {
        this.mockMvc = mockMvc;
        this.userService = userService;
        this.elasticRepository = elasticRepository;
        this.listenerRegistry = listenerRegistry;
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

}
