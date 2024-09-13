import { createAsyncThunk } from "@reduxjs/toolkit";
import { SERVER_API } from "../api/utils";

export const getAllDoctors = createAsyncThunk(
  "doctors/getAllDoctors",
  async (params: { params: string; page: number }) => {
    try {
      const response = await SERVER_API.get(
        `/doctors?search=${params.params}&page=${params.page}`
      );
      if (response.status === 200) {
        return response.data;
      }
    } catch (err) {
      console.log(err);
    }
  }
);
