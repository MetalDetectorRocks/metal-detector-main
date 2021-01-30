import { Pagination } from "./pagination.model";
import { Release } from "./release.model";

export interface ReleasesResponse {
    readonly items: Release[];
    readonly pagination: Pagination;
}
