import { EditDoctor } from "./EditDoctor";
import { Button } from "../ui/button";
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
import {
  FaMessage,
  FaMobileScreenButton,
  FaRegHospital,
} from "react-icons/fa6";
import { BsThreeDots } from "react-icons/bs";
import { useState } from "react";
import female_doctor from "../../assets/female_doctor.png";
import {
  HoverCard,
  HoverCardContent,
  HoverCardTrigger,
} from "../ui/hover-card";

export const Doctor = () => {
  const data = useSelector((state: RootState) => state.profile.data);
  const dispatch = useDispatch<AppDispatch>();
  const navigate = useNavigate();
  const [deleteDialogOpen, setDeleteDialogOpen] = useState<boolean>(false);
  const [editDialogOpen, setEditDialogOpen] = useState<boolean>(false);
  return (
    <>
      <div className="grid grid-cols-3 gap-10 pt-10 pb-40">
        <div className="col-span-1 bg-slate-50 rounded-2xl drop-shadow-lg pb-10 flex flex-row">
          <div>
            <div className="flex flex-row p-11 gap-4">
              {data.gender &&
                (data.gender === "MALE" ? (
                  <img
                    src="https://cdn-icons-png.flaticon.com/512/3774/3774299.png"
                    className="rounded-full border border-slate-200 max-w-[60px] max-h-[60px] mt-2"
                  />
                ) : (
                  <img
                    src={female_doctor}
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
              <FaRegHospital color="#93B1A6" size={20} className="pt-1" />
              <h1 className="text-md text-slate-500 font-light">
                {data.clinicAddress || "No address"}
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
                  Years Of Experience:
                </h1>
                <h1 className="text-xl font-semibold">
                  {data.experienceYears || "Unknown"}
                </h1>
              </div>
              <div className="col-span-1">
                <h1 className="text-sm pt-5 font-semibold text-slate-500">
                  License Number:
                </h1>
                <h1 className="text-xl font-semibold">
                  {data.licenseNumber || "Unknown"}
                </h1>
              </div>
            </div>
            <div className="grid grid-cols-3">
              <div className="cols-span-1">
                <h1 className="text-sm pt-5 font-semibold text-slate-500">
                  Specializations:
                </h1>
                <h1 className="text-xl font-semibold">
                  {data.specializations?.length
                    ? data.specializations
                        .map((s) => s.specializationName)
                        .join(", ")
                    : "Not Specified"}
                </h1>
              </div>
              <div className="cols-span-1">
                <h1 className="text-sm pt-5 font-semibold text-slate-500">
                  Registered since:
                </h1>
                <h1 className="text-xl font-semibold">
                  {new Date(data.createdAt).toLocaleDateString("en-GB")}
                </h1>
              </div>
              <div className="cols-span-1">
                <h1 className="text-sm pt-5 font-semibold text-slate-500">
                  Last Updated:
                </h1>
                <h1 className="text-xl font-semibold">
                  {new Date(data.updatedAt).toLocaleDateString("en-GB")}
                </h1>
              </div>
            </div>
          </div>
        </div>
      </div>
      <EditDoctor
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
