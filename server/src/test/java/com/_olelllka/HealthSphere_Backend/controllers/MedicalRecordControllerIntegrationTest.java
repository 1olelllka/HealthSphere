package com._olelllka.HealthSphere_Backend.controllers;

import com._olelllka.HealthSphere_Backend.AbstractTestContainers;
import com._olelllka.HealthSphere_Backend.TestDataUtil;
import com._olelllka.HealthSphere_Backend.domain.documents.MedicalRecordDocument;
import com._olelllka.HealthSphere_Backend.domain.dto.JwtToken;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.LoginForm;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.RegisterDoctorForm;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.RegisterPatientForm;
import com._olelllka.HealthSphere_Backend.domain.dto.doctors.DoctorDetailDto;
import com._olelllka.HealthSphere_Backend.domain.dto.patients.PatientDto;
import com._olelllka.HealthSphere_Backend.domain.dto.records.MedicalRecordDetailDto;
import com._olelllka.HealthSphere_Backend.domain.entity.*;
import com._olelllka.HealthSphere_Backend.repositories.MedicalRecordElasticRepository;
import com._olelllka.HealthSphere_Backend.service.MedicalRecordService;
import com._olelllka.HealthSphere_Backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
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
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
public class MedicalRecordControllerIntegrationTest extends AbstractTestContainers {

    @BeforeEach
    void initEach() throws ParseException {
        elasticRepository.deleteAll();
        RegisterDoctorForm doctorForm = TestDataUtil.createRegisterDoctorForm();
        doctorForm.setEmail("doctor@email.com");
        doctorForm.setSpecializations(List.of());
        listenerRegistry.getListenerContainer("doctor.post").start();
        userService.doctorRegister(doctorForm);
    }


    private RabbitListenerEndpointRegistry listenerRegistry;
    private RabbitAdmin admin;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private UserService userService;
    private MedicalRecordElasticRepository elasticRepository;
    private MedicalRecordService medicalRecordService;

    @Autowired
    public MedicalRecordControllerIntegrationTest(MockMvc mockMvc,
                                                  UserService userService,
                                                  MedicalRecordElasticRepository elasticRepository,
                                                  MedicalRecordService medicalRecordService,
                                                  RabbitListenerEndpointRegistry listenerRegistry,
                                                  RabbitAdmin admin) {
        this.mockMvc = mockMvc;
        this.userService = userService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.elasticRepository = elasticRepository;
        this.medicalRecordService = medicalRecordService;
        this.listenerRegistry = listenerRegistry;
        this.admin = admin;
    }

    @Test
    public void testThatGetAllMedicalRecordsReturnsHttp403IfAuthorizationHeaderNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/patient/1/medical-records"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testThatGetAllMedicalRecordsReturnsHttp200OkAndCorrespondingData() throws Exception {
        String accessToken = getAccessToken();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/patient/1/medical-records")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testThatGetMedicalRecordsByParamsReturnsHttp200AndCorrespondingData() throws Exception {
        String accessToken = getAccessToken();
        MedicalRecordDocument medicalRecordDocument = MedicalRecordDocument
                .builder()
                .id(1L)
                .user_id(1L)
                .recordDate(LocalDate.of(2020, Month.APRIL, 1))
                .diagnosis("Diagnosis").build();
        // I used repository not service as it's easier and faster in terms of configuration here.
        elasticRepository.save(medicalRecordDocument);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/patient/1/medical-records?diagnosis=Diagnosis&from=2020-01-01&to=2020-09-09")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").exists());
    }

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

    @Test
    public void testThatGetMedicalRecordsReturnsHttp200OkIfEverythingGood() throws Exception {
        String patientAccessToken = getAccessToken();
        String patientJson = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/patients/me")
                        .header("Authorization", "Bearer " + patientAccessToken))
                .andReturn().getResponse().getContentAsString();
        PatientEntity patient = objectMapper.readValue(patientJson, PatientEntity.class);
        String doctorAccessToken = getAccessTokenForDoctor();
        String doctorJson = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/doctors/me")
                        .header("Authorization", "Bearer " + doctorAccessToken))
                .andReturn().getResponse().getContentAsString();
        MedicalRecordEntity record = MedicalRecordEntity.builder()
                .id(30L)
                .patient(patient)
                .diagnosis("Diagnosis")
                .treatment("Treatment")
                .recordDate(LocalDate.of(2020, Month.APRIL, 1)).build();
        medicalRecordService.createMedicalRecord(record, doctorAccessToken);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/patient/medical-records/1")
                .header("Authorization", "Bearer " + patientAccessToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.diagnosis").value(record.getDiagnosis()));
    }

    @Test
    @WithMockUser(roles = {"PATIENT"})
    public void testThatCreateNewMedicalRecordReturnsHttp403ForbiddenIfUserIsUnauthorizedOrHasAWrongRole() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/patient/medical-records"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testThatCreateNewMedicalRecordReturnsHttp400BadRequestIfDataIsInvalid() throws Exception {
        MedicalRecordDetailDto dto = MedicalRecordDetailDto.builder().build();
        String dtoJson = objectMapper.writeValueAsString(dto);
        String accessToken = getAccessTokenForDoctor();
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/patient/medical-records")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(dtoJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @Order(1)
    public void testThatCreateNewMedicalRecordReturnsHttp201CreatedIfEverythingIsGood() throws Exception {
        listenerRegistry.stop();
        assertEquals(0, Objects.requireNonNull(admin.getQueueInfo("medical_record_create_update")).getMessageCount());
        listenerRegistry.getListenerContainer("medical-record.post").start();
        String patientAccessToken = getAccessToken();
        String patientJson = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/patients/me")
                .header("Authorization", "Bearer " + patientAccessToken))
                .andReturn().getResponse().getContentAsString();
        PatientEntity patient = objectMapper.readValue(patientJson, PatientEntity.class);
        PatientDto patientDto = PatientDto.builder().id(patient.getId()).build();
        String doctorAccessToken = getAccessTokenForDoctor();
        String doctorJson = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/doctors/me")
                .header("Authorization", "Bearer " + doctorAccessToken))
                .andReturn().getResponse().getContentAsString();
        DoctorEntity doctor = objectMapper.readValue(doctorJson, DoctorEntity.class);
        DoctorDetailDto doctorDto = DoctorDetailDto.builder()
                .id(doctor.getId()).build();
        MedicalRecordDetailDto record = TestDataUtil.createMedicalRecordDetailDto(patientDto, doctorDto);
        String recordJson = objectMapper.writeValueAsString(record);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/patient/medical-records")
                .header("Authorization", "Bearer " + doctorAccessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(recordJson))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.recordDate").exists());
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .until(() -> admin.getQueueInfo("medical_record_create_update").getMessageCount() == 0, Boolean.TRUE::equals);
        assertTrue(elasticRepository.existsById(1L));
    }

    @Test
    @WithMockUser(roles = {"PATIENT"})
    public void testThatPatchDetailedMedicalRecordReturnsHttp403ForbiddenIfUserIsUnauthorizedOrWrongRole() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/patient/medical-records/1"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testThatPatchDetailedMedicalRecordReturnsHttp400BadRequestIfDataIsInvalid() throws Exception {
        MedicalRecordDetailDto dto = TestDataUtil.createMedicalRecordDetailDto(null, null);
        dto.setDiagnosis("");
        String dtoJson = objectMapper.writeValueAsString(dto);
        String accessToken = getAccessTokenForDoctor();
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/patient/medical-records/1")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(dtoJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testThatPatchDetailedMedicalRecordReturnsHttp404NotFoundIfSuchMedicalRecordDoesNotExist() throws Exception {
        MedicalRecordDetailDto dto = TestDataUtil.createMedicalRecordDetailDto(null, null);
        String dtoJson = objectMapper.writeValueAsString(dto);
        String accessToken = getAccessTokenForDoctor();
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/patient/medical-records/1")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dtoJson))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @Order(2)
    public void testThatPatchDetailedMedicalRecordReturnsHttp200OkAndCorrespondingDataIfEverythingIsOk() throws Exception {
        listenerRegistry.stop();
        assertEquals(0, Objects.requireNonNull(admin.getQueueInfo("medical_record_create_update")).getMessageCount());
        listenerRegistry.getListenerContainer("medical-record.post").start();
        String patientAccessToken = getAccessToken();
        String patientJson = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/patients/me")
                        .header("Authorization", "Bearer " + patientAccessToken))
                .andReturn().getResponse().getContentAsString();
        PatientEntity patient = objectMapper.readValue(patientJson, PatientEntity.class);
        String doctorAccessToken = getAccessTokenForDoctor();
        String doctorJson = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/doctors/me")
                        .header("Authorization", "Bearer " + doctorAccessToken))
                .andReturn().getResponse().getContentAsString();
        DoctorEntity doctor = objectMapper.readValue(doctorJson, DoctorEntity.class);
        MedicalRecordEntity record = MedicalRecordEntity.builder()
                .id(30L)
                .doctor(doctor)
                .patient(patient)
                .diagnosis("Diagnosis")
                .treatment("Treatment")
                .recordDate(LocalDate.of(2020, Month.APRIL, 1)).build();
        medicalRecordService.createMedicalRecord(record, doctorAccessToken);
        MedicalRecordDetailDto dto = MedicalRecordDetailDto.builder()
                .diagnosis("DIAGNOSIS")
                .recordDate(LocalDate.of(2020, Month.APRIL, 1))
                .build();
        String dtoJson = objectMapper.writeValueAsString(dto);
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/patient/medical-records/1")
                        .header("Authorization", "Bearer " + doctorAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dtoJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.diagnosis").value("DIAGNOSIS"));
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .until(() -> admin.getQueueInfo("medical_record_create_update").getMessageCount() == 0, Boolean.TRUE::equals);
        assertTrue(elasticRepository.existsById(1L));
    }

    @Test
    @WithMockUser(roles = {"PATIENT"})
    public void testThatDeleteMedicalRecordByIdReturnsHttp403ForbiddenIfUserIsUnauthorizedOrHasWrongRole() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/patient/medical-records/1"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testThatDeleteMedicalRecordReturnsHttp404IfRecordWasNotFound() throws Exception {
        String accessToken = getAccessTokenForDoctor();
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/patient/medical-records/1")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testThatDeleteMedicalRecordReturnsHttp403ForbiddenIfIncorrectDoctor() throws Exception {
        listenerRegistry.stop();
        assertEquals(0, Objects.requireNonNull(admin.getQueueInfo("medical_record_delete")).getMessageCount());
        elasticRepository.save(MedicalRecordDocument.builder().id(1L).build());
        listenerRegistry.getListenerContainer("medical-record.delete").start();

        String patientAccessToken = getAccessToken();
        String patientJson = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/patients/me")
                        .header("Authorization", "Bearer " + patientAccessToken))
                .andReturn().getResponse().getContentAsString();
        PatientEntity patient = objectMapper.readValue(patientJson, PatientEntity.class);
        MedicalRecordEntity record = MedicalRecordEntity.builder()
                .id(30L)
                .patient(patient)
                .diagnosis("Diagnosis")
                .treatment("Treatment")
                .recordDate(LocalDate.of(2020, Month.APRIL, 1)).build();
        String accessToken = getAccessTokenForDoctor();
        medicalRecordService.createMedicalRecord(record, accessToken);
        RegisterDoctorForm doctorForm = TestDataUtil.createRegisterDoctorForm();
        doctorForm.setEmail("e@email.com");
        userService.doctorRegister(doctorForm);
        LoginForm loginForm = TestDataUtil.createLoginForm();
        loginForm.setEmail("e@email.com");
        String loginFormJson = objectMapper.writeValueAsString(loginForm);
        Cookie cookieToken = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginFormJson))
                .andReturn().getResponse().getCookie("accessToken");
        String token = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/get-jwt")
                        .cookie(cookieToken))
                .andReturn().getResponse().getContentAsString();
        String anotherToken = objectMapper.readValue(token, JwtToken.class).getAccessToken();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/patient/medical-records/1")
                        .header("Authorization", "Bearer " + anotherToken))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testThatDeleteMedicalRecordReturnsHttp202Accepted() throws Exception {
        listenerRegistry.stop();
        assertEquals(0, Objects.requireNonNull(admin.getQueueInfo("medical_record_delete")).getMessageCount());
        listenerRegistry.getListenerContainer("medical-record.delete").start();

        String patientAccessToken = getAccessToken();
        String patientJson = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/patients/me")
                        .header("Authorization", "Bearer " + patientAccessToken))
                .andReturn().getResponse().getContentAsString();
        PatientEntity patient = objectMapper.readValue(patientJson, PatientEntity.class);
        MedicalRecordEntity record = MedicalRecordEntity.builder()
                .id(1L)
                .patient(patient)
                .diagnosis("Diagnosis")
                .treatment("Treatment")
                .recordDate(LocalDate.of(2020, Month.APRIL, 1)).build();
        String accessToken = getAccessTokenForDoctor();
        medicalRecordService.createMedicalRecord(record, accessToken);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/patient/medical-records/1")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isAccepted());
        Awaitility.await().atMost(5, TimeUnit.SECONDS)
                .until(() -> admin.getQueueInfo("medical_record_delete").getMessageCount() == 0);
        assertFalse(elasticRepository.existsById(1L));
    }

    private String getAccessTokenForDoctor() throws Exception {
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

    private String getAccessToken() throws Exception {
        RegisterPatientForm register = TestDataUtil.createRegisterForm();
        userService.register(register);
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
        return objectMapper.readValue(token, JwtToken.class).getAccessToken();
    }
}
