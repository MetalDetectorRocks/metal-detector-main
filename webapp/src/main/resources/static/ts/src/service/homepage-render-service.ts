import {LoadingIndicatorService} from "./loading-indicator-service";
import {AlertService} from "./alert-service";
import {HomepageResponse} from "../model/homepage-response.model";
import {AbstractRenderService} from "./abstract-render-service";
import {Artist} from "../model/artist.model";
import {Release} from "../model/release.model";
import {DateFormat, DateFormatService} from "./date-format-service";

interface HomepageCard {
    readonly divElement: HTMLDivElement;
    readonly coverElement: HTMLImageElement;
    readonly nameElement: HTMLParagraphElement;
    readonly subtitleElement: HTMLParagraphElement;
    readonly footerElement: HTMLDivElement;
}

export class HomepageRenderService extends AbstractRenderService<HomepageResponse> {

    private readonly dateFormatService: DateFormatService;
    private readonly artistTemplateElement: HTMLTemplateElement;
    private readonly releaseTemplateElement: HTMLTemplateElement;
    private readonly MAX_CARDS_PER_ROW: number = 4;
    private readonly MIN_CARDS_PER_ROW: number = this.MAX_CARDS_PER_ROW - 1;

    constructor(alertService: AlertService, loadingIndicatorService: LoadingIndicatorService, dateService: DateFormatService) {
        super(alertService, loadingIndicatorService);
        this.dateFormatService = dateService;
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
                console.log(artist.followedSince);
                const followedSinceElement = artistDivElement.querySelector("#artist-sub-title") as HTMLDivElement;
                followedSinceElement.innerHTML = `
                    <div class="custom-tooltip">${this.dateFormatService.formatRelative(artist.followedSince)}
                        <span class="tooltip-text">${this.dateFormatService.format(artist.followedSince, DateFormat.LONG)}</span>
                    </div>
                `;
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
                const followerElement = artistDivElement.querySelector("#artist-sub-title") as HTMLDivElement;
                followerElement.innerHTML = artist.follower + " follower";
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
            this.attachCard(releaseDivElement, rowElement);
        });
    }

    private renderReleaseCard(release: Release): HTMLDivElement {
        const homepageCard = this.getHomepageCard();
        homepageCard.coverElement.src = release.coverUrl;
        homepageCard.nameElement.textContent = release.artist;
        homepageCard.subtitleElement.textContent = release.albumTitle;
        homepageCard.footerElement.innerHTML = `
            <div class="custom-tooltip">${this.dateFormatService.formatRelative(release.releaseDate)}
                <span class="tooltip-text">${this.dateFormatService.format(release.releaseDate, DateFormat.LONG)}</span>
            </div>
        `;

        return homepageCard.divElement;
    }

    private renderPlaceholderCard(): HTMLDivElement {
        const homepageCard = this.getHomepageCard();
        homepageCard.coverElement.src = "/images/question-mark.jpg";
        homepageCard.nameElement.textContent = "Nothing here..."
        homepageCard.subtitleElement.textContent = "Want to see more?";
        homepageCard.footerElement.innerHTML = "Follow more artists!";

        return homepageCard.divElement;
    }

    private getHomepageCard(): HomepageCard {
        const templateNode = document.importNode(this.releaseTemplateElement.content, true);
        const divElement = templateNode.firstElementChild as HTMLDivElement;
        return {
            divElement: divElement,
            coverElement: divElement.querySelector("#release-cover") as HTMLImageElement,
            nameElement: divElement.querySelector("#release-artist-name") as HTMLParagraphElement,
            subtitleElement: divElement.querySelector("#release-title") as HTMLParagraphElement,
            footerElement: divElement.querySelector("#release-date") as HTMLDivElement
        };
    }

    private attachCard(divElement: HTMLDivElement, rowElement: HTMLDivElement): void {
        rowElement.insertAdjacentElement("beforeend", divElement);
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
