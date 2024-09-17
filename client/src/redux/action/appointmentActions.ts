import { createAsyncThunk } from "@reduxjs/toolkit";
import { SERVER_API } from "../api/utils";
import { AxiosError } from "axios";
// todo: 404 error handling if doctor/patient doesn't exist
export const setAppointmentsForDoctor = createAsyncThunk(
  "appointment/setAppointmentsForDoctor",
  async (doctorId: number, { rejectWithValue }) => {
    try {
      const response = await SERVER_API.get(
        `/doctors/${doctorId}/appointments`
      );
      if (response.status === 200) {
        return response.data;
      }
    } catch (err) {
      const axiosError = err as AxiosError;
      if (axiosError.response?.status === 403) {
        return rejectWithValue({
          status: 403,
          message: "You don't have permission to access this page.",
        });
      }
      console.log(err);
    }
  }
);

export const setAppointmentsForPatient = createAsyncThunk(
  "appointment/setAppointmentsForPatient",
  async (patientId: number, { rejectWithValue }) => {
    try {
      const response = await SERVER_API.get(
        `/patients/${patientId}/appointments`
      );
      if (response.status === 200) {
        return response.data;
      }
    } catch (err) {
      const axiosError = err as AxiosError;
      if (axiosError.response?.status === 403) {
        return rejectWithValue({
          status: 403,
          message: "You don't have permission to access this page.",
        });
      }
      console.log(err);
    }
  }
);

export const patchAppointment = createAsyncThunk(
  "appointment/patchAppointment",
  async (
    values: {
      id: number;
      appointmentDate?: Date;
      status: "SCHEDULED" | "COMPLETED" | "CANCELED";
      reason: string;
    },
    { rejectWithValue }
  ) => {
    try {
      const response = await SERVER_API.patch(
        `/patients/appointments/${values.id}`,
        values
      );
      if (response.status === 200) {
        return { data: response.data, action: "PATCH" };
      }
      if (response.status === 201) {
        return { data: response.data, action: "POST", lastId: values.id };
      }
      if (response.status == 202) {
        return { deleteId: values.id, action: "DELETE" };
      }
    } catch (err) {
      const axiosError = err as AxiosError;
      if (axiosError.response?.status == 400) {
        return rejectWithValue({
          status: 400,
          message: (axiosError.response?.data as { message: string }).message,
        });
      } else if (axiosError.response?.status == 403) {
        return rejectWithValue({
          status: 403,
          message: "You don't have permission to perform this action.",
        });
      } else if (axiosError.response?.status === 404) {
        return rejectWithValue({
          status: 404,
          message: (axiosError.response?.data as { message: string }).message,
        });
      }
    }
  }
);

export const deleteAppointment = createAsyncThunk(
  "appointment/deleteAppointment",
  async (id: number, { rejectWithValue }) => {
    try {
      const response = await SERVER_API.delete(`/patients/appointments/${id}`);
      if (response.status === 202) {
        return id;
      }
    } catch (err) {
      const axiosError = err as AxiosError;
      if (axiosError.response?.status === 403) {
        return rejectWithValue({
          status: 403,
          message: "You don't have permission to access this page.",
        });
      }
    }
  }
);
