import { AbstractRenderService } from "./abstract-render-service";
import { AlertService } from "../util/alert-service";
import { LoadingIndicatorService } from "../util/loading-indicator-service";
import { SwiperComponent } from "../../components/swiper/swiper-component";
import { Release } from "../../model/release.model";
import { DateFormat, DateService } from "../util/date-service";

export class MostExpectedReleasesSwiperRenderer extends AbstractRenderService<Release[]> {
    private readonly dateService: DateService;
    private readonly releaseTemplateElement: HTMLTemplateElement;

    constructor(
        alertService: AlertService,
        loadingIndicatorService: LoadingIndicatorService,
        dateService: DateService,
    ) {
        super(alertService, loadingIndicatorService);
        this.dateService = dateService;
        this.releaseTemplateElement = document.getElementById("release-template") as HTMLTemplateElement;
    }

    protected getHostElementId(): string {
        return "most-expected-releases-container";
    }

    protected onRendering(releases: Release[]): void {
        if (releases && releases.length > 0) {
            const items = this.renderReleaseItems(releases);
            new SwiperComponent({
                uniqueCssClassSelector: "most-expected-releases-swiper",
                items,
                host: this.hostElement,
            });
        } else {
            const hint = document.createElement("p");
            hint.textContent = "There are currently no releases available";
            hint.classList.add("font-color-red");
            this.hostElement.insertAdjacentElement("beforeend", hint);
        }
    }

    private renderReleaseItems(releases: Release[]): HTMLDivElement[] {
        const items: HTMLDivElement[] = [];
        releases.forEach((release) => {
            const releaseDivElement = this.renderReleaseItem(release);
            items.push(releaseDivElement);
        });

        return items;
    }

    private renderReleaseItem(release: Release): HTMLDivElement {
        const releaseTemplateNode = document.importNode(this.releaseTemplateElement.content, true);
        const releaseDivElement = releaseTemplateNode.firstElementChild as HTMLDivElement;
        const releaseCoverElement = releaseDivElement.querySelector("#release-cover") as HTMLImageElement;
        const releaseArtistElement = releaseDivElement.querySelector("#release-artist") as HTMLSpanElement;
        const releaseTitleElement = releaseDivElement.querySelector("#release-title") as HTMLSpanElement;
        const releaseDateElement = releaseDivElement.querySelector("#release-date") as HTMLDivElement;

        releaseCoverElement.src = release.coverUrl;
        releaseArtistElement.textContent = release.artist;
        releaseTitleElement.textContent = release.albumTitle;
        releaseDateElement.innerHTML = `
            <div class="custom-tooltip">${this.dateService.formatRelativeInDays(release.releaseDate)}
                <span class="tooltip-text">${this.dateService.format(release.releaseDate, DateFormat.LONG)}</span>
            </div>
        `;

        return releaseDivElement;
    }
}
