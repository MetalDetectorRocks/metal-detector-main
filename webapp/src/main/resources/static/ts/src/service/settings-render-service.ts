import {SpotifyRestClient} from "../clients/spotify-rest-client";

export class SettingsRenderService {

    private readonly spotifyRestClient: SpotifyRestClient;

    constructor(spotifyRestClient: SpotifyRestClient) {
        this.spotifyRestClient = spotifyRestClient;
    }

    public render(): void {
        const authorizationButton = document.getElementById("authorization-button")! as HTMLButtonElement;
        authorizationButton.addEventListener("click", this.doSpotifyRedirect.bind(this));
    }

    private doSpotifyRedirect(): void {
        const authorizationResponse = this.spotifyRestClient.getAuthorizationUrl()
        authorizationResponse.then(response => window.location.href = response.authorizationUrl);
    }
}
