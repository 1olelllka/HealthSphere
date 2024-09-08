import { createAsyncThunk } from "@reduxjs/toolkit";
import { SERVER_API } from "../api/utils";

export const setProfile = createAsyncThunk("profile/setProfile", async () => {
  try {
    const doctorResponse = await SERVER_API.get("/doctors/me");
    if (doctorResponse.status === 200) {
      return doctorResponse.data;
    }
  } catch (err) {
    console.log(err);
    try {
      const patientResponse = await SERVER_API.get("/patient/me");
      if (patientResponse.status === 200) {
        return patientResponse.data;
      }
    } catch (err) {
      console.log(err);
    }
  }
});
