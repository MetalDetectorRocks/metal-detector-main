import {SpotifyRestClient} from "../clients/spotify-rest-client";
import {ToastService} from "./toast-service";
import {UrlService} from "./url-service";
import {LoadingIndicatorService} from "./loading-indicator-service";

export class SpotifySynchronizationRenderService {

    private static readonly SPOTIFY_CALLBACK_PATH_NAME = "spotify-callback";
    private static readonly BUTTON_BAR_DIV_NAME = "button-bar";

    private readonly spotifyRestClient: SpotifyRestClient;
    private readonly loadingIndicatorService: LoadingIndicatorService;
    private readonly toastService: ToastService;
    private readonly urlService: UrlService;

    constructor(spotifyRestClient: SpotifyRestClient, loadingIndicatorService: LoadingIndicatorService,
                toastService: ToastService, urlService: UrlService) {
        this.spotifyRestClient = spotifyRestClient;
        this.loadingIndicatorService = loadingIndicatorService;
        this.toastService = toastService;
        this.urlService = urlService;
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

                const synchronizeArtistsButton = document.createElement("button");
                synchronizeArtistsButton.className = "btn btn-outline-success btn-lg";
                synchronizeArtistsButton.insertAdjacentHTML("afterbegin", `<span class="material-icons">sync</span> Synchronize artists`);
                synchronizeArtistsButton.addEventListener("click", this.synchronizeSpotifyArtists.bind(this));

                buttonBar.insertAdjacentElement("beforeend", spotifyConnectedButton);
                buttonBar.insertAdjacentElement("beforeend", synchronizeArtistsButton);
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

    private synchronizeSpotifyArtists(): void {
        const importResponse = this.spotifyRestClient.importArtists();
        console.log(importResponse);
        // importResponse.then(response => this.toastService.createInfoToast("Successfully imported " + response.artists.length + " artists!"));
    }
}
