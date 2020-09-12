import {axiosConfig} from "../config/axios.config";
import axios, {AxiosError, AxiosResponse} from "axios";
import {ReleasesResponse} from "../model/releases-response.model";
import {AbstractRestClient} from "./abstract-rest-client";
import {DateFormat, DateFormatService} from "../service/date-format-service";

export class ReleasesRestClient extends AbstractRestClient {

    private readonly RELEASES_URL = "/rest/v1/releases";

    private readonly dateFormatService: DateFormatService;

    constructor(dateFormatService: DateFormatService) {
        super();
        this.dateFormatService = dateFormatService;
    }

    public async fetchReleases(): Promise<ReleasesResponse> {
        axiosConfig.params = {
            page: this.getPageFromUrl(),
            size: 30,
            dateFrom: this.dateFormatService.format(new Date().toUTCString(), DateFormat.UTC)
        }

        return await axios.get(
            this.RELEASES_URL,
            axiosConfig
        ).then((response: AxiosResponse<ReleasesResponse>) => {
            return response.data;
        }).catch((error: AxiosError) => {
            console.error(error);
            throw error;
        });
    }
}
