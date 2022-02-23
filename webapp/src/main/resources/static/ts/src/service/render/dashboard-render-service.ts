import { LoadingIndicatorService } from "../util/loading-indicator-service";
import { AlertService } from "../util/alert-service";
import { DashboardResponse } from "../../model/dashboard-response.model";
import { AbstractRenderService } from "./abstract-render-service";
import { Artist } from "../../model/artist.model";
import { Release } from "../../model/release.model";
import { DateFormat, DateService } from "../util/date-service";
import { FollowArtistService } from "../follow-artist-service";
import { SwiperComponent } from "../../components/swiper/swiper-component";

interface ReleaseCard {
    readonly divElement: HTMLDivElement;
    readonly coverElement: HTMLImageElement;
    readonly nameElement: HTMLParagraphElement;
    readonly subtitleElement: HTMLParagraphElement;
    readonly footerElement: HTMLDivElement;
}

export class DashboardRenderService extends AbstractRenderService<DashboardResponse> {
    private readonly dateService: DateService;
    private readonly followArtistService: FollowArtistService;
    private readonly artistTemplateElement: HTMLTemplateElement;
    private readonly releaseTemplateElement: HTMLTemplateElement;

    private readonly MIN_CARDS_PER_ROW: number = 2;

    constructor(
        alertService: AlertService,
        loadingIndicatorService: LoadingIndicatorService,
        dateService: DateService,
        followArtistService: FollowArtistService,
    ) {
        super(alertService, loadingIndicatorService);
        this.dateService = dateService;
        this.followArtistService = followArtistService;
        this.artistTemplateElement = document.getElementById("artist-template") as HTMLTemplateElement;
        this.releaseTemplateElement = document.getElementById("release-template") as HTMLTemplateElement;
    }

    protected getHostElementId(): string {
        return "dashboard-container";
    }

    protected onRendering(response: DashboardResponse): void {
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

    private renderUpcomingReleasesRow(response: DashboardResponse) {
        const title = "Your upcoming releases";
        this.renderReleaseCards("swiper-upcoming-release", title, response.upcomingReleases);
    }

    private renderRecentReleasesRow(response: DashboardResponse): void {
        const title = "Recent releases";
        this.renderReleaseCards("swiper-recent-releases", title, response.recentReleases);
    }

    private renderReleaseRow(response: DashboardResponse): void {
        const recentReleases = response.recentReleases.sort((r1, r2) =>
            this.dateService.compare(r1.releaseDate, r2.releaseDate),
        );
        const releases = recentReleases.concat(response.upcomingReleases);

        if (releases.length) {
            const title = "Your recent releases";
            this.renderReleaseCards("swiper-releases", title, releases);
        }
    }

    private renderRecentlyFollowedArtistsRow(response: DashboardResponse): void {
        if (response.recentlyFollowedArtists.length) {
            const title = "Your recently followed artists";
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

    private renderFavoriteCommunityArtistsRow(response: DashboardResponse): void {
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

    private renderMostExpectedReleasesRow(response: DashboardResponse) {
        const title = "The community's most expected releases";
        this.renderReleaseCards("swiper-community-most-expected-releases", title, response.mostExpectedReleases);
    }

    private renderArtistCard(artist: Artist): HTMLDivElement {
        const artistTemplateNode = document.importNode(this.artistTemplateElement.content, true);
        const artistDivElement = artistTemplateNode.firstElementChild as HTMLDivElement;
        const artistThumbElement = artistDivElement.querySelector("#artist-image") as HTMLImageElement;
        const artistNameElement = artistDivElement.querySelector("#artist-name") as HTMLParagraphElement;

        // ToDo DanielW: follow icon?
        // const followIconElement = artistDivElement.querySelector("#follow-icon") as HTMLDivElement;
        // const followIcon = followIconElement.getElementsByTagName("img").item(0) as HTMLImageElement;
        // followIconElement.addEventListener("click", this.handleFollowIconClick.bind(this, followIcon, artist));

        artistThumbElement.src = artist.mediumImage;
        artistNameElement.textContent = artist.artistName;

        return artistDivElement;
    }

    private renderReleaseCards(uniqueId: string, title: string, releases: Release[]): void {
        const cards: HTMLDivElement[] = [];
        releases.forEach((release) => {
            const card = this.renderReleaseCard(release);
            cards.push(card);
        });
        this.createSwiper(uniqueId, title, cards);
    }

    private renderReleaseCard(release: Release): HTMLDivElement {
        const homepageCard = this.getReleaseCard();
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

    private getReleaseCard(): ReleaseCard {
        const templateNode = document.importNode(this.releaseTemplateElement.content, true);
        const divElement = templateNode.firstElementChild as HTMLDivElement;
        return {
            divElement: divElement,
            coverElement: divElement.querySelector("#release-cover") as HTMLImageElement,
            nameElement: divElement.querySelector("#release-artist") as HTMLParagraphElement,
            subtitleElement: divElement.querySelector("#release-title") as HTMLParagraphElement,
            footerElement: divElement.querySelector("#release-date") as HTMLDivElement,
        };
    }

    private createSwiper(uniqueCssClassSelector: string, title: string, items: HTMLDivElement[]): void {
        if (items && items.length >= this.MIN_CARDS_PER_ROW) {
            const swiperSection = this.createSwiperSection(title);
            const swiperWrapper = document.createElement("div") as HTMLDivElement;
            swiperWrapper.classList.add("detector__swiper");
            swiperSection.insertAdjacentElement("beforeend", swiperWrapper);
            this.hostElement.insertAdjacentElement("beforeend", swiperSection);

            if (items.length > 0) {
                new SwiperComponent({
                    uniqueCssClassSelector,
                    items,
                    host: swiperWrapper,
                });
            }
        }
    }

    private createSwiperSection(title: string): HTMLDivElement {
        const section = document.createElement("div") as HTMLDivElement;
        section.classList.add("section");

        const sectionTitle = document.createElement("h2") as HTMLHeadingElement;
        sectionTitle.textContent = title;

        const separator = document.createElement("div") as HTMLDivElement;
        separator.classList.add("headline-separator");

        section.insertAdjacentElement("beforeend", sectionTitle);
        section.insertAdjacentElement("beforeend", separator);

        return section;
    }

    private handleFollowIconClick(followIconElement: HTMLImageElement, artist: Artist): void {
        this.followArtistService.handleFollowIconClick(followIconElement, {
            externalId: artist.externalId,
            artistName: artist.artistName,
            source: artist.source,
        });
    }
}
