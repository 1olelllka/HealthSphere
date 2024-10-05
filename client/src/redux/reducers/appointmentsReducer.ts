import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import {
  createAppointment,
  deleteAppointment,
  patchAppointment,
  setAppointmentsForDoctor,
  setAppointmentsForPatient,
} from "../action/appointmentActions";

export interface AppointmentState {
  id: number;
  patient: {
    id: number;
    firstName: string;
    lastName: string;
    user: {
      email: string;
    };
  };
  doctor: {
    id: number;
    firstName: string;
    lastName: string;
  };
  appointmentDate: string | Date;
  endDate: string | Date;
  allDay: boolean;
  status: "SCHEDULED" | "COMPLETED" | "CANCELED";
  reason: string;
  createdAt: Date;
  updatedAt: Date;
}

interface Result {
  content: AppointmentState[];
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
  loading: boolean;
  success: string;
}

const initialState: Result = {
  last: false,
  first: false,
  totalElements: 0,
  totalPages: 0,
  pageNumber: 0,
  number: 0,
  content: [],
  error: null,
  loading: false,
  success: "",
};

const AppointmentSlice = createSlice({
  name: "appointments",
  initialState: initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(setAppointmentsForDoctor.pending, (state) => {
        state.error = null;
        return state;
      })
      .addCase(
        setAppointmentsForDoctor.fulfilled,
        (state, action: PayloadAction<Result>) => {
          state = action.payload;
          state.content.forEach((item: AppointmentState) => {
            item.allDay = false;
            item.endDate = new Date(
              new Date(item.appointmentDate).getTime() + 30 * 60 * 1000
            ).toISOString();
          });
          return state;
        }
      )
      .addCase(setAppointmentsForDoctor.rejected, (state, action) => {
        state.error = action.payload as { status: number; message: string };
        return state;
      });
    builder
      .addCase(setAppointmentsForPatient.pending, (state) => {
        state.error = null;
        return state;
      })
      .addCase(
        setAppointmentsForPatient.fulfilled,
        (state, action: PayloadAction<Result>) => {
          state = action.payload;
          state.content.forEach((item: AppointmentState) => {
            item.allDay = false;
            item.endDate = new Date(
              new Date(item.appointmentDate).getTime() + 30 * 60 * 1000
            ).toISOString();
          });
          return state;
        }
      )
      .addCase(setAppointmentsForPatient.rejected, (state, action) => {
        state.error = action.payload as { status: number; message: string };
        return state;
      });
    builder
      .addCase(deleteAppointment.pending, (state) => {
        state.error = null;
        state.loading = true;
        return state;
      })
      .addCase(deleteAppointment.fulfilled, (state, action) => {
        state.content = state.content.filter(
          (item) => item.id !== (action.payload as number)
        );
        state.loading = false;
        state.success = "Appointment deleted successfully";
        return state;
      })
      .addCase(deleteAppointment.rejected, (state, action) => {
        state.error = action.payload as { status: number; message: string };
        state.loading = false;
        return state;
      });
    builder
      .addCase(patchAppointment.pending, (state) => {
        state.error = null;
        state.loading = true;
        return state;
      })
      .addCase(patchAppointment.fulfilled, (state, action) => {
        if (action.payload?.action === "POST") {
          state.content = state.content.filter(
            (item) => item.id != action.payload?.lastId
          );
          state.content.push(action.payload.data);
        } else if (action.payload?.action === "PATCH") {
          state.content = state.content.map((item) => {
            if (item.id == action.payload?.data.id) {
              return action.payload?.data;
            } else {
              return item;
            }
          }) as AppointmentState[];
        } else if (action.payload?.action === "DELETE") {
          state.content = state.content.filter(
            (item) => item.id != action.payload?.deleteId
          );
        }
        state.loading = false;
        state.success = "Appointment updated successfully";
        return state;
      })
      .addCase(patchAppointment.rejected, (state, action) => {
        state.error = action.payload as { status: number; message: string };
        state.loading = false;
        return state;
      });
    builder
      .addCase(createAppointment.pending, (state) => {
        state.error = null;
        state.loading = true;
        return state;
      })
      .addCase(
        createAppointment.fulfilled,
        (state, action: PayloadAction<AppointmentState>) => {
          return {
            ...state,
            data: [action.payload, ...state.content],
            loading: false,
          };
        }
      )
      .addCase(createAppointment.rejected, (state, action) => {
        state.error = action.payload as { status: number; message: string };
        state.loading = false;
        return state;
      });
  },
});

export default AppointmentSlice.reducer;
