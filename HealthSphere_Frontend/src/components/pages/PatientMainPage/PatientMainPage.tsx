import "../../../index.css";
import MedicalRecords from "./MedicalRecords";
import PatientInfo from "./PatientInfo";

export default function PatientMainpage() {
  return (
    <>
      <div className="Page h-screen px-10">
        <h1 className="text-4xl pt-10">Information about the Patient</h1>
        <div className="flex row grid grid-cols-2 gap-48">
          <div>
            <PatientInfo />
          </div>
          <div>
            <MedicalRecords />
          </div>
        </div>
      </div>
    </>
  );
}
