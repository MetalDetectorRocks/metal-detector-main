import { ArtistsRestClient } from "../clients/artists-rest-client";
import { ToastService } from "./toast-service";
import { Artist } from "../model/artist.model";

export class FollowArtistService {

    private readonly artistRestClient: ArtistsRestClient;
    private readonly toastService: ToastService;

    constructor(artistRestClient: ArtistsRestClient, toastService: ToastService) {
        this.artistRestClient = artistRestClient;
        this.toastService = toastService;
    }

    public followArtist(artist: Artist): void {
        this.artistRestClient.followArtist(artist.externalId, artist.source);
        const toastText = `You are now following "${artist.artistName}"`;
        this.toastService.createToast(toastText);
    }

    public unfollowArtist(artist: Artist): void {
        this.artistRestClient.unfollowArtist(artist.externalId, artist.source);
        const toastText = `You no longer follow "${artist.artistName}"`;
        this.toastService.createToast(toastText);
    }
}
