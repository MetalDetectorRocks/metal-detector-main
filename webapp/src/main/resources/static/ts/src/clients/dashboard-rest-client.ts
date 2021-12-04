import axios, { AxiosError, AxiosResponse } from "axios";
import { axiosConfig } from "../config/axios.config";
import { DashboardResponse } from "../model/dashboard-response.model";

export class DashboardRestClient {
    private readonly DASHBOARD_URL = "/rest/v1/dashboard";

    public async fetchDashboard(): Promise<DashboardResponse> {
        return await axios
            .get(this.DASHBOARD_URL, axiosConfig)
            .then((response: AxiosResponse<DashboardResponse>) => {
                return response.data;
            })
            .catch((error: AxiosError) => {
                console.error(error);
                throw error;
            });
    }
}
