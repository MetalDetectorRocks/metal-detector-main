import {Artist} from "./artist.model";
import {Release} from "./release.model";

export interface HomepageResponse {

    readonly recentlyFollowedArtists: Artist[];
    readonly recentReleases: Release[];

}
