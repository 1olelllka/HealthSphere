import { useEffect, useState } from "react";
import getJwtToken from "../../api/axiosJwt";
import axios from "axios";
import "../../../index.css";
import { Skeleton } from "../../ui/skeleton";
import { Button } from "../../ui/button";
import { Label } from "../../ui/label";
import axiosInstance from "../../api/axiosInstance";
import { Card, CardContent, CardFooter } from "@/components/ui/card";
import PatchPatient from "./PatchPatient";
type User = {
  email: string;
  role: string;
};
type Patient = {
  firstName: string;
  lastName: string;
  address: string;
  gender: string;
  phoneNumber: string;
  dateOfBirth: Date;
  createdAt: Date;
  updatedAt: Date;
  user: User;
};
export default function PatientInfo() {
  const [patient, setPatient] = useState<Patient | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  const deleteAccount = async () => {
    const jwtToken = await getJwtToken();
    axiosInstance
      .delete("http://localhost:8000/api/v1/patient/me", {
        headers: {
          Authorization: `Bearer ${jwtToken}`,
        },
      })
      .then(() => {
        alert("success"); // for now
      })
      .catch((err) => {
        console.log(err);
      });
  };

  useEffect(() => {
    const fetchPatient = async () => {
      setLoading(true);
      try {
        const accessToken = await getJwtToken();
        const response = await axios.get(
          "http://localhost:8000/api/v1/patient/me",
          {
            headers: {
              Authorization: `Bearer ${accessToken}`,
            },
          }
        );
        setPatient(response.data);
        setInterval(() => setLoading(false), 1500);
      } catch (err) {
        console.error("Failed to fetch patient", err);
      }
    };
    fetchPatient();
  }, []);

  return (
    <>
      <div>
        <h2 className="text-2xl pt-5">Personal Information</h2>
        {loading ? (
          <div className="loading space-y-2 p-7">
            <Skeleton className="h-6 w-80 bg-gradient-to-r from-purple-300 via-pink-400 to-red-300 animate-pulse" />
            <Skeleton className="h-6 w-80 bg-gradient-to-r from-purple-300 via-pink-400 to-red-300 animate-pulse" />
            <Skeleton className="h-6 w-80 bg-gradient-to-r from-purple-300 via-pink-400 to-red-300 animate-pulse" />
            <Skeleton className="h-6 w-80 bg-gradient-to-r from-purple-300 via-pink-400 to-red-300 animate-pulse" />
            <Skeleton className="h-6 w-80 bg-gradient-to-r from-purple-300 via-pink-400 to-red-300 animate-pulse" />
            <Skeleton className="h-6 w-80 bg-gradient-to-r from-purple-300 via-pink-400 to-red-300 animate-pulse" />
          </div>
        ) : (
          <Card className="mt-5 pt-5">
            <CardContent className="grid pb-3">
              <Label className="text-xl">
                Full Name: {patient?.firstName} {patient?.lastName}
              </Label>
              <Label className="text-xl">
                Date of Birth:{" "}
                {patient?.dateOfBirth
                  ? new Date(patient?.dateOfBirth)
                      .toISOString()
                      .substring(0, 10)
                  : "N/A"}
              </Label>
              <Label className="text-xl">
                Gender:{" "}
                {patient?.gender &&
                  patient?.gender.charAt(0) +
                    patient?.gender.substring(1).toLowerCase()}
              </Label>
              <Label className="text-xl">
                Address: {patient?.address || "N/A"}
              </Label>
              <Label className="text-xl">
                Phone Number: {patient?.phoneNumber || "N/A"}
              </Label>
              <Label className="text-xl">
                Registered on:{" "}
                {patient?.createdAt
                  ? new Date(patient?.createdAt).toISOString().substring(0, 10)
                  : "N/A"}
              </Label>
              <Label className="text-xl">
                Last updated:{" "}
                {patient?.updatedAt
                  ? new Date(patient?.updatedAt).toISOString().substring(0, 10)
                  : "N/A"}
              </Label>
            </CardContent>
            <CardFooter>
              <PatchPatient patient={patient} />
              <Button
                variant={"destructive"}
                className="mt-5 mx-5"
                onClick={() => deleteAccount()}
              >
                Delete Account
              </Button>
            </CardFooter>
          </Card>
        )}
      </div>
    </>
  );
}
