import { createAsyncThunk } from "@reduxjs/toolkit";
import { SERVER_API } from "../api/utils";
import { AxiosError } from "axios";

export const setRecord = createAsyncThunk(
  "record/setRecord",
  async (values: { id: number; page: number }) => {
    try {
      const response = await SERVER_API.get(
        `patient/${values.id}/medical-records?page=${values.page}`
      );
      if (response.status === 200) {
        return response.data;
      }
    } catch (err) {
      console.log(err);
    }
  }
);

export const searchRecord = createAsyncThunk(
  "record/searchRecord",
  async (values: {
    id: number;
    diagnosis?: string | undefined;
    from?: Date | undefined;
    to?: Date | undefined;
    page: number;
  }) => {
    let from = "";
    let to = "";
    let diagnosis = "";
    if (values.from != undefined) {
      from = values.from.toISOString().substring(0, 10);
    }
    if (values.to != undefined) {
      to = values.to.toISOString().substring(0, 10);
    }
    if (values.diagnosis != undefined) {
      diagnosis = values.diagnosis;
    }
    try {
      const response = await SERVER_API.get(
        `patient/${values.id}/medical-records?diagnosis=${diagnosis}&from=${from}&to=${to}&page=${values.page}`
      );
      if (response.status === 200) {
        return response.data;
      }
    } catch (err) {
      console.log(err);
    }
  }
);

export const setDetailedRecord = createAsyncThunk(
  "record/setDetailedRecord",
  async (id: number, { rejectWithValue }) => {
    try {
      const response = await SERVER_API.get(`patient/medical-records/${id}`);
      if (response.status === 200) {
        return response.data;
      } else if (response.status == 403) {
        return rejectWithValue({
          status: response.status,
          message: "Forbidden",
        });
      }
    } catch (err) {
      console.log(err);
      return rejectWithValue({
        status: 403,
        message: "Forbidden",
      });
    }
  }
);

export const createPrescriptionForRecord = createAsyncThunk(
  "record/createPrescriptionForRecord",
  async (
    values: {
      id: number;
      patient: { id: number };
      diagnosis: string;
      recordDate: string;
    },
    { rejectWithValue }
  ) => {
    try {
      const createdPrescription = await SERVER_API.post(
        "/prescriptions",
        values
      );
      const updatedValues = {
        recordDate: values.recordDate,
        diagnosis: values.diagnosis,
        prescription: {
          id: createdPrescription.data.id,
        },
      };
      const patchedMedicalRecord = await SERVER_API.patch(
        "/patient/medical-records/" + values.id,
        updatedValues
      );
      if (
        createdPrescription.status == 201 &&
        patchedMedicalRecord.status == 200
      ) {
        return patchedMedicalRecord.data;
      } else {
        return rejectWithValue({
          status: 500,
          message: "An error occurred. Please try again later.",
        });
      }
    } catch (err) {
      const axiosError = err as AxiosError;
      if (axiosError.response?.status == 403) {
        return rejectWithValue({
          status: 403,
          message: (axiosError.response?.data as { message: string })
            ?.message as string,
        });
      }
    }
  }
);

export const updateMedicalRecord = createAsyncThunk(
  "record/updateMedicalRecord",
  async (
    values: {
      id: number;
      diagnosis: string;
      treatment?: string;
      recordDate?: string;
    },
    { rejectWithValue }
  ) => {
    try {
      const response = await SERVER_API.patch(
        `/patient/medical-records/${values.id}`,
        values
      );
      if (response.status == 200) {
        console.log(response.data);
        return response.data;
      }
    } catch (err) {
      const axiosError = err as AxiosError;
      if (axiosError.response?.status == 400) {
        return rejectWithValue({
          status: 400,
          message: (axiosError.response?.data as { message: string })
            ?.message as string,
        });
      } else if (axiosError.response?.status == 403) {
        console.log(axiosError);
        return rejectWithValue({
          status: 403,
          message: (axiosError.response?.data as { message: string })
            ?.message as string,
        });
      } else if (axiosError.response?.status == 404) {
        return rejectWithValue({
          status: 404,
          message: (axiosError.response?.data as { message: string })
            ?.message as string,
        });
      }
      console.log(err);
    }
  }
);
