import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import {
  createPrescriptionForRecord,
  deleteMedicalRecord,
  deletePrescriptionForRecord,
  searchRecord,
  setDetailedRecord,
  setRecord,
  updateMedicalRecord,
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
  prescription: Prescription | null;
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
  error: {
    status: number;
    message: string;
  } | null;
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
  error: null,
};

const recordSlice = createSlice({
  name: "record",
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(setRecord.pending, (state) => {
        state.error = null;
      })
      .addCase(setRecord.fulfilled, (state, action: PayloadAction<Result>) => {
        state = action.payload;
        return state;
      });
    builder
      .addCase(searchRecord.pending, (state) => {
        state.error = null;
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
      )
      .addCase(setDetailedRecord.rejected, (state, action) => {
        state.error = action.payload as { status: number; message: string };
        return state;
      });
    builder
      .addCase(createPrescriptionForRecord.pending, (state) => {
        state.error = null;
        return state;
      })
      .addCase(
        createPrescriptionForRecord.fulfilled,
        (state, action: PayloadAction<MedicalRecord>) => {
          state.content[0] = action.payload;
          return state;
        }
      )
      .addCase(createPrescriptionForRecord.rejected, (state, action) => {
        state.error = action.payload as { status: number; message: string };
      });
    builder
      .addCase(updateMedicalRecord.pending, (state) => {
        state.error = null;
      })
      .addCase(
        updateMedicalRecord.fulfilled,
        (state, action: PayloadAction<MedicalRecord>) => {
          state.content[0].diagnosis = action.payload.diagnosis;
          state.content[0].treatment = action.payload.treatment;
          return state;
        }
      )
      .addCase(updateMedicalRecord.rejected, (state, action) => {
        state.error = action.payload as { status: number; message: string };
        return state;
      });
    builder
      .addCase(deletePrescriptionForRecord.pending, (state) => {
        state.error = null;
        return state;
      })
      .addCase(deletePrescriptionForRecord.fulfilled, (state, action) => {
        const record = state.content.filter(
          (item) => item.id === action.payload
        );
        record[0].prescription = null;
        return state;
      })
      .addCase(deletePrescriptionForRecord.rejected, (state, action) => {
        state.error = action.payload as { status: number; message: string };
        return state;
      });
    builder
      .addCase(deleteMedicalRecord.pending, (state) => {
        state.error = null;
        return state;
      })
      .addCase(deleteMedicalRecord.fulfilled, (state, action) => {
        state.content = state.content.filter(
          (item) => item.id !== action.payload
        );
        return state;
      })
      .addCase(deleteMedicalRecord.rejected, (state, action) => {
        state.error = action.payload as { status: number; message: string };
        return state;
      });
  },
});

export default recordSlice.reducer;
