import { AxiosRequestConfig } from "axios";

export const axiosConfig: AxiosRequestConfig = {
    withCredentials: false,
    timeout: 60000,
    headers: {
        "Cache-Control": "no-cache, no-store, must-revalidate",
        Pragma: "no-cache",
        "Content-Type": "application/json",
        Accept: "application/json",
    },
};
