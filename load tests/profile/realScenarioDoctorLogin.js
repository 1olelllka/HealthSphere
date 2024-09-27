import http from "k6/http";
import { sleep, check } from "k6";
export const options = {
  thresholds: {
    http_req_failed: [{ threshold: "rate<0.01", abortOnFail: true }],
    http_req_duration: ["p(95)<2500"],
  },
  scenarios: {
    constant_request_rate: {
      executor: "constant-arrival-rate",
      rate: 100,
      preAllocatedVUs: 100,
      timeUnit: "1s",
      duration: "1m",
    },
    average_load: {
      executor: "ramping-vus",
      stages: [
        { duration: "10s", target: 20 },
        { duration: "50s", target: 20 },
        { duration: "50s", target: 40 },
        { duration: "50s", target: 60 },
        { duration: "50s", target: 80 },
        { duration: "50s", target: 100 },
        { duration: "50s", target: 120 },
        { duration: "50s", target: 140 },
      ],
    },
    stress_test: {
      executor: "ramping-vus",
      stages: [
        { duration: "10s", target: 200 },
        { duration: "50s", target: 300 },
        { duration: "10s", target: 70 },
      ],
    },
  },
};

export default function () {
  // log in
  const loginResponse = http.post(
    `http://localhost:8000/api/v1/login`,
    JSON.stringify({
      email: "iDoctor@email.com",
      password: "olehtop2282",
    }),
    {
      headers: { "Content-Type": "application/json" },
    }
  );
  check(loginResponse, {
    "login successful": (r) => r.status === 200,
    "access token present": (r) => r.cookies["accessToken"] !== undefined,
  });
  // JWT
  const jwtResponse = http.get(`http://localhost:8000/api/v1/jwt`, {
    headers: {
      Cookie: `accessToken=${loginResponse.cookies["accessToken"][0].value}`, // Manually set the cookie
    },
  });
  check(jwtResponse, {
    "JWT fetch successful": (r) => r.status === 200,
    "access token present in JWT response": (r) =>
      r.json().accessToken !== undefined,
  });

  // Get profile
  const accessToken = jwtResponse.json().accessToken;
  const profileResponse = http.get(`http://localhost:8000/api/v1/doctors/me`, {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });
  check(profileResponse, {
    "profile fetch successful": (r) => r.status === 200,
    "profile data present": (r) => r.json().user.email === "iDoctor@email.com",
  });
  sleep(1);
}
