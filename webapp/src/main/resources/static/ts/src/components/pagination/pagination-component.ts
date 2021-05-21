import { Pagination } from "../../model/pagination.model";

export interface PaginationComponentProps {
    readonly additionalUrlParameter?: Array<[string, string]>;
    readonly prevText?: string;
    readonly nextText?: string;
}

export class PaginationComponent {
    private readonly urlParams: URLSearchParams;
    private readonly prevText: string;
    private readonly nextText: string;
    private paginationList?: HTMLUListElement;

    constructor(props?: PaginationComponentProps) {
        this.urlParams = new URLSearchParams(window.location.search);
        props?.additionalUrlParameter?.forEach((urlParam) => this.urlParams.append(urlParam[0], urlParam[1]));
        this.prevText = props?.prevText || "&laquo;";
        this.nextText = props?.nextText || "&raquo;";
    }

    public render(pagination: Pagination): HTMLUListElement {
        this.paginationList = document.createElement("ul") as HTMLUListElement;
        this.paginationList.classList.add("pagination");
        this.insertPreviousLink(pagination.currentPage, pagination.totalPages);

        // no dotting for 10 pages or less
        if (pagination.totalPages <= 10) {
            this.insertPageItems(1, pagination.totalPages, pagination.currentPage);
        }
        // dotting before last page if current page is in first block (first 4 pages)
        else if (pagination.currentPage <= 4) {
            this.insertPageItems(1, 5, pagination.currentPage);
            this.insertDottedPageItem();
            this.insertPageItem(pagination.totalPages);
        }
        // dotting after first page if current page is in last block (last 4 pages)
        else if (pagination.currentPage > pagination.totalPages - 4) {
            this.insertPageItem(1);
            this.insertDottedPageItem();
            this.insertPageItems(pagination.totalPages - 4, pagination.totalPages, pagination.currentPage);
        }
        // dotting after first and last page if current page is somewhere in between
        else {
            this.insertPageItem(1);
            this.insertDottedPageItem();
            this.insertPageItems(pagination.currentPage - 2, pagination.currentPage + 2, pagination.currentPage);
            this.insertDottedPageItem();
            this.insertPageItem(pagination.totalPages);
        }

        this.insertNextLink(pagination.currentPage, pagination.totalPages);
        return this.paginationList;
    }

    private insertPreviousLink(currentPage: number, totalPages: number): void {
        this.paginationList?.insertAdjacentElement("beforeend", this.createPreviousLink(currentPage, totalPages));
    }

    private insertNextLink(currentPage: number, totalPages: number): void {
        this.paginationList?.insertAdjacentElement("beforeend", this.createNextLink(currentPage, totalPages));
    }

    private insertPageItems(pageFrom: number, pageTo: number, currentPage?: number): void {
        for (let page = pageFrom; page <= pageTo; page++) {
            this.insertPageItem(page, currentPage);
        }
    }

    private insertPageItem(page: number, currentPage?: number): void {
        const pageItem = this.createPageItem(page, page === currentPage);
        this.paginationList?.insertAdjacentElement("beforeend", pageItem);
    }

    private insertDottedPageItem(): void {
        const pageLink = document.createElement("a") as HTMLAnchorElement;
        pageLink.classList.add("page-link");
        pageLink.innerText = "...";

        const pageItem = document.createElement("li");
        pageItem.classList.add("page-item", "disabled");
        pageItem.insertAdjacentElement("afterbegin", pageLink);

        this.paginationList?.insertAdjacentElement("beforeend", pageItem);
    }

    private createPageItem(page: number, isActivePage: boolean): HTMLElement {
        const pageLink = document.createElement("a") as HTMLAnchorElement;
        pageLink.classList.add("page-link");
        pageLink.innerText = page.toString();

        const pageItem = document.createElement("li");
        pageItem.classList.add("page-item");

        if (isActivePage) {
            pageItem.classList.add("active");
        } else {
            this.urlParams.set("page", page.toString());
            pageLink.href = `?${this.urlParams.toString()}`;
        }

        pageItem.insertAdjacentElement("afterbegin", pageLink);
        return pageItem;
    }

    private createPreviousLink(currentPage: number, totalPages: number): HTMLElement {
        const prevItem = document.createElement("li");
        const prevLink = document.createElement("a") as HTMLAnchorElement;

        prevItem.classList.add("page-item");
        if (currentPage === 1) {
            prevItem.classList.add("disabled");
        }

        prevLink.classList.add("page-link", "prev");
        prevLink.innerHTML = this.prevText;
        if (currentPage > 1 && currentPage <= totalPages) {
            const previousPage = currentPage - 1;
            this.urlParams.set("page", previousPage.toString());
            prevLink.href = `?${this.urlParams.toString()}`;
        }

        prevItem.insertAdjacentElement("afterbegin", prevLink);
        return prevItem;
    }

    private createNextLink(currentPage: number, totalPages: number) {
        const nextItem = document.createElement("li");
        const nextLink = document.createElement("a") as HTMLAnchorElement;

        nextItem.classList.add("page-item");
        if (currentPage === totalPages) {
            nextItem.classList.add("disabled");
        }

        nextLink.classList.add("page-link", "next");
        nextLink.innerHTML = this.nextText;
        if (currentPage >= 1 && currentPage < totalPages) {
            const nextPage = currentPage + 1;
            this.urlParams.set("page", nextPage.toString());
            nextLink.href = `?${this.urlParams.toString()}`;
        }

        nextItem.insertAdjacentElement("afterbegin", nextLink);
        return nextItem;
    }
}
