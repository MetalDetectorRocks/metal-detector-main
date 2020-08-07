import { Pagination } from "./pagination.model";
import { SearchResponseEntry } from "./search-response-entry.model";

export interface SearchResponse {

    readonly searchResults: SearchResponseEntry[];
    readonly pagination: Pagination;

}
