import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { setSpecialization } from "../action/specializationActions";

type Result = {
  content: Specialization[];
  last: boolean;
  first: boolean;
  totalElements: number;
  pageNumber: number;
};
export type Specialization = {
  id: number;
  specializationName: string;
};

const initialState = {
  content: [
    {
      id: 0,
      specializationName: "",
    },
  ],
  last: false,
  first: false,
  totalElements: 0,
  pageNumber: 0,
};

const specializationSlice = createSlice({
  name: "specialization",
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(setSpecialization.pending, () => {
        console.log("Loading specializations...");
      })
      .addCase(
        setSpecialization.fulfilled,
        (state, action: PayloadAction<Result>) => {
          state = action.payload;
          return state;
        }
      );
  },
});

export default specializationSlice.reducer;
