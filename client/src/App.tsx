import { createBrowserRouter, RouterProvider } from "react-router-dom";
import { Home } from "./pages/HomePage";
import { NotFoundPage } from "./pages/NotFoundPage";
import "./index.css";
import { Layout } from "./lib/Layout";
import { LoginPage } from "./pages/LoginPage";
import { ProfilePage } from "./pages/ProfilePage";
import { RegisterPage } from "./pages/RegisterPage";
import { MedicalRecordDetail } from "./components/profile/MedicalRecordDetail";
import { DoctorsListPage } from "./pages/DoctorsListPage";
import { DoctorDetailPage } from "./pages/DoctorDetailPage";
import { PatientListPage } from "./pages/PatientListPage";

const router = createBrowserRouter([
  {
    path: "/",
    element: <Layout children={<Home />} main={true} />,
    errorElement: <NotFoundPage />,
  },
  {
    path: "/login",
    element: <LoginPage />,
  },
  {
    path: "/register",
    element: <RegisterPage />,
  },
  {
    path: "/profile",
    element: <Layout children={<ProfilePage />} />,
  },
  {
    path: "/medical-records/:id",
    element: <Layout children={<MedicalRecordDetail />} />,
  },
  {
    path: "/doctors",
    element: <Layout children={<DoctorsListPage />} />,
  },
  {
    path: "/doctors/:id",
    element: <Layout children={<DoctorDetailPage />} />,
  },
  {
    path: "/patients",
    element: <Layout children={<PatientListPage />} />,
  },
]);

export const App = () => {
  return <RouterProvider router={router} />;
};
