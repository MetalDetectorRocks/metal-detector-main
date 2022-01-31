export class LoadingIndicatorService {
    public showLoadingIndicator(elementId: string): void {
        const element = document.getElementById(elementId) as HTMLElement;
        element.classList.add("loader");
    }

    public hideLoadingIndicator(elementId: string): void {
        const element = document.getElementById(elementId) as HTMLElement;
        element.classList.remove("loader");
    }
}
