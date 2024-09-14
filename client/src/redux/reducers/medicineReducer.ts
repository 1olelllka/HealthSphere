import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import {
  addMedicineToPrescription,
  deleteMedicineFromPrescription,
  setMedicineForPrescription,
  updateMedicineForPrescription,
} from "../action/medicineActions";

export type Medicine = {
  id: number;
  medicineName: string;
  dosage: string;
  instructions: string | null;
};
interface Result {
  data: Medicine[];
  error: {
    status: number;
    message: string;
  } | null;
}

const initialState: Result = {
  data: [
    {
      id: 0,
      medicineName: "",
      dosage: "",
      instructions: "",
    },
  ],
  error: null,
};

export const MedicineSlice = createSlice({
  name: "medicine",
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(setMedicineForPrescription.pending, (state) => {
        state.error = null;
        return state;
      })
      .addCase(
        setMedicineForPrescription.fulfilled,
        (state, action: PayloadAction<Medicine[]>) => {
          state.data = action.payload as Medicine[];
          return state;
        }
      )
      .addCase(setMedicineForPrescription.rejected, (state, action) => {
        state.error = action.payload as { status: number; message: string };
        return state;
      });
    builder
      .addCase(updateMedicineForPrescription.pending, (state) => {
        state.error = null;
        return state;
      })
      .addCase(
        updateMedicineForPrescription.fulfilled,
        (state, action: PayloadAction<Medicine>) => {
          const updatedData = state.data.map((item) =>
            item.id === action.payload.id ? action.payload : item
          );
          return {
            ...state,
            data: updatedData,
          };
        }
      )
      .addCase(updateMedicineForPrescription.rejected, (state, action) => {
        state.error = action.payload as { status: number; message: string };
        return state;
      });
    builder
      .addCase(addMedicineToPrescription.pending, (state) => {
        state.error = null;
        return state;
      })
      .addCase(
        addMedicineToPrescription.fulfilled,
        (state, action: PayloadAction<Medicine>) => {
          state.data = [...state.data, action.payload];
          return state;
        }
      )
      .addCase(addMedicineToPrescription.rejected, (state, action) => {
        state.error = action.payload as { status: number; message: string };
        return state;
      });
    builder
      .addCase(deleteMedicineFromPrescription.pending, (state) => {
        state.error = null;
        return state;
      })
      .addCase(deleteMedicineFromPrescription.fulfilled, (state, action) => {
        state.data = state.data.filter((item) => item.id !== action.payload);
        return state;
      })
      .addCase(deleteMedicineFromPrescription.rejected, (state, action) => {
        state.error = action.payload as { status: number; message: string };
        return state;
      });
  },
});

export default MedicineSlice.reducer;
