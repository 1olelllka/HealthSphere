import { Link } from "react-router-dom";
import { Button } from "@/components/ui/button";

export default function ErrorPage() {
  return (
    <>
      <h1 className="text-4xl">Opps</h1>
      <h3>404 - Page Not Found</h3>
      <Button variant="outline">
        <Link to="/">Home</Link>
      </Button>
    </>
  );
}
