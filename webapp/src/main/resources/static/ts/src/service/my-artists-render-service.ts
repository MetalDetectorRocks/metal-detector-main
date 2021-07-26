import { MyArtistsResponse } from "../model/my-artists-response.model";
import { FollowArtistService } from "./follow-artist-service";
import { Artist } from "../model/artist.model";
import { AlertService } from "./alert-service";
import { Pagination } from "../model/pagination.model";
import { LoadingIndicatorService } from "./loading-indicator-service";
import { PaginationComponent } from "../components/pagination/pagination-component";
import { AbstractRenderService } from "./abstract-render-service";
import { DateFormat, DateService } from "./date-service";

export class MyArtistsRenderService extends AbstractRenderService<MyArtistsResponse> {
    private readonly followArtistService: FollowArtistService;
    private readonly dateService: DateService;
    private readonly paginationComponent: PaginationComponent;
    private readonly artistTemplateElement: HTMLTemplateElement;
    private readonly paginationWrapper: HTMLDivElement;

    constructor(
        followArtistService: FollowArtistService,
        dateService: DateService,
        alertService: AlertService,
        loadingIndicatorService: LoadingIndicatorService,
    ) {
        super(alertService, loadingIndicatorService);
        this.followArtistService = followArtistService;
        this.dateService = dateService;
        this.paginationComponent = new PaginationComponent();
        this.artistTemplateElement = document.getElementById("artist-card") as HTMLTemplateElement;
        this.paginationWrapper = document.getElementById("pagination-wrapper") as HTMLDivElement;
    }

    protected getHostElementId(): string {
        return "my-artists-wrapper";
    }

    protected onRendering(response: MyArtistsResponse): void {
        if (response.myArtists.length === 0 && response.pagination.currentPage === 1) {
            const message =
                '<h3 class="h5">Currently you do not follow any artists.</h3>Start a search for your favorite artists ' +
                'or <a href="/settings/spotify-synchronization">synchronize</a> your artists from your Spotify account.';
            const infoMessage = this.alertService.renderInfoAlert(message, false);
            this.hostElement.insertAdjacentElement("afterbegin", infoMessage);
        } else if (response.pagination.totalPages < response.pagination.currentPage) {
            // If you follow for example 21 bands and you unfollow one band on page 1 and then go to page 2 there are no bands to display.
            // In this case you will be forwarded to the last page.
            window.location.replace(`?page=${response.pagination.totalPages}`);
        } else {
            response.myArtists.forEach((artist) => {
                const artistDivElement = this.renderArtistCard(artist);
                this.hostElement.insertAdjacentElement("beforeend", artistDivElement);
            });

            const pagination = response.pagination;
            if (pagination.totalPages > 1) {
                this.attachPagination(pagination);
            }
        }
    }

    private renderArtistCard(artist: Artist): HTMLDivElement {
        const artistTemplateNode = document.importNode(this.artistTemplateElement.content, true);
        const artistDivElement = artistTemplateNode.firstElementChild as HTMLDivElement;
        const artistThumbElement = artistDivElement.querySelector("#thumb") as HTMLImageElement;
        const followIconElement = artistDivElement.querySelector("#follow-icon") as HTMLDivElement;
        const followIcon = followIconElement.getElementsByTagName("img").item(0) as HTMLImageElement;
        const artistNameElement = artistDivElement.querySelector("#artist-name") as HTMLParagraphElement;
        const followedSinceElement = artistDivElement.querySelector("#followed-since") as HTMLDivElement;

        artistThumbElement.src = artist.mediumImage;
        artistNameElement.textContent = artist.artistName;
        followedSinceElement.innerHTML = this.createFollowedSinceString(artist.followedSince);
        followIconElement.addEventListener("click", this.handleFollowIconClick.bind(this, followIcon, artist));

        return artistDivElement;
    }

    private createFollowedSinceString(followedSince: string): string {
        const followedSinceString = this.dateService.format(followedSince, DateFormat.LONG);
        return `<img class="followed-since-icon" src="/images/pommesgabel.svg" alt="followed" width=26> on ${followedSinceString}`;
    }

    private handleFollowIconClick(followIconElement: HTMLImageElement, artist: Artist): void {
        this.followArtistService.handleFollowIconClick(followIconElement, {
            externalId: artist.externalId,
            artistName: artist.artistName,
            source: artist.source,
        });
    }

    private attachPagination(paginationData: Pagination): void {
        const paginationList = this.paginationComponent.render(paginationData);
        this.paginationWrapper.insertAdjacentElement("beforeend", paginationList);
    }
}
