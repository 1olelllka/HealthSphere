export const MainNavbar = () => {
  return (
    <>
      <h1
        className="text-2xl font-bold cursor-pointer hover:text-[#183D3D] transition-all col-span-8"
        onClick={() => window.scrollTo(0, 0)}
      >
        HealthSphere
      </h1>
      <div className="flex flex-row space-x-5 justify-end col-span-1">
        <h1 className="text-xl text-[#040D12] cursor-pointer">Log In</h1>
        <h1 className="text-xl text-[#040D12]">Sign Up</h1>
      </div>
    </>
  );
};
