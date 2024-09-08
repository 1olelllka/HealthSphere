import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { setProfile } from "../action/profileAction";

type User = {
  email: string;
  role: string;
  createdAt: number;
  updatedAt: number;
};

type Spezialization = {
  id: number;
  specializationName: string;
};
export interface ProfileState {
  id: number;
  firstName: string;
  lastName: string;
  dateOfBirth: number;
  gender: string | null;
  address: string;
  phoneNumber: string;
  user: User;
  specializations: Spezialization[] | null;
  licenseNumber: string | null;
  experienceYears: number | null;
  clinicAddress: string | null;
  createdAt: number;
  updatedAt: number;
}

const initialState: ProfileState = {
  id: 0,
  firstName: "",
  lastName: "",
  dateOfBirth: 0,
  gender: null,
  address: "",
  phoneNumber: "",
  user: {
    email: "",
    role: "",
    createdAt: 0,
    updatedAt: 0,
  },
  specializations: null,
  licenseNumber: null,
  experienceYears: null,
  clinicAddress: null,
  createdAt: 0,
  updatedAt: 0,
};

const profileSlice = createSlice({
  name: "profile",
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(setProfile.pending, () => {
        console.log("Loading profile...");
      })
      .addCase(
        setProfile.fulfilled,
        (state, action: PayloadAction<ProfileState>) => {
          state = action.payload;
        }
      );
  },
});

export default profileSlice.reducer;
