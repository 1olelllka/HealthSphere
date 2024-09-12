import { createAsyncThunk } from "@reduxjs/toolkit";
import { SERVER_API } from "../api/utils";

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
  async (id: number) => {
    try {
      const response = await SERVER_API.get(`patient/medical-records/${id}`);
      if (response.status === 200) {
        return response.data;
      }
    } catch (err) {
      console.log(err);
    }
  }
);
