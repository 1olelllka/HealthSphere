import { CSSProperties } from "react";
import BeatLoader from "react-spinners/BeatLoader";

const override: CSSProperties = {
  display: "flex",
  height: "100vh",
  justifyContent: "center",
  alignItems: "center",
};

export const LoadingPage = () => {
  return (
    <div className="bg-primary">
      <BeatLoader
        color={"#36D7B7"}
        loading={true}
        size={20}
        cssOverride={override}
      />
    </div>
  );
};
