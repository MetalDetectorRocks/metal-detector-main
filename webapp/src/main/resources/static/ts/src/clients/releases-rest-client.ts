import { axiosConfig } from "../config/axios.config";
import axios, { AxiosError, AxiosResponse } from "axios";
import { ReleasesResponse } from "../model/releases-response.model";
import { AbstractRestClient } from "./abstract-rest-client";

export class ReleasesRestClient extends AbstractRestClient {

    private readonly RELEASES_URL = "/rest/v1/releases";

    constructor() {
        super();
    }

    public async fetchReleases(): Promise<ReleasesResponse> {
        const request = {
            page: this.getPageFromUrl(),
            size: 30,
            artists: []
        }

        return await axios.post(
            this.RELEASES_URL,
            request,
            axiosConfig
        ).then((response: AxiosResponse<ReleasesResponse>) => {
            return response.data;
        }).catch((error: AxiosError) => {
            console.error(error);
            throw error;
        });
    }
}
