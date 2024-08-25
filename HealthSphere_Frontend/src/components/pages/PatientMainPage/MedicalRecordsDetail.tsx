import { Button } from "@/components/ui/button";
import {
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogClose,
  DialogDescription,
  DialogTitle,
} from "@/components/ui/dialog";
import { Label } from "@/components/ui/label";
import { useState, useEffect } from "react";
import axios from "axios";
import getJwtToken from "@/components/api/axiosJwt";

export default function MedicalRecordsDetail(props: { id: number }) {
  const { id } = props;
  const [details, setDetails] = useState({});
  useEffect(() => {
    const fetchDetails = async () => {
      const jwtToken = await getJwtToken();
      const response = await axios.get(
        "http://localhost:8000/api/v1/patient/medical-records/" + id,
        {
          headers: {
            Authorization: `Bearer ${jwtToken}`,
          },
        }
      );
      console.log("server call");
      setDetails(response.data);
    };
    fetchDetails();
  }, [id]);

  return (
    <DialogContent>
      <DialogHeader className="text-2xl">
        <DialogTitle>
          Medical Record -{" "}
          {details?.recordDate
            ? new Date(details?.recordDate).toLocaleDateString()
            : "N/A"}
        </DialogTitle>
        <DialogDescription>More details about the record</DialogDescription>
      </DialogHeader>
      <div className="grid gap-4 py-3">
        <div className="grid-cols-4 items-center gap-4">
          <Label className="text-md">
            Doctor: {details?.doctor?.firstName} {details?.doctor?.lastName},
            Spezialization: {details?.doctor?.specialization || "Not Specified"}
          </Label>
        </div>
        <div className="grid-cols-4 items-center gap-4">
          <Label className="text-md">
            Patient: {details?.patient?.firstName} {details?.patient?.lastName},{" "}
            {details?.patient?.gender}
          </Label>
        </div>
        <div className="grid-cols-4 items-center gap-4">
          <Label className="text-md">
            Diagnosis: {details?.diagnosis || "Not Specified"}
          </Label>
        </div>
        <div className="grid-cols-4 items-center gap-4">
          <Label className="text-md">
            Treatment: {details?.treatment || "Not Specified"}
          </Label>
        </div>
        {/* TODO: Add prescription field when implemented */}
        {/* <div className="grid-cols-4 items-center gap-4">
          <Label className="text-md">
            Prescription: {details?.prescription || "Not Specified"}
          </Label>
        </div> */}
      </div>
      <DialogFooter>
        <DialogClose asChild>
          <Button type="button">Close</Button>
        </DialogClose>
      </DialogFooter>
    </DialogContent>
  );
}
