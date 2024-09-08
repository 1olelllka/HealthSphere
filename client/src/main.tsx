import { createRoot } from "react-dom/client";
import "./index.css";
import React from "react";
import { AppContainer } from "./AppContainer";

createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <AppContainer />
  </React.StrictMode>
);
