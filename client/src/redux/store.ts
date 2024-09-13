import { configureStore } from "@reduxjs/toolkit";
// import { persistStore, persistReducer } from "redux-persist";
// import storage from "redux-persist/lib/storage";

import profileReducer from "./reducers/profileReducer";
import specializationReducer from "./reducers/specializationReducer";
import recordReducer from "./reducers/recordReducer";
import doctorReducer from "./reducers/doctorsReducer";
import patientReducer from "./reducers/patientsReducer";

// const persistConfig = {
//   key: "root",
//   storage,
//   whitelist: ["profile"],
// };

// const persistedReducer = persistReducer(persistConfig, profileReducer);

export const store = configureStore({
  reducer: {
    profile: profileReducer,
    specialization: specializationReducer,
    record: recordReducer,
    doctor: doctorReducer,
    patient: patientReducer,
  },
});

// export const persistor = persistStore(store);

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
