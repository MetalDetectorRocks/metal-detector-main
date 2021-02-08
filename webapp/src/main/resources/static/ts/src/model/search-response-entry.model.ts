export interface SearchResponseEntry {
    readonly id: string;
    readonly name: string;
    readonly thumbnailImage: string;
    readonly smallImage: string;
    readonly mediumImage: string;
    readonly largeImage: string;
    readonly uri: string;
    readonly source: string;
    readonly followed: boolean;
    readonly genres: string[];
    readonly popularity: string;
    readonly metalDetectorFollower: number;
    readonly spotifyFollower: number;
}
