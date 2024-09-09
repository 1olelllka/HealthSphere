import { configureStore } from "@reduxjs/toolkit";
// import { persistStore, persistReducer } from "redux-persist";
// import storage from "redux-persist/lib/storage";

import profileReducer from "./reducers/profileReducer";
import specializationReducer from "./reducers/specializationReducer";

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
  },
});

// export const persistor = persistStore(store);

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
