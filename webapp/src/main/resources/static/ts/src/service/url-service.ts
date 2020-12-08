export class UrlService {

    public getPageFromUrl(): string {
        const url = new URL(window.location.href);
        let page = url.searchParams.get("page") || "1";
        if (Number.isNaN(page) || +page < 1) {
            page = "1";
        }

        return page;
    }

    public getParameterFromUrl(parameterName: string): string {
        const url = new URL(window.location.href);
        return url.searchParams.get(parameterName) || "";
    }

    public getParametersFromUrl(parameterName: string): string[] {
        const url = new URL(window.location.href);
        return url.searchParams.getAll(parameterName) || "";
    }

    public getPathFromUrl(): string {
        const url = new URL(window.location.href);
        return url.pathname || "";
    }
}
