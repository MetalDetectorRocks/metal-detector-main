import {Artist} from "./artist.model";
import {Release} from "./release.model";

export interface HomepageResponse {

    readonly upcomingReleases: Release[];
    readonly recentReleases: Release[];
    readonly recentlyFollowedArtists: Artist[];

}
