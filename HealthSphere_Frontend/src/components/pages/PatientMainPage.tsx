import { useEffect, useState } from "react";
import getJwtToken from "../api/axiosJwt";
import axios from "axios";
import "../../index.css";
import { Skeleton } from "../ui/skeleton";
import { Button } from "../ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTrigger,
} from "../ui/dialog";
import { DialogTitle } from "@radix-ui/react-dialog";
import { Label } from "../ui/label";
import { Input } from "../ui/input";
import axiosInstance from "../api/axiosInstance";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
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

const formSchema = z.object({
  firstName: z.string().min(1, { message: "Please enter your first name" }),
  lastName: z.string().min(1, { message: "Please enter your last name" }),
  address: z.string().min(1, { message: "Please enter your address" }),
  gender: z.string().min(1, { message: "Please enter your gender" }),
  phoneNumber: z
    .string()
    .regex(/^\d{10}$/, { message: "Phone Number must be 10 digits" }),
  dateOfBirth: z
    .string()
    .min(1, { message: "Please enter your date of birth" }),
});

export default function PatientMainpage() {
  const [patient, setPatient] = useState<Patient | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      firstName: patient?.firstName || "",
      lastName: patient?.lastName || "",
      address: patient?.address || "",
      gender: patient?.gender || "",
      phoneNumber: patient?.phoneNumber || "",
      dateOfBirth: patient?.dateOfBirth
        ? new Date(patient?.dateOfBirth).toISOString().slice(0, 10)
        : new Date().toISOString().slice(0, 10),
    },
  });

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

  const onSubmit = async (values: z.infer<typeof formSchema>) => {
    console.log(values);
    const jwtToken = await getJwtToken();
    axiosInstance
      .patch("http://localhost:8000/api/v1/patient/me", values, {
        headers: {
          Authorization: `Bearer ${jwtToken}`,
        },
      })
      .then(() => {
        alert("success"); // for now
      })
      .catch((err) => {
        alert("error");
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

  useEffect(() => {
    if (patient) {
      reset({
        firstName: patient?.firstName || "",
        lastName: patient?.lastName || "",
        address: patient?.address || "",
        gender: patient?.gender || "",
        phoneNumber: patient?.phoneNumber || "",
        dateOfBirth: patient?.dateOfBirth
          ? new Date(patient?.dateOfBirth).toISOString().slice(0, 10)
          : new Date().toISOString().slice(0, 10),
      });
    }
  }, [patient, reset]);

  return (
    <>
      <div className="Page h-screen px-10">
        <h1 className="text-4xl pt-10">Information about the Patient</h1>
        <div className="INFO pt-5">
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
            <div className="loadedInfo border-2 border-black p-7 w-1/3">
              <p className="text-xl">
                Full Name: {patient?.firstName} {patient?.lastName}
              </p>
              <p className="text-xl">
                Date of Birth:{" "}
                {patient?.dateOfBirth
                  ? new Date(patient?.dateOfBirth)
                      .toISOString()
                      .substring(0, 10)
                  : "N/A"}
              </p>
              <p className="text-xl">
                Gender:{" "}
                {patient?.gender &&
                  patient?.gender.charAt(0) +
                    patient?.gender.substring(1).toLowerCase()}
              </p>
              <p className="text-xl">Address: {patient?.address || "N/A"}</p>
              <p className="text-xl">
                Phone Number: {patient?.phoneNumber || "N/A"}
              </p>
              <p className="text-xl">
                Registered on:{" "}
                {patient?.createdAt
                  ? new Date(patient?.createdAt).toISOString().substring(0, 10)
                  : "N/A"}
              </p>
              <p className="text-xl">
                Last updated:{" "}
                {patient?.updatedAt
                  ? new Date(patient?.updatedAt).toISOString().substring(0, 10)
                  : "N/A"}
              </p>
              <Dialog>
                <DialogTrigger asChild>
                  <Button variant="outline" className="mt-5">
                    Edit Profile
                  </Button>
                </DialogTrigger>
                <DialogContent className="sm:max-w-[425px]">
                  <DialogHeader>
                    <DialogTitle>Edit profile</DialogTitle>
                    <DialogDescription>
                      Make changes to your profile here. Click save when you're
                      done.
                    </DialogDescription>
                  </DialogHeader>
                  <form onSubmit={handleSubmit(onSubmit)}>
                    <div className="grid gap-4 py-4">
                      <div className="grid grid-cols-4 items-center gap-4">
                        <Label htmlFor="firstName" className="text-right">
                          First Name
                        </Label>
                        <Input
                          id="firstName"
                          {...register("firstName")}
                          className="col-span-3"
                        />
                        {errors.firstName && (
                          <p className="text-red-500 text-sm col-span-4">
                            {errors.firstName.message}
                          </p>
                        )}
                      </div>
                      <div className="grid grid-cols-4 items-center gap-4">
                        <Label htmlFor="lastName" className="text-right">
                          Last Name
                        </Label>
                        <Input
                          id="lastName"
                          {...register("lastName")}
                          className="col-span-3"
                        />
                        {errors.lastName && (
                          <p className="text-red-500 text-sm col-span-4">
                            {errors.lastName.message}
                          </p>
                        )}
                      </div>
                      <div className="grid grid-cols-4 items-center gap-4">
                        <Label htmlFor="address" className="text-right">
                          Address
                        </Label>
                        <Input
                          id="address"
                          {...register("address")}
                          className="col-span-3"
                        />
                        {errors.address && (
                          <p className="text-red-500 text-sm col-span-4">
                            {errors.address.message}
                          </p>
                        )}
                      </div>
                      <div className="grid grid-cols-4 items-center gap-4">
                        <Label htmlFor="phoneNumber" className="text-right">
                          Phone Number
                        </Label>
                        <Input
                          id="lastName"
                          {...register("phoneNumber")}
                          className="col-span-3"
                        />
                        {errors.phoneNumber && (
                          <p className="text-red-500 text-sm col-span-4">
                            {errors.phoneNumber.message}
                          </p>
                        )}
                      </div>
                      <div className="grid grid-cols-4 items-center gap-4">
                        <Label htmlFor="dateOfBirth" className="text-right">
                          Date Of Birth
                        </Label>
                        <Input
                          id="dateOfBirth"
                          className="col-span-3"
                          {...register("dateOfBirth")}
                        />
                        {errors.dateOfBirth && (
                          <p className="text-red-500 text-sm col-span-4">
                            {errors.dateOfBirth.message}
                          </p>
                        )}
                      </div>
                    </div>
                    <DialogFooter>
                      <Button type="submit">Save changes</Button>
                    </DialogFooter>
                  </form>
                </DialogContent>
              </Dialog>
              <Button
                variant={"destructive"}
                className="mt-5 mx-5"
                onClick={() => deleteAccount()}
              >
                Delete Account
              </Button>
            </div>
          )}
        </div>
      </div>
    </>
  );
}
