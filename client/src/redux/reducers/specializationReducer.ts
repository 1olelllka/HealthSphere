import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { setSpecialization } from "../action/specializationActions";

export type Specialization = {
  id: number;
  specializationName: string;
};

const initialState = [
  {
    id: 0,
    specializationName: "",
  },
];

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
        (state, action: PayloadAction<Specialization[]>) => {
          state = action.payload;
          return state;
        }
      );
  },
});

export default specializationSlice.reducer;
