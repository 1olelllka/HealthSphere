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
  data: AppointmentState[];
  error: {
    status: number;
    message: string;
  } | null;
}

const initialState: Result = {
  data: [],
  error: null,
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
        (state, action: PayloadAction<AppointmentState[]>) => {
          state.data = action.payload as AppointmentState[];
          state.data.forEach((item: AppointmentState) => {
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
        (state, action: PayloadAction<AppointmentState[]>) => {
          state.data = action.payload as AppointmentState[];
          state.data.forEach((item: AppointmentState) => {
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
        return state;
      })
      .addCase(deleteAppointment.fulfilled, (state, action) => {
        state.data = state.data.filter(
          (item) => item.id != (action.payload as number)
        );
        return state;
      })
      .addCase(deleteAppointment.rejected, (state, action) => {
        state.error = action.payload as { status: number; message: string };
        return state;
      });
    builder
      .addCase(patchAppointment.pending, (state) => {
        state.error = null;
        return state;
      })
      .addCase(patchAppointment.fulfilled, (state, action) => {
        if (action.payload?.action === "POST") {
          state.data = state.data.filter(
            (item) => item.id != action.payload?.lastId
          );
          state.data.push(action.payload.data);
        } else if (action.payload?.action === "PATCH") {
          state.data = state.data.map((item) => {
            if (item.id == action.payload?.data.id) {
              return action.payload?.data;
            } else {
              return item;
            }
          }) as AppointmentState[];
        } else if (action.payload?.action === "DELETE") {
          state.data = state.data.filter(
            (item) => item.id != action.payload?.deleteId
          );
        }
        return state;
      })
      .addCase(patchAppointment.rejected, (state, action) => {
        state.error = action.payload as { status: number; message: string };
        return state;
      });
    builder
      .addCase(createAppointment.pending, (state) => {
        state.error = null;
        return state;
      })
      .addCase(
        createAppointment.fulfilled,
        (state, action: PayloadAction<AppointmentState>) => {
          state.data.push(action.payload);
          return state;
        }
      )
      .addCase(createAppointment.rejected, (state, action) => {
        state.error = action.payload as { status: number; message: string };
        return state;
      });
  },
});

export default AppointmentSlice.reducer;
