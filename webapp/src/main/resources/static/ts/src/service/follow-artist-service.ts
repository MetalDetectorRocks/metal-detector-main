import { ArtistsRestClient } from "../clients/artists-rest-client";
import { ToastService } from "./toast-service";

export class FollowArtistService {

    private readonly artistRestClient: ArtistsRestClient;
    private readonly toastService: ToastService;

    constructor(artistRestClient: ArtistsRestClient, toastService: ToastService) {
        this.artistRestClient = artistRestClient;
        this.toastService = toastService;
    }

    public followArtist(artistId: string, artistName: string): void {
        this.artistRestClient.followArtist(artistId);
        const toastText = `You are now following "${artistName}"`;
        this.toastService.createToast(toastText);
    }

    public unfollowArtist(artistId: string, artistName: string): void {
        this.artistRestClient.unfollowArtist(artistId);
        const toastText = `You no longer follow "${artistName}"`;
        this.toastService.createToast(toastText);
    }
}
