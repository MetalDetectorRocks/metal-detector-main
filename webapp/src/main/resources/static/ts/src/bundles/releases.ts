import {LoadingIndicatorService} from "../service/loading-indicator-service";
import {AlertService} from "../service/alert-service";
import {ReleasesRestClient} from "../clients/releases-rest-client";
import {ReleasesRenderService} from "../service/releases-render-service";
import {DateService} from "../service/date-service";
import {ReleasesService} from "../service/releases-service";
import {UrlService} from "../service/url-service";

const loadingIndicatorService = new LoadingIndicatorService();
const alertService = new AlertService();
const urlService = new UrlService();
const dateService = new DateService();
const releasesRestClient = new ReleasesRestClient(urlService, dateService);
const releasesRenderService = new ReleasesRenderService(dateService, alertService, loadingIndicatorService);
const releasesService = new ReleasesService(releasesRestClient, releasesRenderService, urlService);

releasesService.fetchReleases();
