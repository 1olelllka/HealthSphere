import { ProfileState } from "@/redux/reducers/profileReducer";
import { EditDoctor } from "./EditDoctor";
import { Button } from "../ui/button";
import {
  HoverCard,
  HoverCardContent,
  HoverCardTrigger,
} from "@/components/ui/hover-card";

export const Doctor = (props: { data: ProfileState["data"] }) => {
  const data = props.data;
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
    </>
  );
};
