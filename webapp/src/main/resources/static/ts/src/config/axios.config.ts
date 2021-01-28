import * as qs from "qs";
import { PathLike } from "fs";
import { AxiosRequestConfig } from "axios";

export const axiosConfig: AxiosRequestConfig = {
    withCredentials: false,
    timeout: 60000,
    headers: {
        common: {
            "Cache-Control": "no-cache, no-store, must-revalidate",
            Pragma: "no-cache",
            "Content-Type": "application/json",
            Accept: "application/json",
        },
    },
    paramsSerializer: (params: PathLike) => qs.stringify(params, { indices: false }),
};
