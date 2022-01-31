import { ArtistsRestClient } from "../clients/artists-rest-client";
import { ToastService } from "./util/toast-service";
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

    public handleFollowIconClick(followIconElement: HTMLImageElement, info: FollowArtistInfo): void {
        const currentFollowState = followIconElement.src;

        if (currentFollowState.endsWith(FollowState.FOLLOWING.toString())) {
            this.unfollowArtist(followIconElement, info);
        } else {
            this.followArtist(followIconElement, info);
        }
    }

    public followArtist(followIconElement: HTMLImageElement, info: FollowArtistInfo): void {
        this.artistRestClient.followArtist(info.externalId, info.source).then(() => {
            const toastText = `You are now following "${info.artistName}"`;
            this.toastService.createInfoToast(toastText);
            followIconElement.src = FollowState.FOLLOWING.toString();
        });
    }

    public unfollowArtist(followIconElement: HTMLImageElement, info: FollowArtistInfo): void {
        this.artistRestClient.unfollowArtist(info.externalId, info.source).then(() => {
            const toastText = `You no longer follow "${info.artistName}"`;
            this.toastService.createInfoToast(toastText);
            followIconElement.src = FollowState.NOT_FOLLOWING.toString();
        });
    }
}
