import { setSpecialization } from "@/redux/action/specializationActions";
import { AppDispatch, RootState } from "@/redux/store";
import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { ScrollArea } from "../ui/scroll-area";
import { Separator } from "../ui/separator";
import { Specialization } from "@/redux/reducers/specializationReducer";

export const EditSpecializations = (props: {
  selectedSpecializations: Specialization[];
  setSelectedSpecializations: React.Dispatch<
    React.SetStateAction<Specialization[]>
  >;
}) => {
  const data = useSelector((state: RootState) => state.specialization);
  const { selectedSpecializations, setSelectedSpecializations } = props;
  const dispatch = useDispatch<AppDispatch>();

  useEffect(() => {
    const getSpecialization = async () => {
      dispatch(setSpecialization());
    };
    getSpecialization();
  }, [dispatch]);
  const toggleSelection = (item: Specialization) => {
    const isSelected = selectedSpecializations.some(
      (spec) => spec.id === item.id
    );
    if (isSelected) {
      setSelectedSpecializations(
        selectedSpecializations.filter((spec) => spec.id !== item.id)
      );
    } else {
      setSelectedSpecializations([...selectedSpecializations, item]);
    }
  };

  return (
    <>
      <ScrollArea className="h-40 w-48 rounded-md border">
        <div className="p-4">
          <h4 className="mb-4 text-sm font-medium leading-none">
            Medical Specializations
          </h4>
          {data.map((item) => (
            <div
              key={item.id}
              className={`cursor-pointer rounded-sm transition-all ${
                selectedSpecializations.some((spec) => spec.id === item.id)
                  ? "bg-slate-200 hover:bg-slate-100"
                  : "hover:bg-slate-100"
              }`}
              onClick={() => toggleSelection(item)}
            >
              <div className="text-sm">{item.specializationName}</div>
              <Separator className="my-2" />
            </div>
          ))}
        </div>
      </ScrollArea>
    </>
  );
};
