import {SpotifyAuthorizationRestClient} from "../clients/spotify-authorization-rest-client";

export class ProfileRenderService {

    private readonly spotifyAuthorizationRestClient: SpotifyAuthorizationRestClient;

    constructor(spotifyAuthorizationRestClient: SpotifyAuthorizationRestClient) {
        this.spotifyAuthorizationRestClient = spotifyAuthorizationRestClient;
    }

    public render(): void {
        const authorizationButton = document.getElementById("authorization-button")! as HTMLButtonElement;
        authorizationButton.addEventListener("click", this.doSpotifyRedirect.bind(this));
    }

    private doSpotifyRedirect() {
        const authorizationResponse = this.spotifyAuthorizationRestClient.getAuthorizationUrl()
        authorizationResponse.then(response => window.location.href = response.authorizationUrl);
    }
}
