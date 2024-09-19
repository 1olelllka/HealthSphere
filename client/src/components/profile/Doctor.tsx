import { EditDoctor } from "./EditDoctor";
import { Button } from "../ui/button";
import {
  HoverCard,
  HoverCardContent,
  HoverCardTrigger,
} from "@/components/ui/hover-card";
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "../ui/dialog";
import { useDispatch, useSelector } from "react-redux";
import { AppDispatch, RootState } from "@/redux/store";
import {
  deleteDoctorProfile,
  logoutProfile,
} from "@/redux/action/profileActions";
import { useNavigate } from "react-router-dom";

export const Doctor = () => {
  const data = useSelector((state: RootState) => state.profile.data);
  const dispatch = useDispatch<AppDispatch>();
  const navigate = useNavigate();
  return (
    <>
      <div className="grid grid-cols-3 gap-10 pt-10">
        <div className="cols-span-1">
          <img
            src="https://cdn-icons-png.flaticon.com/512/3774/3774299.png"
            className="rounded-full"
          />
        </div>
        <div className="cols-span-1 space-y-4 bg-slate-50 p-10 rounded-3xl drop-shadow-lg">
          <h1 className="text-2xl">Personal Information</h1>
          <h1 className="text-4xl">
            Dr. {data.firstName} {data.lastName}
          </h1>
          <div className="flex flex-row space-x-4">
            {data?.specializations?.map((item) => (
              <div className="bg-slate-200 rounded-md p-2 ">
                <h1 key={item.id} className="text-md text-slate-950">
                  {item.specializationName}{" "}
                </h1>
              </div>
            ))}
          </div>
          <h1 className="text-xl">
            {data.experienceYears} years of practical experience
          </h1>
          <div className="flex flex-row gap-4 pt-16">
            <EditDoctor />

            <HoverCard>
              <HoverCardTrigger>
                <Button
                  variant={"ghost"}
                  className="[box-shadow:1px_1px_2px_var(--tw-shadow-color)] shadow-slate-400 font-semibold hover:bg-[#040D12] hover:text-white"
                >
                  Show Additional Information
                </Button>
              </HoverCardTrigger>
              <HoverCardContent>
                <div className="flex flex-col">
                  <h1>License: {data.licenseNumber}</h1>
                  <h1>
                    Registered:{" "}
                    {new Date(data.createdAt).toISOString().substring(0, 10)}
                  </h1>
                  <h1>
                    Last Update:{" "}
                    {new Date(data.updatedAt).toISOString().substring(0, 10)}
                  </h1>
                </div>
              </HoverCardContent>
            </HoverCard>
          </div>
        </div>
        <div className="col-span-1 space-y-2 bg-slate-50 p-10 rounded-3xl drop-shadow-lg">
          <h1 className="text-2xl">Contact Information</h1>
          <h1 className="text-xl text-slate-500">{data.user.email}</h1>
          {data.phoneNumber ? (
            <h1 className="text-xl text-slate-500">+{data.phoneNumber}</h1>
          ) : (
            <h1 className="text-xl text-slate-500">No phone number</h1>
          )}
          <h1 className="text-xl text-slate-500">{data.clinicAddress}</h1>
        </div>
      </div>
      <div className="space-y-4">
        <h1 className="text-3xl text-red-500 pt-10">Danger Zone</h1>
        <Dialog>
          <DialogTrigger>
            <Button variant={"destructive"}>Delete Account</Button>
          </DialogTrigger>
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
              <DialogClose asChild>
                <Button variant={"default"}>Close</Button>
              </DialogClose>
              <Button
                variant={"destructive"}
                onClick={() => {
                  dispatch(deleteDoctorProfile(data.id));
                  dispatch(logoutProfile());
                  navigate("/login");
                }}
              >
                Delete
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      </div>
    </>
  );
};
