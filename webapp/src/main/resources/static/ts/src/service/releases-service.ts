import {ReleasesRestClient} from "../clients/releases-rest-client";
import {ReleasesRenderService} from "./releases-render-service";
import {UrlService} from "./url-service";

export class ReleasesService {

    private static readonly RELEASES_FILTER_NAME = "releases";
    private static readonly ALL_RELEASES_IDENTIFIER = "all";
    private static readonly MY_RELEASES_IDENTIFIER = "my";
    private static readonly SORT_PARAMETER = (parameter: string, direction: string) => `sort=${parameter},${direction}&sort=artist,ASC&sort=albumTitle,ASC`;

    private readonly releasesRestClient: ReleasesRestClient;
    private readonly releasesRenderService: ReleasesRenderService;
    private readonly urlService: UrlService;

    private allArtistsRb!: HTMLInputElement;
    private followedArtistsRb!: HTMLInputElement;
    private sortPropertySelector!: HTMLSelectElement;
    private sortAscRb!: HTMLInputElement;
    private sortDescRb!: HTMLInputElement;

    constructor(releasesRestClient: ReleasesRestClient, releasesRenderService: ReleasesRenderService, urlService: UrlService) {
        this.releasesRestClient = releasesRestClient;
        this.releasesRenderService = releasesRenderService;
        this.urlService = urlService;
        this.initDocumentElements();
        this.initFilterValuesFromUrl();
        this.addSortPropertyEventListener();
        this.addSortingEventListener();
        this.addReleaseFilterEventListener();
    }

    private initDocumentElements(): void {
        this.allArtistsRb = document.getElementById("all-artists-rb") as HTMLInputElement;
        this.followedArtistsRb = document.getElementById("followed-artists-rb") as HTMLInputElement;
        this.sortPropertySelector = document.getElementById("sort-property-selector") as HTMLSelectElement;
        this.sortAscRb = document.getElementById("sort-asc-rb") as HTMLInputElement;
        this.sortDescRb = document.getElementById("sort-desc-rb") as HTMLInputElement;
    }

    private initFilterValuesFromUrl(): void {
        const releasesFilter = this.urlService.getParameterFromUrl("releases");
        const parameters = this.urlService.getParametersFromUrl("sort");
        let sortSelector;
        let sortAsc;

        if (parameters.length > 0) {
            sortSelector = parameters[0].split(",")[0] == "releaseDate" ? "Release date" : "Announcement date";
            sortAsc = parameters[0].split(",")[1] === "ASC"
        } else {
            sortSelector = "Release date";
            sortAsc = true
        }

        this.allArtistsRb.checked = releasesFilter !== ReleasesService.MY_RELEASES_IDENTIFIER;
        this.followedArtistsRb.checked = releasesFilter === ReleasesService.MY_RELEASES_IDENTIFIER;
        this.sortPropertySelector.value = sortSelector;
        this.sortAscRb.checked = sortAsc;
        this.sortDescRb.checked = !sortAsc;
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
        releaseFilterOptions.forEach(option => {
            option.addEventListener("change", this.onValueChange.bind(this));
        });
    }

    private addSortingEventListener(): void {
        const releaseFilterForm = document.getElementById("sorting-form") as HTMLFormElement;
        const releaseFilterOptions = releaseFilterForm.elements.namedItem("releases") as RadioNodeList;
        releaseFilterOptions.forEach(option => {
            option.addEventListener("change", this.onValueChange.bind(this));
        });
    }

    private addSortPropertyEventListener(): void {
        const sortPropertySelector = document.getElementById("sort-property-selector") as HTMLSelectElement;
        sortPropertySelector.addEventListener("change", this.onValueChange.bind(this));
    }

    private onValueChange(): void {
        const releasesFilterValue = this.allArtistsRb.checked ? ReleasesService.ALL_RELEASES_IDENTIFIER : ReleasesService.MY_RELEASES_IDENTIFIER;
        const sortProperty = this.sortPropertySelector.value == "Release date" ? "releaseDate" : "createdDateTime";
        let sortingValue;

        if (this.sortAscRb.checked) {
            sortingValue = ReleasesService.SORT_PARAMETER(sortProperty, "ASC");
        } else {
            sortingValue = ReleasesService.SORT_PARAMETER(sortProperty, "DESC");
        }

        window.location.href = `?page=1&${ReleasesService.RELEASES_FILTER_NAME}=${releasesFilterValue}&${sortingValue}`;
    }
}
