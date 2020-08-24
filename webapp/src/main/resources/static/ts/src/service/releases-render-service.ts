import { AbstractRenderService } from "./abstract-render-service";
import { ReleasesResponse } from "../model/releases-response.model";
import { PaginationComponent } from "../components/pagination/pagination-component";
import { AlertService } from "./alert-service";
import { LoadingIndicatorService } from "./loading-indicator-service";

export class ReleasesRenderService extends AbstractRenderService<ReleasesResponse> {

    private readonly paginationComponent: PaginationComponent;
    private readonly releasesTemplateElement: HTMLTemplateElement;

    constructor(alertService: AlertService, loadingIndicatorService: LoadingIndicatorService) {
        super(alertService, loadingIndicatorService);
        this.paginationComponent = new PaginationComponent();
        this.releasesTemplateElement = document.getElementById("release-card") as HTMLTemplateElement;
    }

    protected getHostElementId(): string {
        return "releases-container";
    }

    protected onRendering(data: ReleasesResponse): void {
        console.log(data);
    }
}
