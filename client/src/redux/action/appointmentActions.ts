import { createAsyncThunk } from "@reduxjs/toolkit";
import { SERVER_API } from "../api/utils";
import { AxiosError } from "axios";

export const setAppointmentsForDoctor = createAsyncThunk(
  "appointment/setAppointmentsForDoctor",
  async (
    values: { doctorId: number; from?: Date; to?: Date; page?: number },
    { rejectWithValue }
  ) => {
    try {
      if (values.from == undefined || values.to == undefined) {
        const response = await SERVER_API.get(
          `/appointments/doctors/${values.doctorId}?page=${values.page}`
        );
        if (response.status === 200) {
          return response.data;
        }
      } else {
        const response = await SERVER_API.get(
          `/appointments/doctors/${values.doctorId}?from=${values.from
            .toISOString()
            .slice(0, -5)}&to=${values.to.toISOString().slice(0, -5)}`
        );
        if (response.status === 200) {
          return { content: response.data };
        }
      }
    } catch (err) {
      const axiosError = err as AxiosError;
      if (axiosError.response?.status === 403) {
        return rejectWithValue({
          status: 403,
          message: "You don't have permission to access this page.",
        });
      } else if (axiosError.response?.status === 401) {
        return rejectWithValue({
          status: 401,
          message: (axiosError.response.data as { message: string }).message,
        });
      }
      console.log(err);
    }
  }
);

export const setAppointmentsForPatient = createAsyncThunk(
  "appointment/setAppointmentsForPatient",
  async (
    values: { patientId: number; from?: Date; to?: Date; page?: number },
    { rejectWithValue }
  ) => {
    try {
      if (values.from == undefined || values.to == undefined) {
        const response = await SERVER_API.get(
          `/appointments/patients/${values.patientId}?page=${values.page}`
        );
        if (response.status === 200) {
          return response.data;
        }
      } else {
        const response = await SERVER_API.get(
          `/appointments/patients/${values.patientId}?from=${values.from
            .toISOString()
            .slice(0, -5)}&to=${values.to.toISOString().slice(0, -5)}`
        );
        if (response.status === 200) {
          return { content: response.data };
        }
      }
    } catch (err) {
      const axiosError = err as AxiosError;
      if (axiosError.response?.status === 403) {
        return rejectWithValue({
          status: 403,
          message: "You don't have permission to access this page.",
        });
      } else if (axiosError.response?.status === 401) {
        return rejectWithValue({
          status: 401,
          message: (axiosError.response.data as { message: string }).message,
        });
      }
      console.log(err);
    }
  }
);

export const createAppointment = createAsyncThunk(
  "appointment/createAppointmentByPatient",
  async (
    values: {
      doctorId?: number;
      appointmentDate?: string;
      status: string;
      reason?: string;
      patientId?: number;
    },
    { rejectWithValue }
  ) => {
    try {
      let data = {};
      if (values.doctorId) {
        data = {
          ...values,
          doctor: {
            id: values.doctorId,
          },
        };
      } else {
        data = {
          ...values,
          patient: {
            id: values.patientId,
          },
        };
      }
      console.log(data);
      const response = await SERVER_API.post("/appointments", data);
      if (response.status === 201) {
        return response.data;
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
      } else if (axiosError.response?.status === 401) {
        return rejectWithValue({
          status: 401,
          message: (axiosError.response.data as { message: string }).message,
        });
      } else if (axiosError.response?.status === 409) {
        return rejectWithValue({
          status: 409,
          message: (axiosError.response.data as { message: string }).message,
        });
      }
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
        `/appointments/${values.id}`,
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
      } else if (axiosError.response?.status === 401) {
        return rejectWithValue({
          status: 401,
          message: (axiosError.response.data as { message: string }).message,
        });
      }
    }
  }
);

export const deleteAppointment = createAsyncThunk(
  "appointment/deleteAppointment",
  async (id: number, { rejectWithValue }) => {
    try {
      const response = await SERVER_API.patch(`/appointments/${id}`, {
        status: "CANCELED",
      });
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
      } else if (axiosError.response?.status === 401) {
        return rejectWithValue({
          status: 401,
          message: (axiosError.response.data as { message: string }).message,
        });
      }
    }
  }
);
