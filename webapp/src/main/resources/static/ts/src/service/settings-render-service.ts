import {SpotifyRestClient} from "../clients/spotify-rest-client";
import {ToastService} from "./toast-service";

export class SettingsRenderService {

    private readonly spotifyRestClient: SpotifyRestClient;
    private readonly toastService: ToastService;

    constructor(toastService: ToastService, spotifyRestClient: SpotifyRestClient) {
        this.toastService = toastService;
        this.spotifyRestClient = spotifyRestClient;
    }

    public render(): void {
        const authorizationButton = document.getElementById("authorization-button")! as HTMLButtonElement;
        authorizationButton.addEventListener("click", this.doSpotifyRedirect.bind(this));

        const state = new URL(window.location.href).searchParams.get("state") || "";
        const code = new URL(window.location.href).searchParams.get("code") || "";

        this.spotifyRestClient.fetchInitialToken(state, code)
          .then(() => this.toastService.createInfoToast("Spotify authorized!"));

    }

    private doSpotifyRedirect(): void {
        const authorizationResponse = this.spotifyRestClient.getAuthorizationUrl()
        authorizationResponse.then(response => window.location.href = response.authorizationUrl);
    }
}
