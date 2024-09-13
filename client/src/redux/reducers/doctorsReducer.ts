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
};

const doctorsSlice = createSlice({
  name: "doctors",
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(getAllDoctors.pending, () => {
        console.log("Loading doctors...");
      })
      .addCase(
        getAllDoctors.fulfilled,
        (state, action: PayloadAction<Result>) => {
          state = action.payload;
          return state;
        }
      );
  },
});

export default doctorsSlice.reducer;
