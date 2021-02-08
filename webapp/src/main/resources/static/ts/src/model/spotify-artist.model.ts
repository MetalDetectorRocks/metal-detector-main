export interface SpotifyArtist {
    readonly id: string;
    readonly name: string;
    readonly thumbnailImage: string;
    readonly smallImage: string;
    readonly mediumImage: string;
    readonly largeImage: string;
    readonly uri: string;
    readonly genres: string[];
    readonly popularity: number;
    readonly follower: number;
}
