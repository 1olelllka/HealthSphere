import { createAsyncThunk } from "@reduxjs/toolkit";
import { SERVER_API } from "../api/utils";
import { AxiosError } from "axios";

export const getAllPatients = createAsyncThunk(
  "patient/getAllPatients",
  async (params: { params: string; page: number }, { rejectWithValue }) => {
    try {
      const response = await SERVER_API.get(
        `/patients?search=${params.params}&page=${params.page}`
      );
      if (response.status === 200) {
        return response.data;
      }
    } catch (err) {
      const axiosError = err as AxiosError;
      if (axiosError.response?.status == 403) {
        return rejectWithValue({
          status: 403,
          message: "Forbidden",
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
