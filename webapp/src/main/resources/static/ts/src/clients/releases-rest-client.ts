import {axiosConfig} from "../config/axios.config";
import axios, {AxiosError, AxiosResponse} from "axios";
import {ReleasesResponse} from "../model/releases-response.model";
import {DateFormat, DateFormatService} from "../service/date-format-service";
import {UrlService} from "../service/url-service";

export class ReleasesRestClient {

    private readonly RELEASES_URL = "/rest/v1/releases";
    private readonly MY_RELEASES_URL = "/rest/v1/releases/my";

    private readonly urlService: UrlService;
    private readonly dateFormatService: DateFormatService;

    constructor(urlService: UrlService, dateFormatService: DateFormatService) {
        this.urlService = urlService;
        this.dateFormatService = dateFormatService;
    }

    public async fetchAllReleases(): Promise<ReleasesResponse> {
        return await this.fetchReleases(this.RELEASES_URL);
    }

    public async fetchMyReleases(): Promise<ReleasesResponse> {
        return await this.fetchReleases(this.MY_RELEASES_URL);
    }

    private fetchReleases(url: string): Promise<ReleasesResponse> {
        const parameters = this.urlService.getParametersFromUrl("sort");
        const sortParameter = parameters.length > 0 ? "?sort=" + parameters[0] + "&sort=" + parameters[1] + "&sort=" + parameters[2] : "";
        axiosConfig.params = {
            page: this.urlService.getPageFromUrl(),
            size: 30,
            dateFrom: this.dateFormatService.format(new Date().toUTCString(), DateFormat.UTC)
        }

        return axios.get(
            url + sortParameter,
            axiosConfig
        ).then((response: AxiosResponse<ReleasesResponse>) => {
            return response.data;
        }).catch((error: AxiosError) => {
            console.error(error);
            throw error;
        });
    }
}
