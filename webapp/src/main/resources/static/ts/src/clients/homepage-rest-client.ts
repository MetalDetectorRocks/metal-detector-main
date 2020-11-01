import axios, {AxiosError, AxiosResponse} from "axios";
import {axiosConfig} from "../config/axios.config";
import {HomepageResponse} from "../model/homepage-response.model";

export class HomepageRestClient {

    private readonly HOMEPAGE_URL = "/rest/v1/home"

    public async fetchHomepage(): Promise<HomepageResponse> {
        return await axios.get(
          this.HOMEPAGE_URL, axiosConfig
        ).then((response: AxiosResponse<HomepageResponse>) => {
            return response.data;
        }).catch((error: AxiosError) => {
            console.error(error);
            throw error;
        });
    }
}
