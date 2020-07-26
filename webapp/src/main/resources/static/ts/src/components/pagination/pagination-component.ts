import { Pagination } from "../../model/pagination.model";

export interface PaginationComponentProps {

    readonly displayPages?: number;
    readonly hrefTextPrefix?: string,
    readonly prevText?: string,
    readonly nextText?: string
}

export class PaginationComponent {

    readonly displayPages: number;
    readonly href: string;
    readonly prevText: string;
    readonly nextText: string;

    constructor(props?: PaginationComponentProps) {
        this.displayPages = props?.displayPages || 5;
        this.href = props?.hrefTextPrefix ? `?${props.hrefTextPrefix}&page=` : "?page=";
        this.prevText = props?.prevText || "&laquo;";
        this.nextText = props?.nextText || "&raquo;";
    }

    public render(pagination: Pagination): HTMLUListElement {
        const paginationList = document.createElement("ul") as HTMLUListElement;
        paginationList.classList.add("pagination", "justify-content-end");

        paginationList.insertAdjacentElement("beforeend", this.createPreviousLink(pagination));
        this.createPageItems(pagination).forEach((pageItem) => {
            paginationList.insertAdjacentElement("beforeend", pageItem);
        })
        paginationList.insertAdjacentElement("beforeend", this.createNextLink(pagination));
        return paginationList;
    }

    private createPageItems(pagination: Pagination): HTMLElement[] {
        const pageItems: HTMLElement[] = [];
        for (let page = 1; page <= pagination.totalPages; page++) {
            const pageLink = document.createElement("a") as HTMLAnchorElement;
            pageLink.classList.add("page-link");
            pageLink.innerText = page.toString();

            const pageItem = document.createElement("li");
            pageItem.classList.add("page-item");

            if (page === pagination.currentPage) {
                pageItem.classList.add("active");
            }
            else {
                pageLink.href = this.href + page
            }

            pageItem.insertAdjacentElement("afterbegin", pageLink);
            pageItems.push(pageItem);
        }

        return pageItems;
    }

    private createPreviousLink(pagination: Pagination): HTMLElement {
        const prevItem = document.createElement("li");
        const prevLink = document.createElement("a") as HTMLAnchorElement;

        prevItem.classList.add("page-item");
        if (pagination.currentPage === 1) {
            prevItem.classList.add("disabled");
        }

        prevLink.classList.add("page-link", "prev");
        prevLink.innerHTML = this.prevText;
        if (pagination.currentPage > 1 && pagination.currentPage <= pagination.totalPages) {
            const previousPage = pagination.currentPage - 1;
            prevLink.href = this.href + previousPage;
        }

        prevItem.insertAdjacentElement("afterbegin", prevLink);
        return prevItem;
    }

    private createNextLink(pagination: Pagination) {
        const nextItem = document.createElement("li");
        const nextLink = document.createElement("a") as HTMLAnchorElement;

        nextItem.classList.add("page-item");
        if (pagination.currentPage === pagination.totalPages) {
            nextItem.classList.add("disabled");
        }

        nextLink.classList.add("page-link", "next");
        nextLink.innerHTML = this.nextText;
        if (pagination.currentPage >= 1 && pagination.currentPage < pagination.totalPages) {
            const nextPage = pagination.currentPage + 1;
            nextLink.href = this.href + nextPage;
        }

        nextItem.insertAdjacentElement("afterbegin", nextLink);
        return nextItem;
    }
}
