package com._olelllka.HealthSphere_Backend.controllers;

import com._olelllka.HealthSphere_Backend.AbstractTestContainers;
import com._olelllka.HealthSphere_Backend.TestDataUtil;
import com._olelllka.HealthSphere_Backend.domain.dto.JwtToken;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.LoginForm;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.RegisterDoctorForm;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.RegisterPatientForm;
import com._olelllka.HealthSphere_Backend.domain.dto.prescriptions.PrescriptionDto;
import com._olelllka.HealthSphere_Backend.domain.dto.prescriptions.PrescriptionMedicineDto;
import com._olelllka.HealthSphere_Backend.domain.entity.PatientEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.PrescriptionEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.PrescriptionMedicineEntity;
import com._olelllka.HealthSphere_Backend.service.PrescriptionService;
import com._olelllka.HealthSphere_Backend.service.UserService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@Testcontainers
public class PrescriptionControllerIntegrationTest extends AbstractTestContainers {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private PrescriptionService service;
    private UserService userService;
    private RabbitListenerEndpointRegistry listenerRegistry;

    @Autowired
    public PrescriptionControllerIntegrationTest(MockMvc mockMvc,
                                                 PrescriptionService service,
                                                 UserService userService,
                                                 RabbitListenerEndpointRegistry listenerRegistry) {
        this.mockMvc = mockMvc;
        this.service = service;
        this.userService = userService;
        this.objectMapper = new ObjectMapper();
        this.listenerRegistry = listenerRegistry;
    }

    @BeforeEach
    void initEach() {
        RegisterDoctorForm register = TestDataUtil.createRegisterDoctorForm();
        register.setEmail("doctor@email.com");
        register.setSpecializations(List.of());
        listenerRegistry.getListenerContainer("doctor.post").start();
        userService.doctorRegister(register);
    }

    @Test
    public void testThatCreateNewPrescriptionReturnsHttp403ForbiddenIfUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/prescriptions"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testThatCreateNewPrescriptionReturnsHttp400BadRequestIfDataIsInvalid() throws Exception {
        String jwt = getAccessToken();
        PrescriptionDto dto = TestDataUtil.createPrescriptionDto(null, null);
        String json = objectMapper.writeValueAsString(dto);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/prescriptions")
                .header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testThatCreateNewPrescriptionReturnsHttp201CreatedAndCorrespondingData() throws Exception {
        String jwt = getAccessToken();
        RegisterPatientForm patientForm = TestDataUtil.createRegisterForm();
        userService.register(patientForm);
        PatientEntity patient = PatientEntity.builder().id(1L).build();
        PrescriptionDto dto = TestDataUtil.createPrescriptionDto(patient, null);
        String json = objectMapper.writeValueAsString(dto);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/prescriptions")
                .header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.doctor.firstName").exists());

    }

    @Test
    public void testThatAddMedicineToPrescriptionReturnsHttp403ForbiddenWhenUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/prescriptions/1/medicine"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testThatAddMedicineToPrescriptionReturnsHttp400BadRequestWhenInvalidData() throws Exception {
        String jwt = getAccessToken();
        PrescriptionMedicineDto dto = TestDataUtil.createPrescriptionMedicineDto(null);
        dto.setDosage(null);
        String json = objectMapper.writeValueAsString(dto);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/prescriptions/1/medicine")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testThatAddMedicineToPrescriptionReturnsHttp404NotFoundWhenPrescriptionDoesNotExist() throws Exception {
        String jwt = getAccessToken();
        PrescriptionMedicineDto dto = TestDataUtil.createPrescriptionMedicineDto(null);
        String json = objectMapper.writeValueAsString(dto);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/prescriptions/1/medicine")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testThatAddMedicineToPrescriptionReturnsHttp201CreatedAndCorrespondingData() throws Exception {
        String jwt = getAccessToken();
        PrescriptionMedicineDto dto = TestDataUtil.createPrescriptionMedicineDto(null);
        service.createPrescription(PrescriptionEntity.builder().build(), jwt);
        String json = objectMapper.writeValueAsString(dto);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/prescriptions/1/medicine")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void testThatGetAllMedicineToPrescriptionReturnsHttp403ForbiddenIfUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/prescriptions/1/medicine"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testThatGetAllMedicineToPrescriptionReturnsHttp200OkAndCorrespondingData() throws Exception {
        String jwt = getAccessToken();
        PrescriptionEntity prescription = service.createPrescription(PrescriptionEntity.builder().build(), jwt);
        PrescriptionMedicineEntity medicineEntity = service.addMedicineToPrescription(prescription.getId(),
                PrescriptionMedicineEntity.builder()
                        .dosage("DOSAGE")
                        .medicineName("NAME")
                        .build());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/prescriptions/1/medicine")
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(medicineEntity.getId()));
    }

    @Test
    public void testThatUpdateMedicineInstructionsByIdReturnsHttp403ForbiddenIfUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/prescriptions/medicine/1"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testThatUpdateMedicineInstructionsReturnsHttp200OkAndCorrespondingData() throws Exception {
        String jwt = getAccessToken();
        PrescriptionEntity prescription = service.createPrescription(PrescriptionEntity.builder().build(), jwt);
        PrescriptionMedicineEntity medicineEntity = service.addMedicineToPrescription(prescription.getId(),
                PrescriptionMedicineEntity.builder()
                        .dosage("DOSAGE")
                        .medicineName("NAME")
                        .build());
        PrescriptionMedicineDto update = PrescriptionMedicineDto.builder()
                .medicineName("NAME")
                        .instructions("INSTRUCTIONS")
                        .dosage("UPDATED").build();
        String json = objectMapper.writeValueAsString(update);
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/prescriptions/medicine/1")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.dosage").value(update.getDosage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.instructions").value(update.getInstructions()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.medicineName").value(medicineEntity.getMedicineName()));
    }

    @Test
    public void testThatRemoveMedicineFromPrescriptionByIdReturnsHttp403ForbiddenIfUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/prescriptions/medicine/1"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testThatRemoveMedicineFromPrescriptionByIdReturnsHttp202Accepted() throws Exception {
        String jwt = getAccessToken();
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/prescriptions/medicine/1")
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(MockMvcResultMatchers.status().isAccepted());
    }

    @Test
    public void testThatDeletePrescriptionReturnsHttp403ForbiddenIfUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/prescriptions/1"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testThatDeletePrescriptionReturnsHttp202Accepted() throws Exception {
        String accessToken = getAccessToken();
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/prescriptions/1")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isAccepted());
    }

    private String getAccessToken() throws Exception {
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
        return objectMapper.readValue(token, JwtToken.class).getAccessToken();
    }

}
