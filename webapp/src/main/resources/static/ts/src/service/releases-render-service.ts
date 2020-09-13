import {AbstractRenderService} from "./abstract-render-service";
import {ReleasesResponse} from "../model/releases-response.model";
import {PaginationComponent} from "../components/pagination/pagination-component";
import {AlertService} from "./alert-service";
import {LoadingIndicatorService} from "./loading-indicator-service";
import {Pagination} from "../model/pagination.model";
import {Release} from "../model/release.model";
import {DateFormat, DateFormatService} from "./date-format-service";

export class ReleasesRenderService extends AbstractRenderService<ReleasesResponse> {

    private readonly dateFormatService: DateFormatService;
    private readonly paginationComponent: PaginationComponent;
    private readonly releaseTemplateElement: HTMLTemplateElement;

    constructor(dateService: DateFormatService, alertService: AlertService, loadingIndicatorService: LoadingIndicatorService) {
        super(alertService, loadingIndicatorService);
        this.dateFormatService = dateService;
        this.paginationComponent = new PaginationComponent();
        this.releaseTemplateElement = document.getElementById("detailed-release-card") as HTMLTemplateElement;
    }

    protected getHostElementId(): string {
        return "releases-container";
    }

    protected onRendering(data: ReleasesResponse): void {
        data.items.forEach(release => {
            const releaseDivElement = this.renderReleaseCard(release);
            this.hostElement.insertAdjacentElement("beforeend", releaseDivElement);
        });

        const pagination = data.pagination;
        if (pagination.totalPages > 1) {
            this.attachPagination(pagination);
        }
    }

    private renderReleaseCard(release: Release): HTMLDivElement {
        const releaseTemplateNode = document.importNode(this.releaseTemplateElement.content, true);
        const releaseDivElement = releaseTemplateNode.firstElementChild as HTMLDivElement;
        const releaseCoverElement = releaseDivElement.querySelector("#release-cover") as HTMLImageElement;
        const releaseTitleElement = releaseDivElement.querySelector("#release-title") as HTMLParagraphElement;
        const additionalArtistsElement = releaseDivElement.querySelector("#additional-artists") as HTMLDivElement;
        const releaseDateElement = releaseDivElement.querySelector("#release-date") as HTMLElement;
        const releaseTypeElement = releaseDivElement.querySelector("#release-type") as HTMLElement;
        const releaseGenreElement = releaseDivElement.querySelector("#release-genre") as HTMLElement;

        releaseCoverElement.src = release.coverUrl || "/images/unknown-img.jpg";
        releaseTitleElement.textContent = `${release.artist} - ${release.albumTitle}`;

        release.additionalArtists === null || release.additionalArtists.length === 0
            ? releaseTemplateNode.getElementById("additional-artists-wrapper")!.remove()
            : additionalArtistsElement.textContent = release.additionalArtists.join(", ");

        releaseDateElement.textContent = release.releaseDate?.length > 0
            ? this.dateFormatService.format(release.releaseDate, DateFormat.LONG)
            : release.estimatedReleaseDate;

        releaseTypeElement.textContent = release.type || "n/a";
        releaseGenreElement.textContent = release.genre || "n/a";

        return releaseDivElement;
    }

    private attachPagination(paginationData: Pagination) {
        const paginationList = this.paginationComponent.render(paginationData);
        this.hostElement.insertAdjacentElement("beforeend", paginationList);
    }
}
