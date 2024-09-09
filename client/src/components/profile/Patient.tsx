import { ProfileState } from "@/redux/reducers/profileReducer";
import { EditPatient } from "./EditPatient";
import { Button } from "../ui/button";
import {
  HoverCard,
  HoverCardContent,
  HoverCardTrigger,
} from "@/components/ui/hover-card";

export const Patient = (props: { data: ProfileState }) => {
  const data = props.data;
  return (
    <div className="grid grid-cols-3 gap-10 pt-10">
      <div className="cols-span-1">
        <img
          src="https://cdn-icons-png.flaticon.com/512/1430/1430453.png"
          className="rounded-full"
        />
      </div>
      <div className="cols-span-1 space-y-5 bg-slate-50 p-10 rounded-3xl drop-shadow-lg">
        <h1 className="text-2xl">Personal Information</h1>
        <h1 className="text-4xl">
          {data.firstName} {data.lastName}
        </h1>
        <h1 className="text-xl">
          {new Date(data.dateOfBirth).toISOString().substring(0, 10)} |{" "}
          {data.gender && (data.gender === "MALE" ? "Male" : "Female")}
        </h1>
        <div className="flex flex-row gap-4 pt-28">
          <EditPatient />
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
        <h1 className="text-3xl">Contact Information</h1>
        <h1 className="text-xl text-slate-500">{data.user.email}</h1>
        <h1 className="text-xl text-slate-500">+{data.phoneNumber}</h1>
        <h1 className="text-xl text-slate-500">{data.address}</h1>
      </div>
    </div>
  );
};
