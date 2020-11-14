import {SpotifyRestClient} from "../clients/spotify-rest-client";
import {ToastService} from "./toast-service";
import {UrlService} from "./url-service";

export class SettingsRenderService {

    private readonly spotifyRestClient: SpotifyRestClient;
    private readonly toastService: ToastService;
    private readonly urlService: UrlService

    constructor(toastService: ToastService, spotifyRestClient: SpotifyRestClient, urlService: UrlService) {
        this.toastService = toastService;
        this.spotifyRestClient = spotifyRestClient;
        this.urlService = urlService;
    }

    public render(): void {
        const authorizationButton = document.getElementById("authorization-button")! as HTMLButtonElement;
        authorizationButton.addEventListener("click", this.doSpotifyRedirect.bind(this));

        const state = this.urlService.getParameterFromUrl("state")
        const code = this.urlService.getParameterFromUrl("code")

        this.spotifyRestClient.fetchInitialToken(state, code)
          .then(() => this.toastService.createInfoToast("Spotify authorized!"));

    }

    private doSpotifyRedirect(): void {
        const authorizationResponse = this.spotifyRestClient.createAuthorizationUrl()
        authorizationResponse.then(response => window.location.href = response.authorizationUrl);
    }
}
