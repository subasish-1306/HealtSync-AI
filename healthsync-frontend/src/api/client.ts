import axios from "axios";
import { setupAxiosInterceptors } from "../interceptors/axiosInterceptor";

export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 60000,
  headers: {
    "Content-Type": "application/json",
  },
});

setupAxiosInterceptors(apiClient);

export default apiClient;