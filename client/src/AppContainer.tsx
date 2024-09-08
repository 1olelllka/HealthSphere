import { App } from "./App";
import { useState, useEffect } from "react";
import { ServerDownError } from "./pages/ServerDownError";
import { Provider } from "react-redux";
import { persistor, store } from "./redux/store";
import axios from "axios";
import { PersistGate } from "redux-persist/integration/react";

export const AppContainer = () => {
  const [error, setError] = useState<boolean>(false);

  useEffect(() => {
    const checkServerHealth = async () => {
      try {
        console.log("Checking server health...");
        await axios.get("http://localhost:8000/actuator/health", {
          withCredentials: true,
          headers: {
            "Content-Type": "application/json",
          },
        });
        console.log("Server is healthy!");
      } catch (err) {
        console.log(err);
        setError(true); // ! change it to true
      }
    };
    checkServerHealth();
  }, []);

  return (
    <>
      {error ? (
        <ServerDownError />
      ) : (
        <Provider store={store}>
          <PersistGate loading={null} persistor={persistor}>
            <App />
          </PersistGate>
        </Provider>
      )}
    </>
  );
};
