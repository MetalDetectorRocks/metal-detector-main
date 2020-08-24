import { LoadingIndicatorService } from "../service/loading-indicator-service";
import { AlertService } from "../service/alert-service";
import { ReleasesRestClient } from "../clients/releases-rest-client";
import { ReleasesRenderService } from "../service/releases-render-service";

const loadingIndicatorService = new LoadingIndicatorService();
const alertService = new AlertService();
const releasesRestClient = new ReleasesRestClient();
const releasesRenderService = new ReleasesRenderService(alertService, loadingIndicatorService);

const response = releasesRestClient.fetchReleases();
releasesRenderService.render(response);
