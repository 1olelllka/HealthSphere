import { createAsyncThunk } from "@reduxjs/toolkit";
import { SERVER_API } from "../api/utils";
import { AxiosError } from "axios";

export const getAllDoctors = createAsyncThunk(
  "doctors/getAllDoctors",
  async (params: { params: string; page: number }, { rejectWithValue }) => {
    try {
      const response = await SERVER_API.get(
        `/doctors?search=${params.params}&page=${params.page}`
      );
      if (response.status === 200) {
        return response.data;
      }
    } catch (err) {
      const axiosError = err as AxiosError;
      return rejectWithValue({
        status: axiosError.response?.status,
        message: axiosError.response?.data,
      });
    }
  }
);
