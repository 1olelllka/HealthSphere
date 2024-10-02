package com._olelllka.HealthSphere_Backend.controllers;

import com._olelllka.HealthSphere_Backend.AbstractTestContainers;
import com._olelllka.HealthSphere_Backend.TestDataUtil;
import com._olelllka.HealthSphere_Backend.domain.dto.JwtToken;
import com._olelllka.HealthSphere_Backend.domain.dto.appointments.AppointmentDto;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.LoginForm;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.RegisterDoctorForm;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.RegisterPatientForm;
import com._olelllka.HealthSphere_Backend.domain.entity.AppointmentEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.DoctorEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.Status;
import com._olelllka.HealthSphere_Backend.service.AppointmentService;
import com._olelllka.HealthSphere_Backend.service.UserService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@Testcontainers
public class AppointmentControllerIntegrationTest extends AbstractTestContainers {

    private MockMvc mockMvc;
    private AppointmentService service;
    private ObjectMapper objectMapper;
    private UserService userService;

    @Autowired
    public AppointmentControllerIntegrationTest(MockMvc mockMvc,
                                                AppointmentService service,
                                                UserService userService) {
        this.mockMvc = mockMvc;
        this.service = service;
        this.userService = userService;
        this.objectMapper = new ObjectMapper();
    }

    @Test
    public void testThatGetAllAppointmentsForPatientReturnsHttp403ForbiddenIfUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/appointments/patients/1"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testThatGetAllAppointmentsForPatientReturnsHttp200Ok() throws Exception {
        String accessToken = getAccessToken();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/appointments/patients/1")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testThatGetAllAppointmentsForDoctorReturnsHttp200Ok() throws Exception {
        String accessToken = getDoctorAccessToken();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/appointments/doctors/1")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testThatGetAppointmentByIdReturnsHttp404NotFoundIfSuchAppointmentWasNotFound() throws Exception {
        String accessToken = getAccessToken();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/appointments/1")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testThatCreateAppointmentForPatientOrDoctorReturnsHttp400BadRequestIfInvalidData() throws Exception {
        String accessToken = getAccessToken();
        AppointmentDto dto = TestDataUtil.createAppointmentDto(null, null);
        dto.setAppointmentDate(new SimpleDateFormat("yyyy-MM-dd").parse("2020-02-02"));
        String json = objectMapper.writeValueAsString(dto);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/appointments")
                .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testThatCreateAppointmentForPatientReturnsHttp409ConflictIfAppointmentHasTheSamePatientInTheSameDate() throws Exception {
        String accessToken = getAccessToken();
        getDoctorAccessToken();
        AppointmentDto dto = TestDataUtil.createAppointmentDto(null, DoctorEntity.builder().id(1L).build());
        String json = objectMapper.writeValueAsString(dto);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/appointments")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/appointments")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void testThatCreateAppointmentForPatientReturnsHttp201CreatedAndCorrespondingData() throws Exception {
        String accessToken = getAccessToken();
        getDoctorAccessToken();
        AppointmentDto dto = TestDataUtil.createAppointmentDto(null, DoctorEntity.builder().id(1L).build());
        String json = objectMapper.writeValueAsString(dto);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/appointments")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(Status.SCHEDULED.name()));
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    public void testThatCreateAppointmentForDoctorReturnsHttp409ConflictIfAppointmentHasTheSameDoctorInTheSameDate() throws Exception {
        String accessToken = getDoctorAccessToken();
        AppointmentDto dto = TestDataUtil.createAppointmentDto(null, null);
        String json = objectMapper.writeValueAsString(dto);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/appointments")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/appointments")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    public void testThatCreateAppointmentForDoctorReturnsHttp201CreatedAndCorrespondingData() throws Exception {
        String accessToken = getDoctorAccessToken();
        AppointmentDto dto = TestDataUtil.createAppointmentDto(null, null);
        String json = objectMapper.writeValueAsString(dto);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/appointments")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(Status.SCHEDULED.name()));
    }

    @Test
    public void testThatPatchAppointmentReturnsHttp400BadRequestIfDataIsInvalid() throws Exception {
        String accessToken = getAccessToken();
        AppointmentDto dto = AppointmentDto
                .builder()
                .appointmentDate(new SimpleDateFormat("yyyy-MM-dd").parse("2024-01-01"))
                .build();
        String json = objectMapper.writeValueAsString(dto);
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/appointments/1")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testThatPatchAppointmentReturnsHttp404NotFoundIfAppointmentWasNotFound() throws Exception {
        String accessToken = getAccessToken();
        AppointmentDto dto = AppointmentDto
                .builder()
                .appointmentDate(new SimpleDateFormat("yyyy-MM-dd").parse("2025-10-01"))
                .status(Status.SCHEDULED)
                .build();
        String json = objectMapper.writeValueAsString(dto);
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/appointments/1")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testThatPatchAppointmentReturnsHttp200OkAndCorrespondingDataIfChangedReasonOrStatus() throws Exception {
        String accessToken = getAccessToken();
        getDoctorAccessToken();
        AppointmentEntity entity = AppointmentEntity.builder()
                .doctor(DoctorEntity.builder().id(1L).build())
                .appointmentDate(new SimpleDateFormat("yyyy-MM-dd").parse("2024-09-01"))
                .build();
        service.createNewAppointmentForPatient(entity, accessToken);
        AppointmentDto dto = AppointmentDto
                .builder()
                .status(Status.COMPLETED)
                .build();
        String json = objectMapper.writeValueAsString(dto);
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/appointments/1")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(Status.COMPLETED.name()));
    }

    @Test
    public void testThatPatchAppointmentReturnsHttp204AcceptedIfStatusIsCanceled() throws Exception {
        String accessToken = getAccessToken();
        getDoctorAccessToken();
        AppointmentEntity entity = AppointmentEntity.builder()
                .doctor(DoctorEntity.builder().id(1L).build())
                .appointmentDate(new SimpleDateFormat("yyyy-MM-dd").parse("2024-09-01"))
                .build();
        service.createNewAppointmentForPatient(entity, accessToken);
        AppointmentDto dto = AppointmentDto
                .builder()
                .status(Status.CANCELED)
                .build();
        String json = objectMapper.writeValueAsString(dto);
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/appointments/1")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isAccepted());
    }

    @Test
    public void testThatPatchAppointmentAsPatientReturnsHttp201CreatedAndCorrespondingDataIfChangedDate() throws Exception {
        String accessToken = getAccessToken();
        getDoctorAccessToken();
        AppointmentEntity entity = AppointmentEntity.builder()
                .doctor(DoctorEntity.builder().id(1L).build())
                .appointmentDate(new SimpleDateFormat("yyyy-MM-dd").parse("2025-09-01"))
                .build();
        service.createNewAppointmentForPatient(entity, accessToken);
        AppointmentDto dto = AppointmentDto
                .builder()
                .appointmentDate(new SimpleDateFormat("yyyy-MM-dd").parse("2025-10-01"))
                .build();
        String json = objectMapper.writeValueAsString(dto);
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/appointments/1")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                // this will show that it's newly created appointment.
                .andExpect(MockMvcResultMatchers.jsonPath("$.patient").hasJsonPath())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(2L));
    }

    @Test
    public void testThatPatchAppointmentAsDoctorReturnsHttp201CreatedAndCorrespondingDataIfChangedDate() throws  Exception {
        String accessToken = getDoctorAccessToken();
        AppointmentEntity entity = AppointmentEntity.builder()
                .doctor(DoctorEntity.builder().id(1L).build())
                .appointmentDate(new SimpleDateFormat("yyyy-MM-dd").parse("2025-09-01"))
                .build();
        service.createNewAppointmentForDoctor(entity, accessToken);
        AppointmentDto dto = AppointmentDto
                .builder()
                .appointmentDate(new SimpleDateFormat("yyyy-MM-dd").parse("2025-10-01"))
                .build();
        String json = objectMapper.writeValueAsString(dto);
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/appointments/1")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                // this will show that it's newly created appointment.
                .andExpect(MockMvcResultMatchers.jsonPath("$.patient").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(2L));
    }

    @Test
    public void testThatCreatingTheAppointmentToTheSameDoctorAtTheSameTimeReturnsHttp409Conflict() throws Exception {
        String accessToken = getAccessToken();
        getDoctorAccessToken();
        AppointmentDto dto = TestDataUtil.createAppointmentDto(null, DoctorEntity.builder().id(1L).build());
        String json = objectMapper.writeValueAsString(dto);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/appointments")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(Status.SCHEDULED.name()));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/appointments")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isConflict());
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
        String token = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/jwt")
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
        String token = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/jwt")
                        .cookie(cookieToken))
                .andReturn().getResponse().getContentAsString();
        String accessToken = objectMapper.readValue(token, JwtToken.class).getAccessToken();
        return accessToken;
    }

}
