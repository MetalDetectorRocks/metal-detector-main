import {axiosConfig} from "../config/axios.config";
import axios, {AxiosError, AxiosResponse} from "axios";
import {ReleasesResponse} from "../model/releases-response.model";
import {DateService} from "../service/date-service";
import {UrlService} from "../service/url-service";

export class ReleasesRestClient {

    private readonly RELEASES_URL = "/rest/v1/releases";
    private readonly MY_RELEASES_URL = "/rest/v1/releases/my";

    private readonly urlService: UrlService;
    private readonly dateFormatService: DateService;

    constructor(urlService: UrlService, dateFormatService: DateService) {
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
        const sortParameter = parameters.length > 0 ? [parameters[0], parameters[1], parameters[2]] : [];
        axiosConfig.params = {
            page: this.urlService.getPageFromUrl(),
            size: 30,
            dateFrom: this.dateFormatService.yesterday(),
            sort: sortParameter
        }

        return axios.get(
          url,
          axiosConfig
        ).then((response: AxiosResponse<ReleasesResponse>) => {
            return response.data;
        }).catch((error: AxiosError) => {
            console.error(error);
            throw error;
        });
    }
}
