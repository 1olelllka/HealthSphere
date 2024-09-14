import { createAsyncThunk } from "@reduxjs/toolkit";
import { SERVER_API } from "../api/utils";
import { AxiosError } from "axios";
import { Medicine } from "../reducers/medicineReducer";

export const setMedicineForPrescription = createAsyncThunk(
  "medicine/setMedicineForPrescription",
  async (id: number, { rejectWithValue }) => {
    try {
      const response = await SERVER_API.get(`/prescriptions/${id}/medicine`);
      if (response.status === 200) {
        return response.data;
      }
    } catch (err) {
      const axiosError = err as AxiosError;
      if (axiosError.response?.status == 404) {
        return rejectWithValue({
          status: 404,
          message: "Prescription with such id was not found.",
        });
      }
    }
  }
);

export const addMedicineToPrescription = createAsyncThunk(
  "medicine/addMedicineToPrescription",
  async (values: Medicine, { rejectWithValue }) => {
    console.log(values);
    try {
      const response = await SERVER_API.post(
        `/prescriptions/${values.id}/medicine`,
        values
      );
      if (response.status === 201) {
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
      } else if (axiosError.response?.status == 404) {
        return rejectWithValue({
          status: 404,
          message: "Prescription with such id was not found.",
        });
      }
      console.log(err);
    }
  }
);

export const updateMedicineForPrescription = createAsyncThunk(
  "medicine/updateMedicineForPrescription",
  async (values: Medicine, { rejectWithValue }) => {
    try {
      const response = await SERVER_API.patch(
        `prescriptions/medicine/${values.id}`,
        values
      );
      if (response.status == 200) {
        return response.data;
      }
    } catch (err) {
      const axiosError = err as AxiosError;
      if (axiosError.response?.status == 404) {
        return rejectWithValue({
          status: 404,
          message: "Medicament with such id was not found.",
        });
      } else if (axiosError.response?.status == 400) {
        return rejectWithValue({
          status: 400,
          message: (axiosError.response?.data as { message: string })
            ?.message as string,
        });
      }
    }
  }
);

export const deleteMedicineFromPrescription = createAsyncThunk(
  "medicine/deleteMedicineFromPrescription",
  async (id: number, { rejectWithValue }) => {
    try {
      const response = await SERVER_API.delete("/prescriptions/medicine/" + id);
      if (response.status == 202) {
        return id;
      }
    } catch (err) {
      const axiosError = err as AxiosError;
      if (axiosError.response?.status == 403) {
        return rejectWithValue({
          status: 403,
          message: "You are not allowed to delete this medicine.",
        });
      }
    }
  }
);
