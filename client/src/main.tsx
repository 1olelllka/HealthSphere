import { createRoot } from "react-dom/client";
import "./index.css";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import React from "react";
import Home from "./components/pages/Home";
import NotFoundPage from "./components/NotFoundPage";

import PatientRegister from "./components/auth/PatientRegister";
import Login from "./components/auth/Login";
import DoctorRegister from "./components/auth/DoctorRegister";
import PatientMainpage from "./components/pages/PatientMainPage/PatientMainPage";
import DoctorMainPage from "./components/pages/DoctorMainPage/DoctorMainPage";
import DoctorDetail from "./components/pages/DoctorDetail/DoctorDetail";
import ListOfDoctors from "./components/pages/ListOfDoctors";

const router = createBrowserRouter([
  {
    path: "/",
    element: <Home />,
    errorElement: <NotFoundPage />,
  },
  {
    path: "/register",
    element: <PatientRegister />,
  },
  {
    path: "/login",
    element: <Login />,
  },
  {
    path: "/doctor-register",
    element: <DoctorRegister />,
  },
  {
    path: "/patient/me/",
    element: <PatientMainpage />,
  },
  {
    path: "/doctors",
    element: <ListOfDoctors />,
  },
  {
    path: "/doctors/:id",
    element: <DoctorDetail />,
  },
  {
    path: "/doctors/me",
    element: <DoctorMainPage />,
  },
]);

createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <RouterProvider router={router} />
  </React.StrictMode>
);
