import { configureStore } from "@reduxjs/toolkit";
import { persistStore, persistReducer } from "redux-persist";
import storage from "redux-persist/lib/storage"; // defaults to localStorage for web

import profileReducer from "./reducers/profileReducer";
import specializationReducer from "./reducers/specializationReducer";
import recordReducer from "./reducers/recordReducer";
import doctorReducer from "./reducers/doctorsReducer";
import patientReducer from "./reducers/patientsReducer";
import medicineReducer from "./reducers/medicineReducer";
import appointmentReducer from "./reducers/appointmentsReducer";

const persistConfig = {
  key: "profile",
  storage,
  stateReconciler: undefined,
};

const persistedReducer = persistReducer(persistConfig, profileReducer);

export const store = configureStore({
  reducer: {
    profile: persistedReducer,
    specialization: specializationReducer,
    record: recordReducer,
    doctor: doctorReducer,
    patient: patientReducer,
    medicine: medicineReducer,
    appointment: appointmentReducer,
  },
});

export const persistor = persistStore(store);

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
