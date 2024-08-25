import getJwtToken from "@/components/api/axiosJwt";
import { useEffect, useState } from "react";
import axios from "axios";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Label } from "@/components/ui/label";
import { Dialog, DialogTrigger } from "@/components/ui/dialog";
import MedicalRecordsDetail from "./MedicalRecordsDetail";

type Record = {
  id: number;
  diagnosis: string;
  doctor: {
    firstName: string;
    lastName: string;
  };
  recordDate: Date;
  treatment: string;
};

export default function MedicalRecords() {
  const [records, setRecords] = useState<Record[] | null>([]);

  useEffect(() => {
    const fetchRecords = async () => {
      const jwtToken = await getJwtToken();
      const response = await axios.get(
        "http://localhost:8000/api/v1/patient/medical-records",
        {
          headers: {
            Authorization: `Bearer ${jwtToken}`,
          },
        }
      );
      setRecords(response.data);
      console.log("server call");
    };
    fetchRecords();
  }, []);

  return (
    <>
      <div>
        <h2 className="text-2xl pt-5">Medical Records</h2>
        <div>
          {records?.map((record: Record) => (
            <Dialog>
              <DialogTrigger asChild>
                <Card
                  className="mt-5 hover:bg-slate-200 transition duration-300 ease-in-out"
                  key={record.id}
                >
                  <CardHeader>
                    <CardTitle>
                      {record?.doctor.firstName} {record?.doctor.lastName}
                    </CardTitle>
                    <CardDescription>
                      {record?.recordDate
                        ? new Date(record?.recordDate).toLocaleDateString()
                        : "N/A"}
                    </CardDescription>
                  </CardHeader>
                  <CardContent>
                    <Label className="text-lg">{record?.diagnosis}</Label>
                  </CardContent>
                  <CardFooter>
                    <p>Treatment: {record?.treatment}</p>
                  </CardFooter>
                </Card>
              </DialogTrigger>
              <MedicalRecordsDetail id={record?.id} />
            </Dialog>
          ))}
        </div>
      </div>
    </>
  );
}
