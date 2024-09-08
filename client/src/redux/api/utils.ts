import axios from "axios";

const BASE_URL = "http://localhost:8000";
const SERVER = "/api/v1";

export const SERVER_API = axios.create({
  baseURL: BASE_URL + SERVER,
});

const tokenFetcher = axios.create({
  baseURL: BASE_URL + SERVER,
  withCredentials: true,
});

SERVER_API.interceptors.request.use(async (req) => {
  try {
    const response = await tokenFetcher.get("/get-jwt");
    const accessToken = response.data?.accessToken;
    if (accessToken) {
      req.headers = req.headers || {};
      req.headers.Authorization = "Bearer " + accessToken;
    }
  } catch (error) {
    console.error("Failed to retrieve access token:", error);
  }
  return req;
});
