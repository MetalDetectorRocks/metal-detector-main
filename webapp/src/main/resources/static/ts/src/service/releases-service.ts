import {ReleasesRestClient} from "../clients/releases-rest-client";
import {ReleasesRenderService} from "./releases-render-service";
import {UrlService} from "./url-service";

export class ReleasesService {

    private static readonly RELEASES_FILTER_NAME = "releases";
    private static readonly ALL_RELEASES_IDENTIFIER = "all";
    private static readonly MY_RELEASES_IDENTIFIER = "my";

    private readonly releasesRestClient: ReleasesRestClient;
    private readonly releasesRenderService: ReleasesRenderService;
    private readonly urlService: UrlService;

    private allArtistsRb!: HTMLInputElement;
    private followedArtistsRb!: HTMLInputElement;

    constructor(releasesRestClient: ReleasesRestClient, releasesRenderService: ReleasesRenderService, urlService: UrlService) {
        this.releasesRestClient = releasesRestClient;
        this.releasesRenderService = releasesRenderService;
        this.urlService = urlService;
        this.initDocumentElements();
        this.initFilterValuesFromUrl();
        this.addReleaseFilterEventListener();
    }

    private initDocumentElements(): void {
        this.allArtistsRb = document.getElementById("all-artists-rb") as HTMLInputElement;
        this.followedArtistsRb = document.getElementById("followed-artists-rb") as HTMLInputElement;
    }

    private initFilterValuesFromUrl(): void {
        const releasesFilter = this.urlService.getParameterFromUrl("releases");
        this.allArtistsRb.checked = releasesFilter !== ReleasesService.MY_RELEASES_IDENTIFIER;
        this.followedArtistsRb.checked = releasesFilter === ReleasesService.MY_RELEASES_IDENTIFIER;
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
            option.addEventListener("change", this.onFilterValueChange.bind(this));
        });
    }

    private onFilterValueChange(): void {
        const releasesFilterValue = this.allArtistsRb.checked ? ReleasesService.ALL_RELEASES_IDENTIFIER : ReleasesService.MY_RELEASES_IDENTIFIER;
        window.location.href = `?page=1&${ReleasesService.RELEASES_FILTER_NAME}=${releasesFilterValue}`;
    }
}
