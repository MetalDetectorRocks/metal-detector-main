import {FollowArtistService} from "./follow-artist-service";
import {LoadingIndicatorService} from "./loading-indicator-service";
import {PaginationComponent} from "../components/pagination/pagination-component";
import {AlertService} from "./alert-service";
import {AbstractRenderService} from "./abstract-render-service";
import {SearchResponse} from "../model/search-response.model";
import {Pagination} from "../model/pagination.model";
import {SearchResponseEntry} from "../model/search-response-entry.model";
import {FollowState} from "../model/follow-state.model";

interface SearchCardSelectorNames {
    readonly nameSelector: string;
    readonly thumbSelector: string;
    readonly followIconSelector: string;
}

export class SearchRenderService extends AbstractRenderService<SearchResponse> {

    private static readonly MAX_NAME_LENGTH = 50;

    private readonly followArtistService: FollowArtistService;
    private readonly paginationComponent: PaginationComponent;
    private readonly topSearchResultTemplateElement: HTMLTemplateElement;
    private readonly searchResultTemplateElement: HTMLTemplateElement;

    constructor(followArtistService: FollowArtistService, alertService: AlertService, loadingIndicatorService: LoadingIndicatorService) {
        super(alertService, loadingIndicatorService);
        this.followArtistService = followArtistService;
        this.paginationComponent = new PaginationComponent({
            hrefTextPrefix: `query=${this.getQueryFromUrl()}`
        });
        this.topSearchResultTemplateElement = document.getElementById("top-search-result") as HTMLTemplateElement;
        this.searchResultTemplateElement = document.getElementById("search-result") as HTMLTemplateElement;
    }

    protected getHostElementId(): string {
        return "search-result-container";
    }

    protected onRendering(searchResponse: SearchResponse): void {
        const query = new URL(window.location.href).searchParams.get("query") || "";
        const currentPage = searchResponse.pagination.currentPage;
        const totalPages = searchResponse.pagination.totalPages;
        const itemsOnThisPage = searchResponse.searchResults.length;

        if (currentPage == 1 && itemsOnThisPage === 0) {
            this.showNoResultsFoundInfoMessage(query);
        }
        else if (itemsOnThisPage === 0) {
            this.showSpotifyBugInfoMessage();
        }
        else {
            this.createSearchResultCards(searchResponse, query, currentPage, itemsOnThisPage);
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
        const spotifyBugTicket = "https://community.spotify.com/t5/Spotify-for-Developers/Search-API-returns-wrong-total-amount-of-results/td-p/5006005";
        const message = '<h3 class="h5">Arghhh! There are no results on this page.</h3>This is due to a bug in the Spotify REST API. ' +
            `They will burn in hell for this.<br /><a href="${spotifyBugTicket}" target="_blank">Please support our bug report.</a>`;
        const infoMessage = this.alertService.renderInfoAlert(message, false);
        this.hostElement.insertAdjacentElement("afterbegin", infoMessage);
    }

    private createSearchResultCards(searchResponse: SearchResponse, query: string, currentPage: number, itemsOnThisPage: number): void {
        const headline = document.createElement("h1");
        headline.classList.add("h4", "mb-4");
        headline.innerText = this.createHeadlineText(query, searchResponse);
        this.hostElement.insertAdjacentElement("afterbegin", headline);

        if (currentPage === 1) {
            const topSearchResult = this.renderTopSearchResult(searchResponse.searchResults[0]);
            this.hostElement.insertAdjacentElement("beforeend", topSearchResult);
        }

        if (currentPage === 1 && itemsOnThisPage > 1 || currentPage > 1) {
            const otherHeadline = document.createElement("h2");
            otherHeadline.classList.add("h5", "custom-border-bottom", "pb-1");
            otherHeadline.textContent = "Other";
            this.hostElement.insertAdjacentElement("beforeend", otherHeadline);

            const otherSearchResults = this.renderOtherSearchResults(currentPage, searchResponse.searchResults);
            this.hostElement.insertAdjacentElement("beforeend", otherSearchResults);
        }
    }

    private createHeadlineText(query: string, searchResponse: SearchResponse): string {
        const totalPages = searchResponse.pagination.totalPages;
        const itemsPerPage = searchResponse.pagination.itemsPerPage;

        if (totalPages === 1) {
            const amount = searchResponse.searchResults.length;
            const resultWord = searchResponse.searchResults.length === 1 ? "result" : "results";
            return `${amount} ${resultWord} for "${query}"`;
        }
        else {
            const estimatedAmountOfResults = (totalPages - 1) * itemsPerPage;
            return `More than ${estimatedAmountOfResults} results for "${query}"`;
        }
    }

    private renderTopSearchResult(entry: SearchResponseEntry): HTMLDivElement {
        const topSearchResultDivElement = this.renderSearchResult(entry, {
            nameSelector: "#top-name",
            thumbSelector: "#top-thumb",
            followIconSelector: "#top-follow-icon"
        });

        const followInfoElement = topSearchResultDivElement.querySelector("#follow-info") as HTMLParagraphElement;
        followInfoElement.textContent = this.determineFollowerAmountStatement(entry.metalDetectorFollower);

        return topSearchResultDivElement;
    }

    private determineFollowerAmountStatement(metalDetectorFollower: number): string {
        let followerAmountStatement = "";

        if (metalDetectorFollower === 0) {
            followerAmountStatement = "Not followed by any users";
        }
        else if (metalDetectorFollower === 1) {
            followerAmountStatement = "Followed by 1 user";
        }
        else if (metalDetectorFollower > 1) {
            followerAmountStatement = `Followed by ${metalDetectorFollower} users`;
        }

        return followerAmountStatement;
    }

    private renderOtherSearchResults(currentPage: number, searchResults: SearchResponseEntry[]): HTMLDivElement {
        const searchResultWrapper = document.createElement("div");
        let rowWrapper: HTMLDivElement = document.createElement("div");
        rowWrapper.classList.add("row", "mt-1", "mb-4");

        let counter = 0;
        searchResults.forEach((entry, index) => {
            if (index === 0 && currentPage === 1) { // this is the top result
                return;
            }
            else if (counter === 3) {
                searchResultWrapper.insertAdjacentElement("beforeend", rowWrapper);
                rowWrapper = document.createElement("div");
                rowWrapper.classList.add("row", "mb-4");
                counter = 0;
            }

            rowWrapper.insertAdjacentElement("beforeend", this.renderOtherSearchResult(entry));
            counter++;
        });

        searchResultWrapper.insertAdjacentElement("beforeend", rowWrapper);
        return searchResultWrapper;
    }

    private renderOtherSearchResult(entry: SearchResponseEntry): HTMLDivElement {
        return this.renderSearchResult(entry, {
            nameSelector: "#other-name",
            thumbSelector: "#other-thumb",
            followIconSelector: "#other-follow-icon"
        });
    }

    private renderSearchResult(entry: SearchResponseEntry, selectorNames: SearchCardSelectorNames): HTMLDivElement {
        const searchResultTemplateNode = document.importNode(this.searchResultTemplateElement.content, true);
        const searchResultDivElement = searchResultTemplateNode.firstElementChild as HTMLDivElement;
        const nameElement = searchResultDivElement.querySelector(selectorNames.nameSelector) as HTMLParagraphElement;
        const thumbElement = searchResultDivElement.querySelector(selectorNames.thumbSelector) as HTMLImageElement;
        const followIconDivElement = searchResultDivElement.querySelector(selectorNames.followIconSelector) as HTMLDivElement;
        const followIconElement = followIconDivElement.getElementsByTagName("i").item(0)!;

        thumbElement.src = this.determineArtistImageUrl(entry.imageUrl);
        thumbElement.alt = entry.name;
        nameElement.textContent = this.shorten(entry.name);
        followIconDivElement.addEventListener(
            "click",
            this.handleFollowIconClick.bind(this, followIconElement, entry)
        );
        followIconElement.textContent = entry.followed ? FollowState.FOLLOWING.toString() : FollowState.NOT_FOLLOWING.toString();

        return searchResultDivElement;
    }

    private handleFollowIconClick(followIconElement: HTMLElement, entry: SearchResponseEntry) {
        this.followArtistService.handleFollowIconClick(followIconElement, {
            externalId: entry.id,
            artistName: entry.name,
            source: entry.source
        })
    }

    private attachPagination(paginationData: Pagination) {
        const paginationList = this.paginationComponent.render(paginationData);
        this.hostElement.insertAdjacentElement("beforeend", paginationList);
    }

    private determineArtistImageUrl(imageUrl: string): string {
        return imageUrl.trim() ? imageUrl : "/images/unknown-img.jpg";
    }

    private getQueryFromUrl(): string {
        return new URL(window.location.href).searchParams.get("query") || ""
    }

    private shorten(value: string) : string {
        if (value.length > SearchRenderService.MAX_NAME_LENGTH) {
            return value.trim().substring(0, SearchRenderService.MAX_NAME_LENGTH).trimRight().concat("...");
        }

        return value;
    }
}
