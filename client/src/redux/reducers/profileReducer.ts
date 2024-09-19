import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import {
  deleteDoctorProfile,
  deletePatientProfile,
  logoutProfile,
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

export interface Profile {
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
  allergies: string | null;
  bloodType: string | null;
  createdAt: number;
  updatedAt: number;
}
export interface ProfileState {
  data: Profile;
  error: {
    status: number;
    message: string;
  } | null;
}

const initialState: ProfileState = {
  data: {
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
    allergies: null,
    bloodType: null,
    createdAt: 0,
    updatedAt: 0,
  },
  error: null,
};

const profileSlice = createSlice({
  name: "profile",
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(setProfile.pending, (state) => {
        state.error = null;
        console.log("Loading profile...");
      })
      .addCase(
        setProfile.fulfilled,
        (state, action: PayloadAction<Profile>) => {
          state.data = action.payload;
          const bloodTypeMap: { [key: string]: string } = {
            OPlus: "O+",
            OMinus: "O-",
            APlus: "A+",
            AMinus: "A-",
            BPlus: "B+",
            BMinus: "B-",
            ABPlus: "AB+",
            ABMinus: "AB-",
          };
          state.data.bloodType =
            bloodTypeMap[state.data.bloodType as keyof typeof bloodTypeMap] ||
            "";
          state.error = null;
          return state;
        }
      )
      .addCase(setProfile.rejected, (state, action) => {
        localStorage.removeItem("persist:profile");
        state.error = action.payload as { status: number; message: string };
        return state;
      });
    builder
      .addCase(patchPatientProfile.pending, () => {
        console.log("Updating patient profile...");
      })
      .addCase(
        patchPatientProfile.fulfilled,
        (state, action: PayloadAction<Profile>) => {
          state.data.bloodType = action.payload.bloodType;
          const bloodTypeMap: { [key: string]: string } = {
            OPlus: "O+",
            OMinus: "O-",
            APlus: "A+",
            AMinus: "A-",
            BPlus: "B+",
            BMinus: "B-",
            ABPlus: "AB+",
            ABMinus: "AB-",
          };
          state.data.bloodType =
            bloodTypeMap[state.data.bloodType as keyof typeof bloodTypeMap] ||
            "";
          state.data.address = action.payload.address;
          state.data.dateOfBirth = action.payload.dateOfBirth;
          state.data.firstName = action.payload.firstName;
          state.data.lastName = action.payload.lastName;
          state.data.phoneNumber = action.payload.phoneNumber;
          state.data.allergies = action.payload.allergies;
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
          state.data.clinicAddress = action.payload.data.clinicAddress;
          state.data.firstName = action.payload.data.firstName;
          state.data.lastName = action.payload.data.lastName;
          state.data.phoneNumber = action.payload.data.phoneNumber;
          state.data.experienceYears = action.payload.data.experienceYears;
          state.data.specializations = action.payload.data.specializations;
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
    builder
      .addCase(logoutProfile.pending, () => {
        console.log("Logging out...");
      })
      .addCase(logoutProfile.fulfilled, (state) => {
        state = initialState;
        return state;
      });
  },
});

export default profileSlice.reducer;
