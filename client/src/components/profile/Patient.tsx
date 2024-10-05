import { EditPatient } from "./EditPatient";
import { Button } from "../ui/button";
import { MedicalRecords } from "./MedicalRecords";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "../ui/dialog";
import { useDispatch, useSelector } from "react-redux";
import { AppDispatch, RootState } from "@/redux/store";
import { deleteProfile, logoutProfile } from "@/redux/action/profileActions";
import { useNavigate } from "react-router-dom";
import { FaMobileScreenButton, FaMessage, FaHouse } from "react-icons/fa6";
import { BsThreeDots } from "react-icons/bs";
import { useState } from "react";
import patient_female from "../../assets/patient_female.png";
import {
  HoverCard,
  HoverCardContent,
  HoverCardTrigger,
} from "../ui/hover-card";

export const Patient = () => {
  const data = useSelector((state: RootState) => state.profile.data);
  const dispatch = useDispatch<AppDispatch>();
  const navigate = useNavigate();
  const [deleteDialogOpen, setDeleteDialogOpen] = useState<boolean>(false);
  const [editDialogOpen, setEditDialogOpen] = useState<boolean>(false);
  return (
    <>
      <div className="grid grid-cols-3 gap-10 pt-10">
        <div className="col-span-1 bg-slate-50 rounded-2xl drop-shadow-lg pb-10 flex flex-row">
          <div>
            <div className="flex flex-row p-11 gap-4">
              {data.gender &&
                (data.gender === "MALE" ? (
                  <img
                    src="https://cdn-icons-png.flaticon.com/512/1430/1430453.png"
                    className="rounded-full border border-slate-200 max-w-[60px] max-h-[60px] mt-2"
                  />
                ) : (
                  <img
                    src={patient_female}
                    className="rounded-full border border-slate-200 max-w-[60px] max-h-[60px] mt-2"
                  />
                ))}

              <h1 className="text-2xl pt-6 font-semibold">
                {data.gender && (data.gender === "MALE" ? "Mr. " : "Mrs. ")}
                {data.firstName} {data.lastName}
              </h1>
            </div>
            <h1 className="text-xl pl-8 font-semibold pt-5">
              Contact Details:
            </h1>
            <div className="flex flex-row gap-2 pl-8 pt-2">
              <FaMobileScreenButton color="#93B1A6" size={20} />
              {data.phoneNumber ? (
                <h1 className="text-md text-slate-500 font-light">
                  +{data.phoneNumber}
                </h1>
              ) : (
                <h1 className="text-md text-slate-500 font-light">
                  No phone number
                </h1>
              )}
            </div>
            <div className="flex flex-row gap-2 pl-8 pt-2">
              <FaMessage color="#93B1A6" size={20} className="pt-1" />
              <h1 className="text-md text-slate-500 font-light">
                {data.user.email}
              </h1>
            </div>
            <div className="flex flex-row gap-2 pl-8 pt-2">
              <FaHouse color="#93B1A6" size={20} className="pt-1" />
              <h1 className="text-md text-slate-500 font-light">
                {data.address || "No address"}
              </h1>
            </div>
          </div>
          <div>
            <HoverCard>
              <HoverCardTrigger>
                <BsThreeDots size={25} className="mt-4 cursor-pointer" />
              </HoverCardTrigger>
              <HoverCardContent className="w-40">
                <div
                  className="p-1 rounded-lg hover:bg-slate-200 transition-color duration-300 cursor-pointer"
                  onClick={() => setEditDialogOpen(true)}
                >
                  Edit profile
                </div>
                <div
                  className="text-red-500 p-1 rounded-lg hover:bg-slate-200 transition-color duration-300 cursor-pointer"
                  onClick={() => setDeleteDialogOpen(true)}
                >
                  Delete profile
                </div>
              </HoverCardContent>
            </HoverCard>
          </div>
        </div>
        <div className="col-span-2 bg-slate-50 rounded-2xl drop-shadow-lg pl-16 h-64">
          <h1 className="text-xl font-semibold pt-10">Overview:</h1>
          <div className="grid grid-rows-2">
            <div className="grid grid-cols-3">
              <div className="cols-span-1">
                <h1 className="text-sm pt-5 font-semibold text-slate-500">
                  Gender:
                </h1>
                <h1 className="text-xl font-semibold">
                  {data.gender && (data.gender === "MALE" ? "Male" : "Female")}
                </h1>
              </div>
              <div className="col-span-1">
                <h1 className="text-sm pt-5 font-semibold text-slate-500">
                  Date of Birth:
                </h1>
                <h1 className="text-xl font-semibold">
                  {new Date(data.dateOfBirth).toISOString().substring(0, 10)}
                </h1>
              </div>
              <div className="col-span-1">
                <h1 className="text-sm pt-5 font-semibold text-slate-500">
                  BloodType:
                </h1>
                <h1 className="text-xl font-semibold">
                  {data.bloodType || "Unknown"}
                </h1>
              </div>
            </div>
            <div className="grid grid-cols-3">
              <div className="cols-span-1">
                <h1 className="text-sm pt-5 font-semibold text-slate-500">
                  Allergies:
                </h1>
                <h1 className="text-xl font-semibold">
                  {data.allergies || "None"}
                </h1>
              </div>
              <div className="cols-span-1">
                <h1 className="text-sm pt-5 font-semibold text-slate-500">
                  Registered since:
                </h1>
                <h1 className="text-xl font-semibold">
                  {new Date(data.createdAt).toISOString().substring(0, 10)}
                </h1>
              </div>
              <div className="cols-span-1">
                <h1 className="text-sm pt-5 font-semibold text-slate-500">
                  Last Updated:
                </h1>
                <h1 className="text-xl font-semibold">
                  {new Date(data.updatedAt).toISOString().substring(0, 10)}
                </h1>
              </div>
            </div>
          </div>
        </div>
      </div>
      <MedicalRecords id={data.id} />
      <EditPatient
        open={editDialogOpen}
        onClose={() => setEditDialogOpen(false)}
      />
      <Dialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>
              Are you sure you want to delete this account?
            </DialogTitle>
            <DialogDescription>
              Please note, this action cannot be undone and all data will be
              lost.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button
              variant={"default"}
              onClick={() => setDeleteDialogOpen(false)}
            >
              Close
            </Button>
            <Button
              variant={"destructive"}
              onClick={() => {
                dispatch(deleteProfile());
                dispatch(logoutProfile());
                setDeleteDialogOpen(false);
                navigate("/login");
              }}
            >
              Delete
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </>
  );
};
