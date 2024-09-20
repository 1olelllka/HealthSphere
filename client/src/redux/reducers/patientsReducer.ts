import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { getAllPatients } from "../action/patientActions";

export type Patient = {
  id: number;
  email: string;
  user: {
    email: string;
  };
  firstName: string;
  lastName: string;
  dateOfBirth: string | undefined;
  gender: string | null;
  address: string | null;
  phoneNumber: string | null;
  bloodType: string | null;
  allergies: string | null;
  createdAt: number;
  updatedAt: number;
};
type Result = {
  content: Patient[];
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
      email: "",
      firstName: "",
      lastName: "",
      user: {
        email: "",
      },
      dateOfBirth: undefined,
      gender: null,
      address: null,
      phoneNumber: null,
      bloodType: null,
      allergies: null,
      createdAt: 0,
      updatedAt: 0,
    },
  ],
  last: false,
  first: false,
  totalElements: 0,
  totalPages: 0,
  pageNumber: 0,
  number: 0,
  error: null,
};

const patientsSlice = createSlice({
  name: "patients",
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(getAllPatients.pending, (state) => {
        state.error = null;
      })
      .addCase(
        getAllPatients.fulfilled,
        (state, action: PayloadAction<Result>) => {
          state = action.payload;
          return state;
        }
      )
      .addCase(getAllPatients.rejected, (state, action) => {
        state.error = action.payload as { status: number; message: string };
        return state;
      });
  },
});

export default patientsSlice.reducer;
