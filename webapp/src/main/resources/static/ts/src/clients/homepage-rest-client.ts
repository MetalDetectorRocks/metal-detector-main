import axios, { AxiosError, AxiosResponse } from "axios";
import { axiosConfig } from "../config/axios.config";
import { HomepageResponse } from "../model/homepage-response.model";
import { AbstractRestClient } from "./abstract-rest-client";

export class HomepageRestClient extends AbstractRestClient {

    private readonly HOMEPAGE_URL = "/rest/v1/home"

    constructor() {
        super();
    }

    public async fetchHomepage(): Promise<HomepageResponse> {
        return axios.get(
          this.HOMEPAGE_URL, axiosConfig
        ).then((response: AxiosResponse<HomepageResponse>) => {
            return response.data;
        }).catch((error: AxiosError) => {
            console.error(error);
            throw error;
        });
    }
}
