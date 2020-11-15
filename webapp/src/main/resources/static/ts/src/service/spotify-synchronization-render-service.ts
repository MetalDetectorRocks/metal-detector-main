import {SpotifyRestClient} from "../clients/spotify-rest-client";
import {ToastService} from "./toast-service";

export class SpotifySynchronizationRenderService {

    private readonly spotifyRestClient: SpotifyRestClient;
    private readonly toastService: ToastService;

    constructor(spotifyRestClient: SpotifyRestClient, toastService: ToastService) {
        this.spotifyRestClient = spotifyRestClient;
        this.toastService = toastService;
    }

    public init(): void {
        this.addEventListener();
    }

    private addEventListener(): void {
        const authorizationButton = document.getElementById("spotify-connect-button")! as HTMLButtonElement;
        authorizationButton.addEventListener("click", this.connectWithSpotify.bind(this));

        const importButton = document.getElementById("spotify-synchronize-button")! as HTMLButtonElement;
        importButton.addEventListener("click", this.doSpotifyArtistImport.bind(this));
    }

    private connectWithSpotify(): void {
        const authorizationResponse = this.spotifyRestClient.getAuthorizationUrl()
        authorizationResponse.then(response => window.location.href = response.authorizationUrl);
    }

    private doSpotifyArtistImport(): void {
        const importResponse = this.spotifyRestClient.importArtists();
        console.log(importResponse);
        // importResponse.then(response => this.toastService.createInfoToast("Successfully imported " + response.artists.length + " artists!"));
    }
}
