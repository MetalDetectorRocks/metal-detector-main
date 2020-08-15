import {LoadingIndicatorService} from "./loading-indicator-service";
import {AlertService} from "./alert-service";
import {HomepageResponse} from "../model/homepage-response.model";
import {AbstractRenderService} from "./abstract-render-service";
import {Artist} from "../model/artist.model";

export class HomepageRenderService extends AbstractRenderService<HomepageResponse> {

    private readonly artistTemplateElement: HTMLTemplateElement;

    constructor(alertService: AlertService, loadingIndicatorService: LoadingIndicatorService) {
        super(alertService, loadingIndicatorService);
        this.artistTemplateElement = document.getElementById("artist-card")! as HTMLTemplateElement;
    }

    protected getHostElementId(): string {
        return "home-container";
    }

    protected onRendering(response: HomepageResponse): void {
        const recentlyFollowedRowElement = document.getElementById("recently-followed-row")! as HTMLDivElement;

        response.recentlyFollowedArtists.forEach(artist => {
            const artistDivElement = this.renderArtistCard(artist);
            this.attachCard(artistDivElement, recentlyFollowedRowElement);
        });
    }

    private renderArtistCard(artist: Artist): HTMLDivElement {
        const artistTemplateNode = document.importNode(this.artistTemplateElement.content, true);
        const artistDivElement = artistTemplateNode.firstElementChild as HTMLDivElement;
        const artistThumbElement = artistDivElement.querySelector("#thumb") as HTMLImageElement;
        const artistNameElement = artistDivElement.querySelector("#artist-name") as HTMLParagraphElement;
        const followedSinceElement = artistDivElement.querySelector("#followed-since") as HTMLDivElement;

        artistThumbElement.src = artist.thumb;
        artistNameElement.textContent = artist.artistName;
        followedSinceElement.innerHTML = this.createFollowedSinceString(artist.followedSince);

        return artistDivElement;
    }

    private attachCard(divElement: HTMLDivElement, rowElement: HTMLDivElement) {
        rowElement.insertAdjacentElement("beforeend", divElement);
    }

    private createFollowedSinceString(followedSince: Date): string {
        const now = Date.now();
        const then = new Date(followedSince).valueOf();
        const followedForDays = Math.round((now - then) / 1000 / 60 / 60 / 24);
        return `${followedForDays} days ago`;
    }
}
