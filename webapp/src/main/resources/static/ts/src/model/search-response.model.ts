import {Pagination} from "./pagination.model";
import {SearchResponseEntry} from "./search-response-entry.model";

export interface SearchResponse {

    readonly query: string;
    readonly searchResults: SearchResponseEntry[];
    readonly pagination: Pagination;

}
