// sample data for design:
const data = {
  id: 20,
  user: {
    email: "iDoctor@email.com",
    role: "ROLE_DOCTOR",
    createdAt: 1724754259084,
    updatedAt: 1724754259084,
  },
  firstName: "Adam",
  lastName: "Smith",
  specializations: [
    {
      id: 1,
      name: "Dermatologist",
    },
  ],
  licenseNumber: "1232451123412",
  experienceYears: 5,
  phoneNumber: "4123412341234123",
  clinicAddress: "Nord St. 4121",
  createdAt: 1724754259096,
  updatedAt: 1725014588745,
};

export const Profile = () => {
  return (
    <>
      <div className="flex flex-col pt-28 justify-center items-center">
        <div className="container">
          <h1 className="text-4xl">Profile</h1>
          <div className="grid grid-cols-3 gap-10 pt-10">
            <div className="cols-span-1">
              <img
                src="https://cdn-icons-png.flaticon.com/512/3774/3774299.png"
                className="rounded-full"
              />
            </div>
            <div className="cols-span-1 space-y-5 bg-slate-50 p-10 rounded-3xl">
              <h1 className="text-3xl">Personal Information</h1>
              <h1 className="text-2xl">
                {data.firstName} {data.lastName}
              </h1>
              <h1 className="text-xl">
                {data.specializations[0].name}, {data.experienceYears} years of
                experience
              </h1>
              <h1 className="text-xl">Works at {data.clinicAddress}</h1>
              <h1 className="text-xl">License Number: {data.licenseNumber}</h1>
            </div>
            <div className="col-span-1 space-y-2 bg-slate-50 p-10 rounded-3xl">
              <h1 className="text-3xl">Contact Information</h1>
              <h1 className="text-xl">{data.user.email}</h1>
              <h1 className="text-xl">+{data.phoneNumber}</h1>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};
