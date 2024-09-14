import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { Specialization } from "./specializationReducer";
import { getAllDoctors } from "../action/doctorActions";

type Doctor = {
  id: number;
  firstName: string;
  lastName: string;
  clinicAddress: string;
  specializations: Specialization[];
  experienceYears: number;
};
type Result = {
  content: Doctor[];
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
      firstName: "",
      lastName: "",
      clinicAddress: "",
      specializations: [],
      experienceYears: 0,
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

const doctorsSlice = createSlice({
  name: "doctors",
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(getAllDoctors.pending, (state) => {
        state.error = null;
        return state;
      })
      .addCase(
        getAllDoctors.fulfilled,
        (state, action: PayloadAction<Result>) => {
          state = action.payload;
          return state;
        }
      )
      .addCase(getAllDoctors.rejected, (state, action) => {
        state.error = action.payload as { status: number; message: string };
        return state;
      });
  },
});

export default doctorsSlice.reducer;
