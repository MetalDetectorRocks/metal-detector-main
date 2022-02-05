import { Artist } from "./artist.model";
import { Release } from "./release.model";

export interface HomeResponse {
    readonly mostExpectedReleases: Release[];
    readonly mostFollowedArtists: Artist[];
}
