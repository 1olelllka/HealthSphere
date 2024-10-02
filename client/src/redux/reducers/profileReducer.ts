import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import {
  deleteProfile,
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
  loading: boolean;
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
  loading: false,
};

const profileSlice = createSlice({
  name: "profile",
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(setProfile.pending, (state) => {
        state.error = null;
        state.loading = true;
        return state;
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
          state.loading = false;
          return state;
        }
      )
      .addCase(setProfile.rejected, (state, action) => {
        state.data = initialState.data;
        state.loading = false;
        localStorage.removeItem("persist:profile");
        console.log(action.payload);
        state.error = action.payload as { status: number; message: string };
        return state;
      });
    builder
      .addCase(patchPatientProfile.pending, (state) => {
        state.error = null;
        state.loading = true;
        return state;
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
          state.loading = false;
          return state;
        }
      )
      .addCase(patchPatientProfile.rejected, (state, action) => {
        state.error = action.payload as { status: number; message: string };
        if (state.error.status != 400) {
          state.data = initialState.data;
        }
        state.loading = false;
        return state;
      });
    builder
      .addCase(patchDoctorProfile.pending, (state) => {
        state.error = null;
        return state;
      })
      .addCase(
        patchDoctorProfile.fulfilled,
        (state, action: PayloadAction<Profile>) => {
          state.data.clinicAddress = action.payload.clinicAddress;
          state.data.firstName = action.payload.firstName;
          state.data.lastName = action.payload.lastName;
          state.data.phoneNumber = action.payload.phoneNumber;
          state.data.experienceYears = action.payload.experienceYears;
          state.data.specializations = action.payload.specializations;
          return state;
        }
      )
      .addCase(patchDoctorProfile.rejected, (state, action) => {
        state.error = action.payload as { status: number; message: string };
        if (state.error.status != 400) {
          state.data = initialState.data;
        }
        return state;
      });
    builder
      .addCase(deleteProfile.pending, (state) => {
        state.error = null;
        return state;
      })
      .addCase(deleteProfile.fulfilled, (state) => {
        state = initialState;
        return state;
      })
      .addCase(deleteProfile.rejected, (state, action) => {
        state.error = action.payload as { status: number; message: string };
        return state;
      });
    builder
      .addCase(logoutProfile.pending, () => {
        console.log("Logout pending");
      })
      .addCase(logoutProfile.fulfilled, (state) => {
        state = initialState;
        return state;
      });
  },
});

export default profileSlice.reducer;
