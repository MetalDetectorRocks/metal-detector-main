import {MyArtistsResponse} from "../model/my-artists-response.model";
import {FollowArtistService} from "./follow-artist-service";
import {Artist} from "../model/artist.model";
import {AlertService} from "./alert-service";
import {Pagination} from "../model/pagination.model";
import {LoadingIndicatorService} from "./loading-indicator-service";
import {PaginationComponent} from "../components/pagination/pagination-component";
import {AbstractRenderService} from "./abstract-render-service";
import {DateFormat, DateFormatService} from "./date-format-service";

export class MyArtistsRenderService extends AbstractRenderService<MyArtistsResponse> {

    private readonly MAX_CARDS_PER_ROW = 4;

    private readonly followArtistService: FollowArtistService;
    private readonly dateFormatService: DateFormatService;
    private readonly paginationComponent: PaginationComponent;
    private readonly artistTemplateElement: HTMLTemplateElement;
    private rowElement?: HTMLDivElement;

    constructor(followArtistService: FollowArtistService, dateFormatService: DateFormatService, alertService: AlertService, loadingIndicatorService: LoadingIndicatorService) {
        super(alertService, loadingIndicatorService);
        this.followArtistService = followArtistService;
        this.dateFormatService = dateFormatService;
        this.paginationComponent = new PaginationComponent();
        this.artistTemplateElement = document.getElementById("artist-card")! as HTMLTemplateElement;
    }

    protected getHostElementId(): string {
        return "artists-container";
    }

    protected onRendering(response: MyArtistsResponse): void {
        if (response.myArtists.length === 0 && response.pagination.currentPage === 1) {
            const message = '<h3 class="h5">Currently you do not follow any artist.</h3>Start a search for your favorite artists right now.';
            const infoMessage = this.alertService.renderInfoAlert(message, false);
            this.hostElement.insertAdjacentElement("afterbegin", infoMessage);
        }
        else if (response.pagination.totalPages < response.pagination.currentPage) {
            // If you follow for example 21 bands and you unfollow one band on page 1 and then go to page 2 there are no bands to display.
            // In this case you will be forwarded to the last page.
            window.location.replace(`?page=${response.pagination.totalPages}`);
        }
        else {
            let currentCardNo = 1;
            response.myArtists.forEach(artist => {
                const artistDivElement = this.renderArtistCard(artist);
                this.attachArtistCard(artistDivElement, currentCardNo);
                currentCardNo++;
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
        const followIcon = followIconElement.getElementsByTagName("i").item(0)!;
        const artistNameElement = artistDivElement.querySelector("#artist-name") as HTMLParagraphElement;
        const followedSinceElement = artistDivElement.querySelector("#followed-since") as HTMLDivElement;

        artistThumbElement.src = artist.thumb;
        artistNameElement.textContent = artist.artistName;
        followedSinceElement.innerHTML = this.createFollowedSinceString(artist.followedSince);
        followIconElement.addEventListener(
            "click",
            this.handleFollowIconClick.bind(this, followIcon, artist)
        );

        return artistDivElement;
    }

    private attachArtistCard(artistDivElement: HTMLDivElement, currentCarNo: number): void {
        if (this.rowElement === undefined || currentCarNo % this.MAX_CARDS_PER_ROW === 1) {
            this.rowElement = document.createElement("div");
            this.rowElement.className = "row";
        }
        this.rowElement.insertAdjacentElement("beforeend", artistDivElement);
        this.hostElement.insertAdjacentElement("beforeend", this.rowElement);
    }

    private createFollowedSinceString(followedSince: string): string {
        const followedSinceString = this.dateFormatService.format(followedSince, DateFormat.LONG)
        return `<i class="material-icons">favorite</i> on ${followedSinceString}`;
    }

    private handleFollowIconClick(followIconElement: HTMLElement, artist: Artist): void {
        this.followArtistService.handleFollowIconClick(followIconElement, {
            externalId: artist.externalId,
            artistName: artist.artistName,
            source: artist.source
        })
    }

    private attachPagination(paginationData: Pagination): void {
        const paginationList = this.paginationComponent.render(paginationData);
        this.hostElement.insertAdjacentElement("beforeend", paginationList);
    }
}
