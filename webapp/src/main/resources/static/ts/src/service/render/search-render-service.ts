import { FollowArtistService } from "../follow-artist-service";
import { LoadingIndicatorService } from "../util/loading-indicator-service";
import { PaginationComponent } from "../../components/pagination/pagination-component";
import { AlertService } from "../util/alert-service";
import { AbstractRenderService } from "./abstract-render-service";
import { SearchResponse } from "../../model/search-response.model";
import { Pagination } from "../../model/pagination.model";
import { SearchResponseEntry } from "../../model/search-response-entry.model";
import { FollowState } from "../../model/follow-state.model";
import { AuthenticationRestClient } from "../../clients/authentication-rest-client";
import { ToastService } from "../util/toast-service";

export class SearchRenderService extends AbstractRenderService<SearchResponse> {
    private static readonly MAX_NAME_LENGTH = 50;

    private readonly followArtistService: FollowArtistService;
    private readonly authenticationRestClient: AuthenticationRestClient;
    private readonly toastService: ToastService;
    private readonly paginationComponent: PaginationComponent;
    private readonly topSearchResultTemplateElement: HTMLTemplateElement;
    private readonly searchResultTemplateElement: HTMLTemplateElement;
    private readonly paginationWrapper: HTMLDivElement;
    private authenticated = false;

    constructor(
        followArtistService: FollowArtistService,
        authenticationRestClient: AuthenticationRestClient,
        toastService: ToastService,
        alertService: AlertService,
        loadingIndicatorService: LoadingIndicatorService,
    ) {
        super(alertService, loadingIndicatorService);
        this.followArtistService = followArtistService;
        this.authenticationRestClient = authenticationRestClient;
        this.toastService = toastService;
        this.paginationComponent = new PaginationComponent();
        this.topSearchResultTemplateElement = document.getElementById("top-search-result") as HTMLTemplateElement;
        this.searchResultTemplateElement = document.getElementById("search-result") as HTMLTemplateElement;
        this.paginationWrapper = document.getElementById("pagination-wrapper") as HTMLDivElement;
        authenticationRestClient.getAuthentication().then((response) => (this.authenticated = response.authenticated));
    }

    protected getHostElementId(): string {
        return "search-result-wrapper";
    }

    protected onRendering(searchResponse: SearchResponse): void {
        const query = searchResponse.query || "";
        const currentPage = searchResponse.pagination.currentPage;
        const totalPages = searchResponse.pagination.totalPages;
        const itemsOnThisPage = searchResponse.searchResults.length;

        if (currentPage == 1 && itemsOnThisPage === 0) {
            this.showNoResultsFoundInfoMessage(query);
        } else if (itemsOnThisPage === 0) {
            this.showSpotifyBugInfoMessage();
        } else {
            this.createSearchResultCards(searchResponse, query);
        }

        if (totalPages > 1) {
            this.attachPagination(searchResponse.pagination);
        }
    }

    private showNoResultsFoundInfoMessage(query: string): void {
        const message = `<h3 class="h5">No results could be found for "${query}".</h3>Try changing your search query.`;
        const infoMessage = this.alertService.renderInfoAlert(message, false);
        this.hostElement.insertAdjacentElement("afterbegin", infoMessage);
    }

    private showSpotifyBugInfoMessage() {
        const spotifyBugTicket =
            "https://community.spotify.com/t5/Spotify-for-Developers/Search-API-returns-wrong-total-amount-of-results/td-p/5006005";
        const message =
            '<h3 class="h5">Arghhh! There are no results on this page.</h3>This is due to a bug in the Spotify REST API. ' +
            `They will burn in hell for this.<br /><a href="${spotifyBugTicket}" target="_blank">Please support our bug report.</a>`;
        const infoMessage = this.alertService.renderInfoAlert(message, false);
        this.hostElement.insertAdjacentElement("afterbegin", infoMessage);
    }

    private createSearchResultCards(searchResponse: SearchResponse, query: string): void {
        const headline = document.createElement("h1");
        headline.classList.add("h4", "mb-4");
        headline.innerText = this.createHeadlineText(query, searchResponse);
        this.hostElement.insertAdjacentElement("afterbegin", headline);

        searchResponse.searchResults.forEach((entry) => {
            const card = this.renderSearchResultCard(entry);
            this.hostElement.insertAdjacentElement("beforeend", card);
        });
    }

    private createHeadlineText(query: string, searchResponse: SearchResponse): string {
        const totalPages = searchResponse.pagination.totalPages;
        const itemsPerPage = searchResponse.pagination.itemsPerPage;

        if (totalPages === 1) {
            const amount = searchResponse.searchResults.length;
            const resultWord = searchResponse.searchResults.length === 1 ? "result" : "results";
            return `${amount} ${resultWord} for "${query}"`;
        } else {
            const estimatedAmountOfResults = (totalPages - 1) * itemsPerPage;
            return `More than ${estimatedAmountOfResults} results for "${query}"`;
        }
    }

    private determineFollowerAmountStatement(metalDetectorFollower: number): string {
        let followerAmountStatement = "";

        if (metalDetectorFollower === 0) {
            followerAmountStatement = "Not followed by any user";
        } else if (metalDetectorFollower === 1) {
            followerAmountStatement = "Followed by 1 user";
        } else if (metalDetectorFollower > 1) {
            followerAmountStatement = `Followed by ${metalDetectorFollower} users`;
        }

        return followerAmountStatement;
    }

    private renderSearchResultCard(entry: SearchResponseEntry): HTMLDivElement {
        const node = document.importNode(this.searchResultTemplateElement.content, true);
        const searchResultDivElement = node.firstElementChild as HTMLDivElement;

        const nameElement = searchResultDivElement.querySelector("#name") as HTMLParagraphElement;
        nameElement.textContent = this.shorten(entry.name);

        const thumbElement = searchResultDivElement.querySelector("#thumb") as HTMLImageElement;
        thumbElement.src = this.determineArtistImageUrl(entry.smallImage);
        thumbElement.alt = entry.name;

        const followIconDivElement = searchResultDivElement.querySelector("#follow-icon") as HTMLDivElement;
        const followIconElement = followIconDivElement.getElementsByTagName("img").item(0)!;
        const followInfoElement = searchResultDivElement.querySelector("#follow-info") as HTMLParagraphElement;
        followIconDivElement.addEventListener("click", this.handleFollowIconClick.bind(this, followIconElement, entry));
        followIconElement.src = entry.followed
            ? FollowState.FOLLOWING.toString()
            : FollowState.NOT_FOLLOWING.toString();
        followInfoElement.textContent = this.determineFollowerAmountStatement(entry.metalDetectorFollower);

        const genreElement = searchResultDivElement.querySelector("#genres") as HTMLParagraphElement;
        entry.genres.splice(0, 3).forEach((genre) => {
            const genreBadge = document.createElement("span");
            genreBadge.classList.add("badge", "badge-dark", "me-2");
            genreBadge.textContent = genre;
            genreElement.insertAdjacentElement("beforeend", genreBadge);
        });

        return searchResultDivElement;
    }

    private handleFollowIconClick(followIconElement: HTMLImageElement, entry: SearchResponseEntry) {
        if (this.authenticated) {
            this.followArtistService.handleFollowIconClick(followIconElement, {
                externalId: entry.id,
                artistName: entry.name,
                source: entry.source,
            });
        } else {
            this.toastService.createInfoToast("Please sign in to follow artists.");
        }
    }

    private attachPagination(paginationData: Pagination) {
        const paginationList = this.paginationComponent.render(paginationData);
        this.paginationWrapper.insertAdjacentElement("beforeend", paginationList);
    }

    private determineArtistImageUrl(imageUrl: string): string {
        return imageUrl.trim() ? imageUrl : "/images/unknown-img.jpg";
    }

    private shorten(value: string): string {
        if (value.length > SearchRenderService.MAX_NAME_LENGTH) {
            return value.trim().substring(0, SearchRenderService.MAX_NAME_LENGTH).trimRight().concat("...");
        }

        return value;
    }
}
