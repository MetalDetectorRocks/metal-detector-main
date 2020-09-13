import {LoadingIndicatorService} from "../service/loading-indicator-service";
import {AlertService} from "../service/alert-service";
import {ReleasesRestClient} from "../clients/releases-rest-client";
import {ReleasesRenderService} from "../service/releases-render-service";
import {DateFormatService} from "../service/date-format-service";

const loadingIndicatorService = new LoadingIndicatorService();
const alertService = new AlertService();
const dateFormatService = new DateFormatService();
const releasesRestClient = new ReleasesRestClient(dateFormatService);
const releasesRenderService = new ReleasesRenderService(dateFormatService, alertService, loadingIndicatorService);

const response = releasesRestClient.fetchReleases();
releasesRenderService.render(response);
