import { SpotifyArtist } from "./spotify-artist.model";

export interface SpotifyFetchArtistsResponse {
    readonly artists: SpotifyArtist[];
}
