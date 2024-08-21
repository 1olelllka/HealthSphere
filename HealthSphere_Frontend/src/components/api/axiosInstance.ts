import axios from "axios";
import Cookies from "js-cookie";

const axiosInstance = axios.create({
  withCredentials: true,
  withXSRFToken: true,
});

axiosInstance.interceptors.request.use(
  async (config) => {
    let xsrfToken = Cookies.get("CSRF-TOKEN");

    if (!xsrfToken) {
      try {
        const response = await axios.get(
          "http://localhost:8000/api/v1/csrf-cookie",
          {
            withCredentials: true,
          }
        );
        xsrfToken = response.data.token;
      } catch (err) {
        console.log("Failed to fetch csrf token", err);
      }
    }
    if (xsrfToken) {
      config.headers["X-CSRF-TOKEN"] = xsrfToken;
      Cookies.set("CSRF-TOKEN", xsrfToken);
    }

    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default axiosInstance;
