import { axiosConfig } from "../config/axios.config";
import axios, { AxiosError, AxiosResponse } from "axios";
import { ReleasesResponse } from "../model/releases-response.model";
import { DateService } from "../service/util/date-service";
import { UrlService } from "../service/util/url-service";

export class ReleasesRestClient {
    private readonly RELEASES_URL = "/rest/v1/releases";
    private readonly MY_RELEASES_URL = "/rest/v1/releases/my";

    private readonly urlService: UrlService;
    private readonly dateService: DateService;

    constructor(urlService: UrlService, dateService: DateService) {
        this.urlService = urlService;
        this.dateService = dateService;
    }

    public async fetchAllReleases(): Promise<ReleasesResponse> {
        return await this.fetchReleases(this.RELEASES_URL);
    }

    public async fetchMyReleases(): Promise<ReleasesResponse> {
        return await this.fetchReleases(this.MY_RELEASES_URL);
    }

    private fetchReleases(url: string): Promise<ReleasesResponse> {
        const sort = this.urlService.getParameterFromUrl("sort");
        const direction = this.urlService.getParameterFromUrl("direction");
        const query = this.urlService.getParameterFromUrl("query");
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
}
