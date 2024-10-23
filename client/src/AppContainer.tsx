import { App } from "./App";
import { useState, useEffect } from "react";
import { ServerDownError } from "./pages/ServerDownError";
import { Provider } from "react-redux";
import { persistor, store } from "./redux/store";
import axios from "axios";
import { PersistGate } from "redux-persist/integration/react";

export const AppContainer = () => {
  const [error, setError] = useState<boolean>(false);

  const checkServerHealth = async () => {
    try {
      console.log("Checking server health...");
      await axios.get(`${import.meta.env.VITE_SERVER_URL}/actuator/health`, {
        withCredentials: true,
        headers: {
          "Content-Type": "application/json",
        },
      });
      console.log("Server is healthy!");
      setError(false); // Reset error state on success
    } catch (err) {
      console.log(err);
      setError(true); // Set error state to true
    }
  };

  useEffect(() => {
    checkServerHealth();

    const intervalId = setInterval(checkServerHealth, 1000 * 35); // Check every minute

    return () => clearInterval(intervalId); // Cleanup function to clear the interval
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
