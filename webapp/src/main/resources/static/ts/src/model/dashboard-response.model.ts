import { Artist } from "./artist.model";
import { Release } from "./release.model";

export interface DashboardResponse {
    readonly upcomingReleases: Release[];
    readonly recentReleases: Release[];
    readonly mostExpectedReleases: Release[];
    readonly recentlyFollowedArtists: Artist[];
    readonly favoriteCommunityArtists: Artist[];
}
