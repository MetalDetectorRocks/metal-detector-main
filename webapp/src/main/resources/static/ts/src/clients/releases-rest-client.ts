import { axiosConfig } from "../config/axios.config";
import axios, { AxiosError, AxiosResponse } from "axios";
import { ReleasesResponse } from "../model/releases-response.model";
import { DateService } from "../service/util/date-service";
import { UrlService } from "../service/util/url-service";
import { Release } from "../model/release.model";

export class ReleasesRestClient {
    private readonly RELEASES_URL = "/rest/v1/releases";
    private readonly TOP_RELEASES_URL = "/rest/v1/releases/top";

    private readonly urlService: UrlService;
    private readonly dateService: DateService;

    constructor(urlService: UrlService, dateService: DateService) {
        this.urlService = urlService;
        this.dateService = dateService;
    }

    public async fetchReleases(): Promise<ReleasesResponse> {
        return await this.doFetchReleases(this.RELEASES_URL);
    }

    private doFetchReleases(url: string): Promise<ReleasesResponse> {
        const sort = this.urlService.getParameterFromUrl("sort");
        const direction = this.urlService.getParameterFromUrl("direction");
        const query = this.urlService.getParameterFromUrl("query");
        const releasesFilter = this.urlService.getParameterFromUrl("releases");
        const dateFrom = this.urlService.getParameterFromUrl("dateFrom");
        const dateTo = this.urlService.getParameterFromUrl("dateTo");
        axiosConfig.params = {
            page: this.urlService.getPageFromUrl(),
            size: 30,
            dateFrom: dateFrom || this.dateService.today(),
            dateTo: dateTo,
            sort: sort.length === 0 ? "release_date" : sort,
            direction: direction.length === 0 ? "asc" : direction,
            query: query,
            releasesFilter: releasesFilter.length === 0 ? "all" : releasesFilter,
        };

        return axios
            .get(url, axiosConfig)
            .then((response: AxiosResponse<ReleasesResponse>) => {
                return response.data;
            })
            .catch((error: AxiosError) => {
                console.error(error);
                throw error;
            });
    }

    public async fetchTopReleases(): Promise<Release[]> {
        return axios
            .get(this.TOP_RELEASES_URL)
            .then((response: AxiosResponse<Release[]>) => {
                return response.data;
            })
            .catch((error: AxiosError) => {
                console.error(error);
                throw error;
            });
    }
}
