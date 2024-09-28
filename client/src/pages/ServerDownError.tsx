import ClimbingBoxLoader from "react-spinners/ClimbingBoxLoader";

export const ServerDownError = () => {
  return (
    <>
      <div className="h-[70vh] flex items-center justify-end flex-col">
        <h1 className="text-4xl text-[#040D12]font-light text-center">
          Opps...
        </h1>
        <h1 className="text-9xl text-[#040D12] font-semibold text-center [text-shadow:3px_3px_4px_var(--tw-shadow-color)] shadow-[#93B1A6]">
          521
        </h1>
        <h2 className="text-[#040D12]">
          Server is down. It wants some rest too :)
        </h2>
        <ClimbingBoxLoader color="#93B1A6" />
      </div>
    </>
  );
};
