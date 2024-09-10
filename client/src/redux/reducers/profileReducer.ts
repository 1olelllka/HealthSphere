import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import {
  deleteDoctorProfile,
  deletePatientProfile,
  patchDoctorProfile,
  patchPatientProfile,
  setProfile,
} from "../action/profileActions";

type User = {
  email: string;
  role: string;
  createdAt: number;
  updatedAt: number;
};

export type Spezialization = {
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
  licenseNumber: string;
  experienceYears: number | undefined;
  clinicAddress: string;
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
  licenseNumber: "",
  experienceYears: undefined,
  clinicAddress: "",
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
          return state;
        }
      );
    builder
      .addCase(patchPatientProfile.pending, () => {
        console.log("Updating patient profile...");
      })
      .addCase(
        patchPatientProfile.fulfilled,
        (state, action: PayloadAction<ProfileState>) => {
          state.address = action.payload.address;
          state.dateOfBirth = action.payload.dateOfBirth;
          state.firstName = action.payload.firstName;
          state.lastName = action.payload.lastName;
          state.phoneNumber = action.payload.phoneNumber;
          return state;
        }
      );
    builder
      .addCase(patchDoctorProfile.pending, () => {
        console.log("Updating doctor profile...");
      })
      .addCase(
        patchDoctorProfile.fulfilled,
        (state, action: PayloadAction<ProfileState>) => {
          state.clinicAddress = action.payload.clinicAddress;
          state.firstName = action.payload.firstName;
          state.lastName = action.payload.lastName;
          state.phoneNumber = action.payload.phoneNumber;
          state.experienceYears = action.payload.experienceYears;
          state.specializations = action.payload.specializations;
          return state;
        }
      );
    builder
      .addCase(deletePatientProfile.pending, () => {
        console.log("Deleting patient profile...");
      })
      .addCase(deletePatientProfile.fulfilled, (state) => {
        state = initialState;
        return state;
      });
    builder
      .addCase(deleteDoctorProfile.pending, () => {
        console.log("Deleting doctor profile...");
      })
      .addCase(deleteDoctorProfile.fulfilled, (state) => {
        state = initialState;
        return state;
      });
  },
});

export default profileSlice.reducer;
