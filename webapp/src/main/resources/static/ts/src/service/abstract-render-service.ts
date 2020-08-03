import { AlertService } from "./alert-service";
import { LoadingIndicatorService } from "./loading-indicator-service";

export abstract class AbstractRenderService<T> {

    protected readonly alertService: AlertService;
    protected readonly loadingIndicatorService: LoadingIndicatorService;
    protected readonly hostElement: HTMLDivElement;

    protected constructor(alertService: AlertService, loadingIndicatorService: LoadingIndicatorService) {
        this.alertService = alertService;
        this.loadingIndicatorService = loadingIndicatorService;
        this.hostElement = document.getElementById(this.getHostElementId())! as HTMLDivElement;
    }

    public render(data: Promise<T>): void{
        this.loadingIndicatorService.showLoadingIndicator(this.getHostElementId());
        data.then((response) => {
            this.onRendering(response);
        }).catch((reason) => {
            console.debug(reason);
            this.onCatch();
        }).finally(() => {
            this.loadingIndicatorService.hideLoadingIndicator(this.getHostElementId());
        });
    }

    protected abstract getHostElementId(): string;

    protected abstract onRendering(data: T): void;

    protected onCatch(): void {
        const message = '<h3 class="h5">Damn it! A satanic error has occurred.</h3>Please try again later.';
        const infoMessage = this.alertService.renderErorAlert(message, false);
        this.hostElement.insertAdjacentElement("afterbegin", infoMessage);
    }
}
