import {LoadingIndicatorService} from "./loading-indicator-service";
import {AlertService} from "./alert-service";
import {HomepageResponse} from "../model/homepage-response.model";
import {AbstractRenderService} from "./abstract-render-service";
import {Artist} from "../model/artist.model";
import {Release} from "../model/release.model";
import {DateService} from "./date-service";

export class HomepageRenderService extends AbstractRenderService<HomepageResponse> {

    private readonly dateService: DateService;
    private readonly artistTemplateElement: HTMLTemplateElement;
    private readonly releaseTemplateElement: HTMLTemplateElement;
    private readonly MAX_CARDS_PER_ROW: number = 4;
    private readonly MIN_CARDS_PER_ROW: number = this.MAX_CARDS_PER_ROW - 1;

    constructor(alertService: AlertService, loadingIndicatorService: LoadingIndicatorService, dateService: DateService) {
        super(alertService, loadingIndicatorService);
        this.dateService = dateService;
        this.artistTemplateElement = document.getElementById("artist-card")! as HTMLTemplateElement;
        this.releaseTemplateElement = document.getElementById("release-card")! as HTMLTemplateElement;
    }

    protected getHostElementId(): string {
        return "home-container";
    }

    protected onRendering(response: HomepageResponse): void {
        if (response.upcomingReleases.length >= this.MIN_CARDS_PER_ROW &&
            response.recentReleases.length >= this.MIN_CARDS_PER_ROW) {
            this.renderUpcomingReleasesRow(response);
            this.renderRecentReleasesRow(response);
        }
        else {
            this.renderReleaseRow(response);
        }

        this.renderRecentlyFollowedArtistsRow(response);
        this.renderFavoriteCommunityArtistsRow(response);
    }

    private renderUpcomingReleasesRow(response: HomepageResponse) {
        this.insertHeadingElement("Upcoming releases")
        const upcomingReleasesRowElement = this.insertRowElement();
        this.renderReleaseCards(response.upcomingReleases, upcomingReleasesRowElement);
        this.insertPlaceholder(response.upcomingReleases.length, upcomingReleasesRowElement);
    }

    private renderRecentReleasesRow(response: HomepageResponse): void {
        this.insertHeadingElement("Recent releases")
        const recentReleasesRowElement = this.insertRowElement();
        this.renderReleaseCards(response.recentReleases, recentReleasesRowElement);
        this.insertPlaceholder(response.recentReleases.length, recentReleasesRowElement);
    }

    private renderReleaseRow(response: HomepageResponse): void {
        const releases = response.recentReleases.concat(response.upcomingReleases)
          .splice(0, this.MAX_CARDS_PER_ROW);

        if (releases.length) {
            this.insertHeadingElement("Releases");
            const releasesRow = this.insertRowElement();
            this.renderReleaseCards(releases, releasesRow);
            this.insertPlaceholder(releases.length, releasesRow);
        }
    }

    private renderRecentlyFollowedArtistsRow(response: HomepageResponse): void {
        if (response.recentlyFollowedArtists.length) {
            this.insertHeadingElement("Recently followed artists")
            const recentlyFollowedRowElement = this.insertRowElement();

            response.recentlyFollowedArtists.forEach(artist => {
                const artistDivElement = this.renderArtistCard(artist);
                const followedSinceElement = artistDivElement.querySelector("#artist-followed-since") as HTMLDivElement;
                followedSinceElement.innerHTML = this.createDateString(artist.followedSince);
                this.attachCard(artistDivElement, recentlyFollowedRowElement);
            });
        }
    }

    private renderFavoriteCommunityArtistsRow(response: HomepageResponse): void {
        if (response.favoriteCommunityArtists.length) {
            this.insertHeadingElement("The community's favorite artists")
            const recentlyFollowedRowElement = this.insertRowElement();

            response.favoriteCommunityArtists.forEach(artist => {
                const artistDivElement = this.renderArtistCard(artist);
                this.attachCard(artistDivElement, recentlyFollowedRowElement);
            });
        }
    }

    private renderArtistCard(artist: Artist): HTMLDivElement {
        const artistTemplateNode = document.importNode(this.artistTemplateElement.content, true);
        const artistDivElement = artistTemplateNode.firstElementChild as HTMLDivElement;
        const artistThumbElement = artistDivElement.querySelector("#artist-thumb") as HTMLImageElement;
        const artistNameElement = artistDivElement.querySelector("#artist-name") as HTMLParagraphElement;

        artistThumbElement.src = artist.thumb;
        artistNameElement.textContent = artist.artistName;

        return artistDivElement;
    }

    private renderReleaseCards(releases: Release[], rowElement: HTMLDivElement): void {
        releases.forEach(release => {
            const releaseDivElement = this.renderReleaseCard(release);
            const releaseDateElement = releaseDivElement.querySelector("#release-date") as HTMLDivElement;
            releaseDateElement.innerHTML = this.createDateString(release.releaseDate);
            this.attachCard(releaseDivElement, rowElement);
        });
    }

    private renderReleaseCard(release: Release): HTMLDivElement {
        const releaseTemplateNode = document.importNode(this.releaseTemplateElement.content, true);
        const releaseDivElement = releaseTemplateNode.firstElementChild as HTMLDivElement;
        const releaseCoverElement = releaseDivElement.querySelector("#release-cover") as HTMLImageElement;
        const artistNameElement = releaseDivElement.querySelector("#release-artist-name") as HTMLParagraphElement;
        const releaseTitleElement = releaseDivElement.querySelector("#release-title") as HTMLParagraphElement;

        releaseCoverElement.src = release.coverUrl;
        artistNameElement.textContent = release.artist;
        releaseTitleElement.textContent = release.albumTitle;

        return releaseDivElement;
    }

    private renderPlaceholderCard(): HTMLDivElement {
        const artistTemplateNode = document.importNode(this.releaseTemplateElement.content, true);
        const divElement = artistTemplateNode.firstElementChild as HTMLDivElement;
        const thumbElement = divElement.querySelector("#release-cover") as HTMLImageElement;
        const nameElement = divElement.querySelector("#release-artist-name") as HTMLParagraphElement;
        const subtitleElement = divElement.querySelector("#release-title") as HTMLParagraphElement;
        const footerElement = divElement.querySelector("#release-date") as HTMLDivElement;

        thumbElement.src = "/images/question-mark.jpg";
        nameElement.textContent = "Nothing here..."
        subtitleElement.textContent = "Want to see more?";
        footerElement.innerHTML = "Follow more artists!";

        return divElement;
    }

    private attachCard(divElement: HTMLDivElement, rowElement: HTMLDivElement): void {
        rowElement.insertAdjacentElement("beforeend", divElement);
    }

    private createDateString(date: Date): string {
        let timeRange = this.dateService.calculateTimeRange(date);

        if (timeRange >= 0) {
            return `In ${timeRange} days`;
        }
        else {
            timeRange = -timeRange;
            return `${timeRange} days ago`;
        }
    }

    private insertHeadingElement(heading: string): void {
        const headingElement = document.createElement("p") as HTMLParagraphElement;
        headingElement.className = "h5 mt-4 mb-2";
        headingElement.textContent = heading;
        this.hostElement.insertAdjacentElement("beforeend", headingElement);
    }

    private insertRowElement(): HTMLDivElement {
        const recentlyFollowedRowElement = document.createElement("div") as HTMLDivElement;
        recentlyFollowedRowElement.className = "row";
        this.hostElement.insertAdjacentElement("beforeend", recentlyFollowedRowElement);
        return recentlyFollowedRowElement;
    }

    private insertPlaceholder(elementCount: number, rowElement: HTMLDivElement): void {
        if (elementCount < this.MAX_CARDS_PER_ROW) {
            const placeholderDivElement = this.renderPlaceholderCard();
            this.attachCard(placeholderDivElement, rowElement);
        }
    }
}
