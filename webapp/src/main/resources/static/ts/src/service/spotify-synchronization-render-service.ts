import {SpotifyRestClient} from "../clients/spotify-rest-client";
import {ToastService} from "./toast-service";
import {UrlService} from "./url-service";
import {LoadingIndicatorService} from "./loading-indicator-service";
import {SpotifyArtist} from "../model/spotify-artist.model";

export class SpotifySynchronizationRenderService {

    private static readonly SPOTIFY_CALLBACK_PATH_NAME = "spotify-callback";
    private static readonly BUTTON_BAR_DIV_NAME = "button-bar";
    private static readonly ARTISTS_HOST_ID = "artists-container";

    private readonly spotifyRestClient: SpotifyRestClient;
    private readonly loadingIndicatorService: LoadingIndicatorService;
    private readonly toastService: ToastService;
    private readonly urlService: UrlService;
    private readonly artistContainerElement: HTMLDivElement;

    constructor(spotifyRestClient: SpotifyRestClient, loadingIndicatorService: LoadingIndicatorService,
                toastService: ToastService, urlService: UrlService) {
        this.spotifyRestClient = spotifyRestClient;
        this.loadingIndicatorService = loadingIndicatorService;
        this.toastService = toastService;
        this.urlService = urlService;
        this.artistContainerElement = document.getElementById(SpotifySynchronizationRenderService.ARTISTS_HOST_ID) as HTMLDivElement;
    }

    public init(): void {
        this.loadingIndicatorService.showLoadingIndicator(SpotifySynchronizationRenderService.BUTTON_BAR_DIV_NAME);
        const path = this.urlService.getPathFromUrl();
        if (path.endsWith(SpotifySynchronizationRenderService.SPOTIFY_CALLBACK_PATH_NAME)) {
            const state = this.urlService.getParameterFromUrl("state")
            const code = this.urlService.getParameterFromUrl("code")
            this.spotifyRestClient.fetchInitialToken(state, code)
                .then(() => this.toastService.createInfoToast("Successfully connected with Spotify!"))
                .then(() => {
                    this.initButtonBar();
                    this.loadingIndicatorService.hideLoadingIndicator(SpotifySynchronizationRenderService.BUTTON_BAR_DIV_NAME);
                });
        }
        else {
            this.initButtonBar();
            this.loadingIndicatorService.hideLoadingIndicator(SpotifySynchronizationRenderService.BUTTON_BAR_DIV_NAME);
        }
    }

    private initButtonBar(): void {
        const response = this.spotifyRestClient.existsAuthorization();
        response.then(response => {
            const buttonBar = document.getElementById(SpotifySynchronizationRenderService.BUTTON_BAR_DIV_NAME)! as HTMLDivElement;
            if (response.exists) {
                const spotifyConnectedButton = document.createElement("button");
                spotifyConnectedButton.className = "btn btn-outline-success btn-lg mr-2 disabled";
                spotifyConnectedButton.insertAdjacentHTML("afterbegin", `<span class="material-icons">check_circle</span> Connected with Spotify`);

                const fetchArtistsButton = document.createElement("button");
                fetchArtistsButton.className = "btn btn-outline-success btn-lg";
                fetchArtistsButton.insertAdjacentHTML("afterbegin", `<span class="material-icons">sync</span> Fetch artists`);
                fetchArtistsButton.addEventListener("click", this.fetchSpotifyArtists.bind(this));

                buttonBar.insertAdjacentElement("beforeend", spotifyConnectedButton);
                buttonBar.insertAdjacentElement("beforeend", fetchArtistsButton);
           }
           else {
               const spotifyConnectButton = document.createElement("button");
               spotifyConnectButton.className = "btn btn-outline-success btn-lg";
               spotifyConnectButton.insertAdjacentHTML("afterbegin", `<span class="material-icons">power</span> Connect with Spotify`);
               spotifyConnectButton.addEventListener("click", this.connectWithSpotify.bind(this));
               buttonBar.insertAdjacentElement("afterbegin", spotifyConnectButton);
           }
        });
    }

    private connectWithSpotify(): void {
        const authorizationResponse = this.spotifyRestClient.createAuthorizationUrl()
        authorizationResponse.then(response => window.location.href = response.authorizationUrl);
    }

    private fetchSpotifyArtists(): void {
        // ToDo DanielW:
        //  - show select all / deselect all
        this.loadingIndicatorService.showLoadingIndicator(SpotifySynchronizationRenderService.ARTISTS_HOST_ID);
        this.artistContainerElement.innerHTML = "";
        const followedArtists = this.spotifyRestClient.fetchFollowedArtists();
        followedArtists.then(response => {
            response.artists.forEach(artist => {
                const artistTemplateElement = document.getElementById("artist-card")! as HTMLTemplateElement;
                const artistTemplateNode = document.importNode(artistTemplateElement.content, true);
                const artistDivElement = artistTemplateNode.firstElementChild as HTMLDivElement;
                const artistThumbElement = artistDivElement.querySelector("#thumb") as HTMLImageElement;
                const artistNameElement = artistDivElement.querySelector("#artist-name") as HTMLParagraphElement;
                const artistInfoElement = artistDivElement.querySelector("#artist-info") as HTMLParagraphElement;

                artistThumbElement.src = artist.imageUrl;
                artistNameElement.textContent = artist.name;
                artistInfoElement.innerHTML = this.buildArtistInfoText(artist);
                this.artistContainerElement.insertAdjacentElement("beforeend", artistDivElement);
            });
        }).finally(() => {
            this.loadingIndicatorService.hideLoadingIndicator(SpotifySynchronizationRenderService.ARTISTS_HOST_ID);
        });
    }

    private buildArtistInfoText(artist: SpotifyArtist): string {
        const followerCount = new Intl.NumberFormat("en-us", { minimumFractionDigits: 0 }).format(artist.follower);
        const follower = `${followerCount} followers on Spotify`;
        const genres = artist.genres.slice(0, 3).join(", ");
        return `${genres}<br />${follower}`;
    }
}
