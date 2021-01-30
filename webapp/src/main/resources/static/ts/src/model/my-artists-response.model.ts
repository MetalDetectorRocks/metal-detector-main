import { Artist } from "./artist.model";
import { Pagination } from "./pagination.model";

export interface MyArtistsResponse {
    readonly myArtists: Artist[];
    readonly pagination: Pagination;
}
