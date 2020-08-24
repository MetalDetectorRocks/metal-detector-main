import {LoadingIndicatorService} from "./loading-indicator-service";
import {AlertService} from "./alert-service";
import {HomepageResponse} from "../model/homepage-response.model";
import {AbstractRenderService} from "./abstract-render-service";
import {Artist} from "../model/artist.model";
import {Release} from "../model/release.model";

export class HomepageRenderService extends AbstractRenderService<HomepageResponse> {

    private readonly artistTemplateElement: HTMLTemplateElement;
    private readonly releaseTemplateElement: HTMLTemplateElement;

    constructor(alertService: AlertService, loadingIndicatorService: LoadingIndicatorService) {
        super(alertService, loadingIndicatorService);
        this.artistTemplateElement = document.getElementById("artist-card")! as HTMLTemplateElement;
        this.releaseTemplateElement = document.getElementById("release-card")! as HTMLTemplateElement;
    }

    protected getHostElementId(): string {
        return "home-container";
    }

    protected onRendering(response: HomepageResponse): void {
        this.renderRecentReleasesRow(response);
        this.renderRecentlyFollowedRow(response);
    }

    private renderRecentReleasesRow(response: HomepageResponse): void {
        const headingElement = document.createElement("p") as HTMLParagraphElement;
        headingElement.className = "h5 mt-4 mb-2";
        headingElement.textContent = "Recent releases";
        this.hostElement.insertAdjacentElement("beforeend", headingElement);

        const recentReleasesRowElement = document.createElement("div")! as HTMLDivElement;
        recentReleasesRowElement.className = "row";
        this.hostElement.insertAdjacentElement("beforeend", recentReleasesRowElement);

        response.recentReleases.forEach(release => {
            const releaseDivElement = this.renderReleaseCard(release);
            this.attachCard(releaseDivElement, recentReleasesRowElement);
        });
    }

    private renderRecentlyFollowedRow(response: HomepageResponse): void {
        const headingElement = document.createElement("p") as HTMLParagraphElement;
        headingElement.className = "h5 mt-4 mb-2";
        headingElement.textContent = "Recently followed artists";
        this.hostElement.insertAdjacentElement("beforeend", headingElement);

        const recentlyFollowedRowElement = document.createElement("div") as HTMLDivElement;
        recentlyFollowedRowElement.className = "row";
        this.hostElement.insertAdjacentElement("beforeend", recentlyFollowedRowElement);

        response.recentlyFollowedArtists.forEach(artist => {
            const artistDivElement = this.renderArtistCard(artist);
            this.attachCard(artistDivElement, recentlyFollowedRowElement);
        });
    }

    private renderArtistCard(artist: Artist): HTMLDivElement {
        const artistTemplateNode = document.importNode(this.artistTemplateElement.content, true);
        const artistDivElement = artistTemplateNode.firstElementChild as HTMLDivElement;
        const artistThumbElement = artistDivElement.querySelector("#artist-thumb") as HTMLImageElement;
        const artistNameElement = artistDivElement.querySelector("#artist-name") as HTMLParagraphElement;
        const followedSinceElement = artistDivElement.querySelector("#artist-followed-since") as HTMLDivElement;

        artistThumbElement.src = artist.thumb;
        artistNameElement.textContent = artist.artistName;
        followedSinceElement.innerHTML = this.createFollowedSinceString(artist.followedSince);

        return artistDivElement;
    }

    private renderReleaseCard(release: Release): HTMLDivElement {
        const releaseTemplateNode = document.importNode(this.releaseTemplateElement.content, true);
        const releaseDivElement = releaseTemplateNode.firstElementChild as HTMLDivElement;
        const releaseCoverElement = releaseDivElement.querySelector("#release-cover") as HTMLImageElement;
        const artistNameElement = releaseDivElement.querySelector("#release-artist-name") as HTMLParagraphElement;
        const releaseTitleElement = releaseDivElement.querySelector("#release-title") as HTMLParagraphElement;
        const releaseDateElement = releaseDivElement.querySelector("#release-date") as HTMLDivElement;

        releaseCoverElement.src = release.coverUrl;
        artistNameElement.textContent = release.artist;
        releaseTitleElement.textContent = release.albumTitle;
        releaseDateElement.innerHTML = this.createReleasedInString(release.releaseDate);

        return releaseDivElement;
    }

    private attachCard(divElement: HTMLDivElement, rowElement: HTMLDivElement) {
        rowElement.insertAdjacentElement("beforeend", divElement);
    }

    private createReleasedInString(releaseDate: Date): string {
        const now = Date.now();
        const then = new Date(releaseDate).valueOf();
        const releasedInDays = Math.round((now - then) / 1000 / 60 / 60 / 24);
        return `In ${releasedInDays} days`;
    }

    private createFollowedSinceString(followedSince: Date): string {
        const now = Date.now();
        const then = new Date(followedSince).valueOf();
        const followedForDays = Math.round((now - then) / 1000 / 60 / 60 / 24);
        return `${followedForDays} days ago`;
    }
}
