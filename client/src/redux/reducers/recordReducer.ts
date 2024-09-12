import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import {
  searchRecord,
  setDetailedRecord,
  setRecord,
} from "../action/recordActions";

export type MedicalRecord = {
  id: number;
  patient: {
    id: number;
    firstName: string;
    lastName: string;
  };
  doctor: {
    id: number;
    firstName: string;
    lastName: string;
  };
  recordDate: string;
  diagnosis: string;
  treatment: string;
  prescription: Prescription;
  createdAt: string;
  updatedAt: string;
};

export type Prescription = {
  id: number;
  issuedDate: string;
};

type Result = {
  content: MedicalRecord[];
  last: boolean;
  first: boolean;
  totalElements: number;
  totalPages: number;
  pageNumber: number;
  number: number;
};

const initialState: Result = {
  content: [
    {
      id: 0,
      doctor: {
        id: 0,
        firstName: "",
        lastName: "",
      },
      patient: {
        id: 0,
        firstName: "",
        lastName: "",
      },
      recordDate: "",
      diagnosis: "",
      treatment: "",
      prescription: {
        id: 0,
        issuedDate: "",
      },
      createdAt: "",
      updatedAt: "",
    },
  ],
  last: true,
  first: true,
  totalElements: 0,
  pageNumber: 0,
  totalPages: 0,
  number: 0,
};

const recordSlice = createSlice({
  name: "record",
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(setRecord.pending, () => {
        console.log("Loading records...");
      })
      .addCase(setRecord.fulfilled, (state, action: PayloadAction<Result>) => {
        state = action.payload;
        return state;
      });
    builder
      .addCase(searchRecord.pending, () => {
        console.log("Searching records...");
      })
      .addCase(
        searchRecord.fulfilled,
        (state, action: PayloadAction<Result>) => {
          state = action.payload;
          return state;
        }
      );
    builder
      .addCase(setDetailedRecord.pending, () => {
        console.log("Loading detailed record...");
      })
      .addCase(
        setDetailedRecord.fulfilled,
        (state, action: PayloadAction<MedicalRecord>) => {
          state.content = [action.payload];
          return state;
        }
      );
  },
});

export default recordSlice.reducer;
