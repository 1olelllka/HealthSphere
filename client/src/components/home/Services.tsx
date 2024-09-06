export const Services = () => {
  return (
    <div className="pt-20">
      <h1 className="text-5xl">Our Services</h1>
      <div className="pt-10 grid grid-cols-3 gap-10">
        <div className="col-span-1 relative group drop-shadow-lg">
          <img
            src="https://americanretrieval.com/wp-content/uploads/2019/10/Components-of-a-Medical-Record.jpg"
            className="hover:opacity-50 rounded-3xl  transition-all duration-300 ease-in-out"
          />
          <div className="absolute inset-0 flex items-center justify-center bg-black bg-opacity-50 opacity-0 group-hover:opacity-100 transition-opacity duration-300 rounded-3xl">
            <span className="text-white text-lg p-5">
              Create and manage your medical records
            </span>
          </div>
        </div>
        <div className="col-span-1 relative group drop-shadow-lg">
          <img
            src="https://cdn.prod.website-files.com/629767a3b4a5c62d9ac4f16e/62eba59f235d5cec1548cced_Medical-History.jpg"
            className="hover:opacity-50 rounded-3xl  transition-all duration-300 ease-in-out"
          />
          <div className="absolute inset-0 flex items-center justify-center bg-black bg-opacity-50 opacity-0 group-hover:opacity-100 transition-opacity duration-300 rounded-3xl">
            <span className="text-white text-lg">
              Access your medical history anytime
            </span>
          </div>
        </div>
        <div className="col-span-1 relative group drop-shadow-lg">
          <img
            src="https://www.managemore.com/images/appointment.jpg"
            className="hover:opacity-50 rounded-3xl  transition-all duration-300 ease-in-out"
          />
          <div className="absolute inset-0 flex items-center justify-center bg-black bg-opacity-50 opacity-0 group-hover:opacity-100 transition-opacity duration-300 rounded-3xl">
            <span className="text-white text-lg">
              Set up appointments with doctors
            </span>
          </div>
        </div>
      </div>
    </div>
  );
};
