import { SERVER_API } from "./utils";

export const logIn = async (values: { email: string; password: string }) => {
  try {
    console.log(values);
    const response = await SERVER_API.post("/login", values, {
      headers: {
        "Content-Type": "application/json",
      },
      withCredentials: true,
    });
    return response.data;
  } catch (err) {
    return err;
  }
};
