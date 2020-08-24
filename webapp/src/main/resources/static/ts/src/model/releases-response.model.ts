import { Pagination } from "./pagination.model";
import { Release } from "./release.model";

export interface ReleasesResponse {

    readonly releases: Release[];
    readonly pagination: Pagination;

}
