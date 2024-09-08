import { ProfileState } from "@/redux/reducers/profileReducer";

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
      <div className="cols-span-1 space-y-5 bg-slate-50 p-10 rounded-3xl">
        <h1 className="text-3xl">Personal Information</h1>
        <h1 className="text-2xl">
          {data.firstName} {data.lastName}
        </h1>
        <h1 className="text-xl">
          {new Date(data.dateOfBirth).toISOString().substring(0, 10)} |{" "}
          {data.gender && (data.gender === "MALE" ? "Male" : "Female")}
        </h1>
        <h1 className="text-xl">Lives at {data.address}</h1>
      </div>
      <div className="col-span-1 space-y-2 bg-slate-50 p-10 rounded-3xl">
        <h1 className="text-3xl">Contact Information</h1>
        <h1 className="text-xl">{data.user.email}</h1>
        <h1 className="text-xl">+{data.phoneNumber}</h1>
      </div>
    </div>
  );
};
