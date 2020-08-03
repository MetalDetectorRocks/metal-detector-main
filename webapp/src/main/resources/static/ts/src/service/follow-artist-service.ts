import { ArtistsRestClient } from "../clients/artists-rest-client";
import { ToastService } from "./toast-service";
import { FollowState } from "../model/follow-state.model";

export interface FollowArtistInfo {

    readonly externalId: string;
    readonly artistName: string;
    readonly source: string;
}

export class FollowArtistService {

    private readonly artistRestClient: ArtistsRestClient;
    private readonly toastService: ToastService;

    constructor(artistRestClient: ArtistsRestClient, toastService: ToastService) {
        this.artistRestClient = artistRestClient;
        this.toastService = toastService;
    }

    public handleFollowIconClick(followIconElement: HTMLElement, info: FollowArtistInfo): void {
        const currentFollowState = followIconElement.textContent;

        if (currentFollowState === FollowState.FOLLOWING.toString()) {
            this.unfollowArtist(info);
            followIconElement.textContent = FollowState.NOT_FOLLOWING.toString();
        }
        else {
            this.followArtist(info);
            followIconElement.textContent = FollowState.FOLLOWING.toString();
        }
    }

    public followArtist(info: FollowArtistInfo): void {
        this.artistRestClient.followArtist(info.externalId, info.source);
        const toastText = `You are now following "${info.artistName}"`;
        this.toastService.createToast(toastText);
    }

    public unfollowArtist(info: FollowArtistInfo): void {
        this.artistRestClient.unfollowArtist(info.externalId, info.source);
        const toastText = `You no longer follow "${info.artistName}"`;
        this.toastService.createToast(toastText);
    }
}
