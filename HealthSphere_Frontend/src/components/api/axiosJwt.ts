import axios from "axios";

export default async function getJwtToken(): Promise<string> {
  try {
    const response = await axios.get<{ accessToken: string }>(
      "http://localhost:8000/api/v1/get-jwt",
      {
        withCredentials: true,
      }
    );
    return response.data.accessToken;
  } catch (err) {
    console.error("Failed to fetch jwt token", err);
    throw err;
  }
}
