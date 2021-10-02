import { LoadingIndicatorService } from "./loading-indicator-service";
import { AlertService } from "./alert-service";
import { HomepageResponse } from "../model/homepage-response.model";
import { AbstractRenderService } from "./abstract-render-service";
import { Artist } from "../model/artist.model";
import { Release } from "../model/release.model";
import { DateFormat, DateService } from "./date-service";
import { FollowArtistService } from "./follow-artist-service";
import { SwiperComponent } from "../components/swiper/swiper-component";

interface HomepageCard {
    readonly divElement: HTMLDivElement;
    readonly coverElement: HTMLImageElement;
    readonly nameElement: HTMLParagraphElement;
    readonly subtitleElement: HTMLParagraphElement;
    readonly footerElement: HTMLDivElement;
}

export class HomepageRenderService extends AbstractRenderService<HomepageResponse> {
    private readonly dateService: DateService;
    private readonly followArtistService: FollowArtistService;
    private readonly artistTemplateElement: HTMLTemplateElement;
    private readonly releaseTemplateElement: HTMLTemplateElement;

    private readonly MIN_CARDS_PER_ROW: number = 3;

    constructor(
        alertService: AlertService,
        loadingIndicatorService: LoadingIndicatorService,
        dateService: DateService,
        followArtistService: FollowArtistService,
    ) {
        super(alertService, loadingIndicatorService);
        this.dateService = dateService;
        this.followArtistService = followArtistService;
        this.artistTemplateElement = document.getElementById("artist-card") as HTMLTemplateElement;
        this.releaseTemplateElement = document.getElementById("release-card") as HTMLTemplateElement;
    }

    protected getHostElementId(): string {
        return "home-container";
    }

    protected onRendering(response: HomepageResponse): void {
        if (
            response.upcomingReleases.length >= this.MIN_CARDS_PER_ROW &&
            response.recentReleases.length >= this.MIN_CARDS_PER_ROW
        ) {
            this.renderUpcomingReleasesRow(response);
            this.renderRecentReleasesRow(response);
        } else {
            this.renderReleaseRow(response);
        }

        this.renderRecentlyFollowedArtistsRow(response);
        this.renderFavoriteCommunityArtistsRow(response);
        this.renderMostExpectedReleasesRow(response);
    }

    private renderUpcomingReleasesRow(response: HomepageResponse) {
        const title = "Upcoming releases";
        this.renderReleaseCards("swiper-upcoming-release", title, response.upcomingReleases);
    }

    private renderRecentReleasesRow(response: HomepageResponse): void {
        const title = "Recent releases";
        this.renderReleaseCards("swiper-recent-releases", title, response.recentReleases);
    }

    private renderReleaseRow(response: HomepageResponse): void {
        const recentReleases = response.recentReleases.sort((r1, r2) =>
            this.dateService.compare(r1.releaseDate, r2.releaseDate),
        );
        const releases = recentReleases.concat(response.upcomingReleases);

        if (releases.length) {
            const title = "Releases";
            this.renderReleaseCards("swiper-releases", title, releases);
        }
    }

    private renderRecentlyFollowedArtistsRow(response: HomepageResponse): void {
        if (response.recentlyFollowedArtists.length) {
            const title = "Recently followed artists";
            const cards: HTMLDivElement[] = [];

            response.recentlyFollowedArtists.forEach((artist) => {
                const artistDivElement = this.renderArtistCard(artist);
                const followedSinceElement = artistDivElement.querySelector("#artist-sub-title") as HTMLDivElement;
                followedSinceElement.innerHTML = `
                    <div class="custom-tooltip">${this.dateService.formatRelative(artist.followedSince)}
                        <span class="tooltip-text">${this.dateService.format(
                            artist.followedSince,
                            DateFormat.LONG,
                        )}</span>
                    </div>
                `;
                cards.push(artistDivElement);
            });
            this.createSwiper("swiper-recently-followed-artists", title, cards);
        }
    }

    private renderFavoriteCommunityArtistsRow(response: HomepageResponse): void {
        if (response.favoriteCommunityArtists.length) {
            const title = "The community's favorite artists";
            const cards: HTMLDivElement[] = [];

            response.favoriteCommunityArtists.forEach((artist) => {
                const artistDivElement = this.renderArtistCard(artist);
                const followerElement = artistDivElement.querySelector("#artist-sub-title") as HTMLDivElement;
                followerElement.innerHTML = artist.follower + " follower";
                cards.push(artistDivElement);
            });
            this.createSwiper("swiper-community-favorite-artists", title, cards);
        }
    }

    private renderMostExpectedReleasesRow(response: HomepageResponse) {
        const title = "The community's most expected releases";
        this.renderReleaseCards("swiper-community-most-expected-releases", title, response.mostExpectedReleases, false);
    }

    private renderArtistCard(artist: Artist): HTMLDivElement {
        const artistTemplateNode = document.importNode(this.artistTemplateElement.content, true);
        const artistDivElement = artistTemplateNode.firstElementChild as HTMLDivElement;
        const artistThumbElement = artistDivElement.querySelector("#artist-thumb") as HTMLImageElement;
        const artistNameElement = artistDivElement.querySelector("#artist-name") as HTMLParagraphElement;
        const followIconElement = artistDivElement.querySelector("#follow-icon") as HTMLDivElement;
        const followIcon = followIconElement.getElementsByTagName("img").item(0) as HTMLImageElement;
        followIconElement.addEventListener("click", this.handleFollowIconClick.bind(this, followIcon, artist));

        artistThumbElement.src = artist.mediumImage;
        artistNameElement.textContent = artist.artistName;

        return artistDivElement;
    }

    private renderReleaseCards(uniqueId: string, title: string, releases: Release[], addPlaceholder = true): void {
        const cards: HTMLDivElement[] = [];
        releases.forEach((release) => {
            const card = this.renderReleaseCard(release);
            cards.push(card);
        });
        if (cards.length <= this.MIN_CARDS_PER_ROW && addPlaceholder) {
            cards.push(this.renderPlaceholderCard());
        }
        this.createSwiper(uniqueId, title, cards);
    }

    private renderReleaseCard(release: Release): HTMLDivElement {
        const homepageCard = this.getHomepageCard();
        homepageCard.coverElement.src = release.coverUrl;
        homepageCard.nameElement.textContent = release.artist;
        homepageCard.subtitleElement.textContent = release.albumTitle;
        homepageCard.footerElement.innerHTML = `
            <div class="custom-tooltip">${this.dateService.formatRelativeInDays(release.releaseDate)}
                <span class="tooltip-text">${this.dateService.format(release.releaseDate, DateFormat.LONG)}</span>
            </div>
        `;

        return homepageCard.divElement;
    }

    private renderPlaceholderCard(): HTMLDivElement {
        const homepageCard = this.getHomepageCard();
        homepageCard.coverElement.src = "/images/question-mark.jpg";
        homepageCard.nameElement.textContent = "Nothing here...";
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
            footerElement: divElement.querySelector("#release-date") as HTMLDivElement,
        };
    }

    private createSwiper(uniqueCssClassSelector: string, title: string, items: HTMLDivElement[]): void {
        if (items.length > 0) {
            new SwiperComponent({
                uniqueCssClassSelector,
                title,
                items,
                host: this.hostElement,
            });
        }
    }

    // private insertPlaceholder(elementCount: number, rowElement: HTMLDivElement): void {
    //     if (elementCount < this.MAX_CARDS_PER_ROW) {
    //         const placeholderDivElement = this.renderPlaceholderCard();
    //         this.attachCard(placeholderDivElement, rowElement);
    //     }
    // }

    private handleFollowIconClick(followIconElement: HTMLImageElement, artist: Artist): void {
        this.followArtistService.handleFollowIconClick(followIconElement, {
            externalId: artist.externalId,
            artistName: artist.artistName,
            source: artist.source,
        });
    }
}
