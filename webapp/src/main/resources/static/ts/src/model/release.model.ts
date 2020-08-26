export interface Release {

    readonly artist: string;
    readonly additionalArtists: string[];
    readonly albumTitle: string;
    readonly releaseDate: Date;
    readonly estimatedReleaseDate: string;
    readonly genre: string;
    readonly type: string;
    readonly metalArchivesArtistUrl: string;
    readonly metalArchivesAlbumUrl: string;
    readonly source: string;
    readonly state: string;
    readonly coverUrl: string;

}
