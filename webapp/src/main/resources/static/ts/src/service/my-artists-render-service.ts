import { MyArtistsResponse } from "../model/my-artists-response.model";
import { FollowArtistService } from "./follow-artist-service";
import { FollowState } from "../model/follow-state.model";
import { Artist } from "../model/artist.model";
import { AlertService } from "./alert-service";
import { Pagination } from "../model/pagination.model";
import { LoadingIndicatorService } from "./loading-indicator-service";
import { PaginationComponent } from "../components/pagination/pagination-component";

export class MyArtistsRenderService {

    private readonly MAX_CARDS_PER_ROW = 4;

    private readonly followArtistService: FollowArtistService;
    private readonly alertService: AlertService;
    private readonly loadingIndicatorService: LoadingIndicatorService;
    private readonly paginationComponent: PaginationComponent;
    private readonly artistTemplateElement: HTMLTemplateElement;
    private readonly hostElement: HTMLDivElement;
    private rowElement?: HTMLDivElement;

    constructor(followArtistService: FollowArtistService, alertService: AlertService, loadingIndicatorService: LoadingIndicatorService) {
        this.followArtistService = followArtistService;
        this.alertService = alertService;
        this.loadingIndicatorService = loadingIndicatorService;
        this.paginationComponent = new PaginationComponent();
        this.artistTemplateElement = document.getElementById("artist-card")! as HTMLTemplateElement;
        this.hostElement = document.getElementById("artists-container")! as HTMLDivElement;
    }

    public renderResults(data: Promise<MyArtistsResponse>): void {
        this.loadingIndicatorService.showLoadingIndicator("artists-container");
        data.then((response) => {
            if (response.myArtists.length === 0 && response.pagination.currentPage === 1) {
                const message = "Currently you do not follow any artist. Start a search for your favorite artists right now.";
                const infoMessage = this.alertService.renderInfoAlert(message, false);
                this.hostElement.insertAdjacentElement("afterbegin", infoMessage);
            }
            else if (response.pagination.totalPages < response.pagination.currentPage) {
                // If you follow for example 21 bands and you unfollow one band on page 1 and then go to page 2 there are no bands to display.
                // In this case you will be forwarded to the last page.
                window.location.replace(`?page=${response.pagination.totalPages}`);
            }
            else {
                let currentCarNo = 1;
                response.myArtists.forEach((artist => {
                    const artistDivElement = this.renderArtistCard(artist);
                    this.attachArtistCard(artistDivElement, currentCarNo);
                    currentCarNo++;
                }));

                const pagination = response.pagination;
                if (pagination.totalPages > 1) {
                    this.attachPagination(pagination);
                }
            }
        }).catch(() => {
            const message = "Damn it! A satanic error has occurred. Please try again later.";
            const infoMessage = this.alertService.renderErorAlert(message, false);
            this.hostElement.insertAdjacentElement("afterbegin", infoMessage);
        }).finally(() => {
            this.loadingIndicatorService.hideLoadingIndicator("artists-container");
        });
    }

    private renderArtistCard(artist: Artist): HTMLDivElement {
        const artistTemplateNode = document.importNode(this.artistTemplateElement.content, true);
        const artistDivElement = artistTemplateNode.firstElementChild as HTMLDivElement;
        const artistThumbElement = artistDivElement.querySelector("#thumb") as HTMLImageElement;
        const followIconElement = artistDivElement.querySelector("#follow-icon") as HTMLDivElement;
        const artistNameElement = artistDivElement.querySelector("#artist-name") as HTMLParagraphElement;
        const followedSinceElement = artistDivElement.querySelector("#followed-since") as HTMLDivElement;

        artistThumbElement.src = artist.thumb;
        artistNameElement.textContent = artist.artistName;
        followedSinceElement.innerHTML = this.createFollowedSinceString(artist.followedSince);
        followIconElement.addEventListener(
            "click",
            this.handleFollowIconClick.bind(this, followIconElement, artist)
        );

        return artistDivElement;
    }

    private attachArtistCard(artistDivElement: HTMLDivElement, currentCarNo: number) {
        if (this.rowElement === undefined || currentCarNo % this.MAX_CARDS_PER_ROW === 1) {
            this.rowElement = document.createElement("div");
            this.rowElement.className = "row";
        }
        this.rowElement.insertAdjacentElement("beforeend", artistDivElement);
        this.hostElement.insertAdjacentElement("beforeend", this.rowElement);
    }

    private createFollowedSinceString(followedSince: Date): string {
        const followedSinceString = new Date(2020, 1, 1).toDateString();
        return `<i class="material-icons">favorite</i> on ${followedSinceString}`;
    }

    private handleFollowIconClick(followIconElement: HTMLDivElement, artist: Artist) {
        const followIcon = followIconElement.getElementsByTagName("i").item(0)!;
        const currentFollowState = followIcon.textContent;

        if (currentFollowState === FollowState.FOLLOWING.toString()) {
            this.followArtistService.unfollowArtist(artist);
            followIcon.textContent = FollowState.NOT_FOLLOWING.toString();
        }
        else {
            this.followArtistService.followArtist(artist);
            followIcon.textContent = FollowState.FOLLOWING.toString();
        }
    }

    private attachPagination(paginationData: Pagination) {
        const paginationList = this.paginationComponent.render(paginationData);
        this.hostElement.insertAdjacentElement("beforeend", paginationList);
    }
}
