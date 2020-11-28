export interface SpotifyArtist {

    readonly id: string;
    readonly name: string;
    readonly imageUrl: string;
    readonly uri: string;
    readonly genres: string[];
    readonly popularity: number;
    readonly follower: number;
}