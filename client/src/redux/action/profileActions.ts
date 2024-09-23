import { createAsyncThunk } from "@reduxjs/toolkit";
import { SERVER_API } from "../api/utils";
import { Spezialization } from "../reducers/profileReducer";
import axios, { AxiosError } from "axios";

export const setProfile = createAsyncThunk(
  "profile/setProfile",
  async (_, { rejectWithValue }) => {
    try {
      const doctorResponse = await SERVER_API.get("/doctors/me");
      if (doctorResponse.status === 200) {
        return doctorResponse.data;
      }
    } catch (err) {
      console.log("DOCTOR ERROR:", err);
      try {
        const patientResponse = await SERVER_API.get("/patients/me");
        if (patientResponse.status === 200) {
          return patientResponse.data;
        }
      } catch (err) {
        console.log(err);
      }
    }
    return rejectWithValue({ status: 403, message: "Forbidden" });
  }
);
export const patchPatientProfile = createAsyncThunk(
  "profile/patchPatientProfile",
  async (values: {
    firstName: string;
    lastName: string;
    address: string;
    phoneNumber: string;
    dateOfBirth: string;
    allergies?: string;
  }) => {
    try {
      const response = await SERVER_API.patch("/patients/me", values, {
        headers: {
          "Content-Type": "application/json",
        },
      });
      if (response.status === 200) {
        return response.data;
      }
    } catch (err) {
      console.log(err);
    }
  }
);

export const patchDoctorProfile = createAsyncThunk(
  "profile/patchDoctorProfile",
  async (
    values: {
      firstName: string;
      lastName: string;
      clinicAddress: string;
      phoneNumber: string;
      experienceYears: number;
      licenseNumber: string;
      specializations?: Spezialization[] | null;
    },
    { rejectWithValue }
  ) => {
    try {
      const response = await SERVER_API.patch("/doctors/me", values, {
        headers: {
          "Content-Type": "application/json",
        },
      });
      if (response.status === 200) {
        return response.data;
      }
    } catch (err) {
      const error = err as AxiosError;
      if (error.response?.status === 403) {
        return rejectWithValue({
          status: 403,
          message: "You are not allowed to perform this action",
        });
      } else if (error.response?.status == 400) {
        return rejectWithValue({
          status: 400,
          message: (error.response.data as { message: string }).message,
        });
      }
    }
  }
);

export const logoutProfile = createAsyncThunk(
  "profile/logoutProfile",
  async () => {
    try {
      const response = await axios.post(
        "http://localhost:8000/api/v1/logout",
        {},
        {
          withCredentials: true,
        }
      );
      if (response.status === 200) {
        return response.data;
      }
    } catch (err) {
      console.log(err);
    }
  }
);

export const deletePatientProfile = createAsyncThunk(
  "profile/deletePatientProfile",
  async () => {
    try {
      const response = await SERVER_API.delete("/patients/me");
      if (response.status === 202) {
        return "deleted";
      }
    } catch (err) {
      console.log(err);
    }
  }
);

export const deleteDoctorProfile = createAsyncThunk(
  "profile/deleteDoctorProfile",
  async () => {
    try {
      const response = await SERVER_API.delete("/doctors/me");
      if (response.status === 202) {
        return "deleted";
      }
    } catch (err) {
      console.log(err);
    }
  }
);
