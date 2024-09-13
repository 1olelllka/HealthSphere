import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { getAllPatients } from "../action/patientActions";

type Patient = {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
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
