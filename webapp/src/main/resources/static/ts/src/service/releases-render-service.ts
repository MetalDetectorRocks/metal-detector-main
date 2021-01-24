import {AbstractRenderService} from "./abstract-render-service";
import {ReleasesResponse} from "../model/releases-response.model";
import {PaginationComponent} from "../components/pagination/pagination-component";
import {AlertService} from "./alert-service";
import {LoadingIndicatorService} from "./loading-indicator-service";
import {Pagination} from "../model/pagination.model";
import {Release} from "../model/release.model";
import {DateFormat, DateService} from "./date-service";
import {ReleasesService} from "./releases-service";

export class ReleasesRenderService extends AbstractRenderService<ReleasesResponse> {

    private readonly dateService: DateService;
    private readonly paginationComponent: PaginationComponent;
    private readonly releaseTemplateElement: HTMLTemplateElement;
    private readonly sortPropertySelector!: HTMLSelectElement;

    constructor(dateService: DateService, alertService: AlertService, loadingIndicatorService: LoadingIndicatorService) {
        super(alertService, loadingIndicatorService);
        this.dateService = dateService;
        this.paginationComponent = new PaginationComponent();
        this.releaseTemplateElement = document.getElementById("detailed-release-card") as HTMLTemplateElement;
        this.sortPropertySelector = document.getElementById("sort-property-selector") as HTMLSelectElement;
    }

    protected getHostElementId(): string {
        return "releases-container";
    }

    protected onRendering(data: ReleasesResponse): void {
        if (data.items.length === 0) {
            this.showNoReleasesFoundInfoMessage();
        }
        else {
            data.items.forEach(release => {
                const releaseDivElement = this.renderReleaseCard(release);
                this.hostElement.insertAdjacentElement("beforeend", releaseDivElement);
            });
        }

        const pagination = data.pagination;
        if (pagination.totalPages > 1) {
            this.attachPagination(pagination);
        }
    }

    private showNoReleasesFoundInfoMessage(): void {
        const message = `<h3 class="h5">No releases could be found.</h3>Try to follow more artist to see upcoming releases here.`;
        const infoMessage = this.alertService.renderInfoAlert(message, false);
        this.hostElement.insertAdjacentElement("afterbegin", infoMessage);
    }

    private renderReleaseCard(release: Release): HTMLDivElement {
        const releaseTemplateNode = document.importNode(this.releaseTemplateElement.content, true);
        const releaseDivElement = releaseTemplateNode.firstElementChild as HTMLDivElement;
        const releaseCoverElement = releaseDivElement.querySelector("#release-cover") as HTMLImageElement;
        const releaseTitleElement = releaseDivElement.querySelector("#release-title") as HTMLParagraphElement;
        const additionalArtistsElement = releaseDivElement.querySelector("#additional-artists") as HTMLDivElement;
        const releaseDateElement = releaseDivElement.querySelector("#release-date") as HTMLElement;
        const announcementDateElement = releaseDivElement.querySelector("#announcement-date") as HTMLElement;
        const releaseTypeElement = releaseDivElement.querySelector("#release-type") as HTMLElement;
        const releaseGenreElement = releaseDivElement.querySelector("#release-genre") as HTMLElement;

        releaseCoverElement.src = release.coverUrl || "/images/unknown-img.jpg";
        releaseTitleElement.textContent = `${release.artist} - ${release.albumTitle}`;

        release.additionalArtists === null || release.additionalArtists.length === 0
            ? releaseTemplateNode.getElementById("additional-artists-wrapper")!.remove()
            : additionalArtistsElement.textContent = release.additionalArtists.join(", ");

        releaseDateElement.textContent = release.releaseDate?.length > 0
            ? this.dateService.format(release.releaseDate, DateFormat.LONG)
            : release.estimatedReleaseDate;

        this.sortPropertySelector.value === ReleasesService.SORT_BY_ANNOUNCEMENT_DATE_OPTION_VALUE
            ? announcementDateElement.textContent = this.dateService.format(release.announcementDate, DateFormat.LONG)
            : releaseDivElement.querySelector("#announcement-date-wrapper")!.remove();

        releaseTypeElement.textContent = release.type || "n/a";
        releaseGenreElement.textContent = release.genre || "n/a";

        return releaseDivElement;
    }

    private attachPagination(paginationData: Pagination) {
        const paginationList = this.paginationComponent.render(paginationData);
        this.hostElement.insertAdjacentElement("beforeend", paginationList);
    }
}
