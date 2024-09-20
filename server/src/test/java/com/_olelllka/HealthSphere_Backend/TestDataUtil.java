package com._olelllka.HealthSphere_Backend;

import com._olelllka.HealthSphere_Backend.domain.documents.MedicalRecordDocument;
import com._olelllka.HealthSphere_Backend.domain.dto.appointments.AppointmentDto;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.LoginForm;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.RegisterDoctorForm;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.RegisterPatientForm;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.UserDto;
import com._olelllka.HealthSphere_Backend.domain.dto.doctors.DoctorDetailDto;
import com._olelllka.HealthSphere_Backend.domain.dto.patients.PatientDto;
import com._olelllka.HealthSphere_Backend.domain.dto.prescriptions.PrescriptionDto;
import com._olelllka.HealthSphere_Backend.domain.dto.prescriptions.PrescriptionMedicineDto;
import com._olelllka.HealthSphere_Backend.domain.dto.records.MedicalRecordDetailDto;
import com._olelllka.HealthSphere_Backend.domain.entity.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.Date;
import java.util.List;

public class TestDataUtil {

    public static RegisterPatientForm createRegisterForm() throws ParseException {
        return RegisterPatientForm.builder()
                .email("patient@email.com")
                .password("password123")
                .firstName("First Name")
                .lastName("Last Name")
                .gender(Gender.MALE)
                .dateOfBirth(new SimpleDateFormat("yyyy-MM-dd").parse("2020-04-12")).build();
    }

    public static LoginForm createLoginForm() {
        return LoginForm.builder()
                .email("email@email.com")
                .password("password123")
                .build();
    }

    public static RegisterDoctorForm createRegisterDoctorForm() {
        return RegisterDoctorForm.builder()
                .email("doctor@email.com")
                .password("password123")
                .licenseNumber("12341090189123")
                .phoneNumber("123456789")
                .clinicAddress("West 12 St.")
                .firstName("First Name")
                .lastName("Last Name")
                .gender(Gender.FEMALE)
                .specializations(List.of())
                .build();
    }

    public static PatientDto createPatientDto(UserDto userDto) throws ParseException{
        return PatientDto.builder()
                .user(userDto)
                .firstName("First Name")
                .lastName("Last Name")
                .gender(Gender.MALE)
                .address("Address Address")
                .dateOfBirth(new SimpleDateFormat("dd-MM-yyyy").parse("12-04-2004"))
                .phoneNumber("14123412341234")
                .build();
    }

    public static DoctorDetailDto createDoctorDetailDto(UserDto userDto) {
        return DoctorDetailDto.builder()
                .user(userDto)
                .firstName("Doctor First")
                .lastName("Doctor Last")
                .clinicAddress("Address")
                .experienceYears(2L)
                .specializations(List.of())
                .licenseNumber("12390123123")
                .phoneNumber("13422341234")
                .build();
    }

    public static UserDto createUserDto() {
        return UserDto.builder()
                .email("email@email.com")
                .role(Role.ROLE_PATIENT)
                .build();
    }

    public static PrescriptionDto createPrescriptionDto(PatientEntity patient, DoctorEntity doctor) {
        return PrescriptionDto.builder()
                .id(1L)
                .patient(patient)
                .doctor(doctor)
                .build();
    }

    public static PrescriptionMedicineDto createPrescriptionMedicineDto(PrescriptionEntity entity) {
        return PrescriptionMedicineDto.builder()
                .id(1L)
                .prescription(entity)
                .instructions("INSTRUCTIONS")
                .medicineName("NAME")
                .dosage("DOSAGE")
                .build();
    }

    public static MedicalRecordDetailDto createMedicalRecordDetailDto(PatientDto patientDto, DoctorDetailDto doctorDetailDto) {
        return MedicalRecordDetailDto.builder()
                .patient(patientDto)
                .diagnosis("Diagnosis")
                .treatment("Treatment")
                .recordDate(LocalDate.of(2020, Month.APRIL, 1)).build();
    }

    public static AppointmentDto createAppointmentDto(PatientEntity patient, DoctorEntity doctor) throws ParseException{
        return AppointmentDto.builder()
                .appointmentDate(new SimpleDateFormat("yyyy-MM-dd").parse("2025-01-01"))
                .reason("REASON")
                .doctor(doctor)
                .patient(patient)
                .status(Status.SCHEDULED)
                .build();
    }
}
