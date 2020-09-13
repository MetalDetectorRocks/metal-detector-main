export abstract class AbstractRestClient {

    protected getPageFromUrl(): string {
        const url = new URL(window.location.href);
        let page = url.searchParams.get("page") || "1";
        if (Number.isNaN(page) || +page < 1) {
            page = "1";
        }

        return page;
    }

    protected getParameterFromUrl(parameterName: string): string {
        const url = new URL(window.location.href);
        return url.searchParams.get(parameterName) || "";
    }
}
