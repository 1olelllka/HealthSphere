import { createAsyncThunk } from "@reduxjs/toolkit";
import { SERVER_API } from "../api/utils";

export const setSpecialization = createAsyncThunk(
  "specialization/setSpecialization",
  async () => {
    try {
      const response = await SERVER_API.get("/specializations");
      if (response.status === 200) {
        return response.data;
      }
    } catch (err) {
      console.log(err);
    }
  }
);
