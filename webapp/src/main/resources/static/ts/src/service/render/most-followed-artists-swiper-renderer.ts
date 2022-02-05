import { AbstractRenderService } from "./abstract-render-service";
import { AlertService } from "../util/alert-service";
import { LoadingIndicatorService } from "../util/loading-indicator-service";
import { Artist } from "../../model/artist.model";
import { SwiperComponent } from "../../components/swiper/swiper-component";

export class MostFollowedArtistsSwiperRenderer extends AbstractRenderService<Artist[]> {
    private readonly artistTemplateElement: HTMLTemplateElement;

    constructor(alertService: AlertService, loadingIndicatorService: LoadingIndicatorService) {
        super(alertService, loadingIndicatorService);
        this.artistTemplateElement = document.getElementById("artist-template") as HTMLTemplateElement;
    }

    protected getHostElementId(): string {
        return "most-followed-artists-container";
    }

    protected onRendering(artists: Artist[]): void {
        if (artists && artists.length > 0) {
            const items = this.renderArtistItems(artists);
            new SwiperComponent({
                uniqueCssClassSelector: "most-followed-artists-swiper",
                items,
                host: this.hostElement,
            });
        } else {
            const hint = document.createElement("p");
            hint.textContent = "There are currently no artists available";
            hint.classList.add("font-color-red");
            this.hostElement.insertAdjacentElement("beforeend", hint);
        }
    }

    private renderArtistItems(artists: Artist[]): HTMLDivElement[] {
        const items: HTMLDivElement[] = [];
        artists.forEach((artist) => {
            const artistDivElement = this.renderArtistItem(artist);
            items.push(artistDivElement);
        });

        return items;
    }

    private renderArtistItem(artist: Artist): HTMLDivElement {
        const artistTemplateNode = document.importNode(this.artistTemplateElement.content, true);
        const artistDivElement = artistTemplateNode.firstElementChild as HTMLDivElement;
        const artistThumbElement = artistDivElement.querySelector("#artist-image") as HTMLImageElement;
        const artistNameElement = artistDivElement.querySelector("#artist-name") as HTMLParagraphElement;
        const followerElement = artistDivElement.querySelector("#follower") as HTMLDivElement;

        artistThumbElement.src = artist.mediumImage;
        artistNameElement.textContent = artist.artistName;
        followerElement.innerText = `${artist.follower} Follower`;

        return artistDivElement;
    }
}
