import { ReleasesRestClient } from "../clients/releases-rest-client";
import { ReleasesRenderService } from "./releases-render-service";
import { UrlService } from "./url-service";
import { DateService } from "./date-service";

export class ReleasesService {
    private static readonly RELEASES_PARAM_NAME = "releases";
    private static readonly ALL_RELEASES_PARAM_VALUE = "all";
    private static readonly MY_RELEASES_PARAM_VALUE = "my";
    private static readonly DATE_FROM_PARAM_VALUE = "dateFrom";
    private static readonly DATE_TO_PARAM_VALUE = "dateTo";

    private static readonly SORT_BY_RELEASE_DATE_OPTION_VALUE = "Release date";
    static readonly SORT_BY_ANNOUNCEMENT_DATE_OPTION_VALUE = "Announcement date";
    private static readonly SORT_BY_PARAM_NAME = "sort";
    private static readonly SORT_BY_RELEASE_DATE_PARAM_VALUE = "release_date";
    private static readonly SORT_BY_ANNOUNCEMENT_PARAM_VALUE = "announcement_date";
    private static readonly SORT_DIRECTION_PARAM_NAME = "direction";
    private static readonly SORT_DIRECTION_ASC_PARAM_VALUE = "asc";
    private static readonly SORT_DIRECTION_DESC_PARAM_VALUE = "desc";

    private static readonly QUERY_PARAM_VALUE = "query";

    private readonly releasesRestClient: ReleasesRestClient;
    private readonly releasesRenderService: ReleasesRenderService;
    private readonly urlService: UrlService;
    private readonly dateService: DateService;

    private allArtistsRb!: HTMLInputElement;
    private followedArtistsRb!: HTMLInputElement;
    private sortPropertySelector!: HTMLSelectElement;
    private sortAscRb!: HTMLInputElement;
    private sortDescRb!: HTMLInputElement;
    private searchField!: HTMLInputElement;
    private timeAllUpcomingRb!: HTMLInputElement;
    private timeNextMonthRb!: HTMLInputElement;
    private timeLastMonthRb!: HTMLInputElement;

    constructor(
        releasesRestClient: ReleasesRestClient,
        releasesRenderService: ReleasesRenderService,
        urlService: UrlService,
        dateService: DateService,
    ) {
        this.releasesRestClient = releasesRestClient;
        this.releasesRenderService = releasesRenderService;
        this.urlService = urlService;
        this.dateService = dateService;
        this.initDocumentElements();
        this.initFilterValuesFromUrl();
        this.addSortPropertyEventListener();
        this.addSortingEventListener();
        this.addReleaseFilterEventListener();
        this.addSearchEventListener();
        this.addTimeEventListener();
    }

    private initDocumentElements(): void {
        this.allArtistsRb = document.getElementById("all-artists-rb") as HTMLInputElement;
        this.followedArtistsRb = document.getElementById("followed-artists-rb") as HTMLInputElement;
        this.sortPropertySelector = document.getElementById("sort-property-selector") as HTMLSelectElement;
        this.sortAscRb = document.getElementById("sort-asc-rb") as HTMLInputElement;
        this.sortDescRb = document.getElementById("sort-desc-rb") as HTMLInputElement;
        this.searchField = document.getElementById("release-search") as HTMLInputElement;
        this.timeAllUpcomingRb = document.getElementById("time-all-upcoming-rb") as HTMLInputElement;
        this.timeNextMonthRb = document.getElementById("time-next-month-rb") as HTMLInputElement;
        this.timeLastMonthRb = document.getElementById("time-last-month-rb") as HTMLInputElement;
    }

    private initFilterValuesFromUrl(): void {
        const releasesParamValue = this.urlService.getParameterFromUrl(ReleasesService.RELEASES_PARAM_NAME);
        const sortParamValue = this.urlService.getParameterFromUrl(ReleasesService.SORT_BY_PARAM_NAME);
        const directionParamValue = this.urlService.getParameterFromUrl(ReleasesService.SORT_DIRECTION_PARAM_NAME);
        const searchQueryParamValue = this.urlService.getParameterFromUrl(ReleasesService.QUERY_PARAM_VALUE);
        const dateFromParamValue = this.urlService.getParameterFromUrl(ReleasesService.DATE_FROM_PARAM_VALUE);
        const dateToParamValue = this.urlService.getParameterFromUrl(ReleasesService.DATE_TO_PARAM_VALUE);

        this.followedArtistsRb.checked = releasesParamValue === ReleasesService.MY_RELEASES_PARAM_VALUE;
        this.allArtistsRb.checked = !this.followedArtistsRb.checked;
        this.sortPropertySelector.value =
            sortParamValue.length === 0 || sortParamValue === ReleasesService.SORT_BY_RELEASE_DATE_PARAM_VALUE
                ? ReleasesService.SORT_BY_RELEASE_DATE_OPTION_VALUE
                : ReleasesService.SORT_BY_ANNOUNCEMENT_DATE_OPTION_VALUE;
        this.sortDescRb.checked = directionParamValue === ReleasesService.SORT_DIRECTION_DESC_PARAM_VALUE;
        this.sortAscRb.checked = !this.sortDescRb.checked;
        this.searchField.value = searchQueryParamValue;
        this.timeAllUpcomingRb.checked = dateToParamValue === "";
        this.timeNextMonthRb.checked =
            !this.timeAllUpcomingRb.checked && Date.parse(dateFromParamValue) == Date.parse(this.dateService.today());
        this.timeLastMonthRb.checked = !this.timeAllUpcomingRb.checked && !this.timeNextMonthRb.checked;
    }

    public fetchReleases(): void {
        this.allArtistsRb.checked ? this.fetchAllReleases() : this.fetchMyReleases();
    }

    private fetchAllReleases(): void {
        const response = this.releasesRestClient.fetchAllReleases();
        this.releasesRenderService.render(response);
    }

    private fetchMyReleases(): void {
        const response = this.releasesRestClient.fetchMyReleases();
        this.releasesRenderService.render(response);
    }

    private addReleaseFilterEventListener(): void {
        const releaseFilterForm = document.getElementById("release-filter-form") as HTMLFormElement;
        const releaseFilterOptions = releaseFilterForm.elements.namedItem("releases") as RadioNodeList;
        releaseFilterOptions.forEach((option) => {
            option.addEventListener("change", this.onAnyValueChange.bind(this));
        });
    }

    private addSortingEventListener(): void {
        const releaseFilterForm = document.getElementById("sorting-form") as HTMLFormElement;
        const releaseFilterOptions = releaseFilterForm.elements.namedItem("releases") as RadioNodeList;
        releaseFilterOptions.forEach((option) => {
            option.addEventListener("change", this.onAnyValueChange.bind(this));
        });
    }

    private addSortPropertyEventListener(): void {
        this.sortPropertySelector.addEventListener("change", this.onAnyValueChange.bind(this));
    }

    private addSearchEventListener(): void {
        this.searchField.addEventListener("change", this.onAnyValueChange.bind(this));
    }

    private addTimeEventListener(): void {
        this.timeAllUpcomingRb.addEventListener("change", this.onAnyValueChange.bind(this));
        this.timeNextMonthRb.addEventListener("change", this.onAnyValueChange.bind(this));
        this.timeLastMonthRb.addEventListener("change", this.onAnyValueChange.bind(this));
    }

    private onAnyValueChange(): void {
        const releasesFilterValue = this.allArtistsRb.checked
            ? ReleasesService.ALL_RELEASES_PARAM_VALUE
            : ReleasesService.MY_RELEASES_PARAM_VALUE;
        const sortBy =
            this.sortPropertySelector.value === ReleasesService.SORT_BY_RELEASE_DATE_OPTION_VALUE
                ? ReleasesService.SORT_BY_RELEASE_DATE_PARAM_VALUE
                : ReleasesService.SORT_BY_ANNOUNCEMENT_PARAM_VALUE;
        const sortDirection = this.sortAscRb.checked
            ? ReleasesService.SORT_DIRECTION_ASC_PARAM_VALUE
            : ReleasesService.SORT_DIRECTION_DESC_PARAM_VALUE;
        const dateFrom = this.timeLastMonthRb.checked ? this.dateService.beforeAMonth() : this.dateService.today();
        const dateTo = this.timeNextMonthRb.checked
            ? this.dateService.inAMonth()
            : this.timeLastMonthRb.checked
            ? this.dateService.today()
            : "";

        const urlSearchParams = new URLSearchParams();
        urlSearchParams.set("page", "1");
        urlSearchParams.set(ReleasesService.RELEASES_PARAM_NAME, releasesFilterValue);
        urlSearchParams.set(ReleasesService.SORT_BY_PARAM_NAME, sortBy);
        urlSearchParams.set(ReleasesService.SORT_DIRECTION_PARAM_NAME, sortDirection);
        urlSearchParams.set(ReleasesService.QUERY_PARAM_VALUE, this.searchField.value);
        urlSearchParams.set(ReleasesService.DATE_FROM_PARAM_VALUE, dateFrom);
        urlSearchParams.set(ReleasesService.DATE_TO_PARAM_VALUE, dateTo);

        window.location.href = "?" + urlSearchParams.toString();
    }
}
