import {LoadingIndicatorService} from "../service/loading-indicator-service";
import {AlertService} from "../service/alert-service";
import {ReleasesRestClient} from "../clients/releases-rest-client";
import {ReleasesRenderService} from "../service/releases-render-service";
import {DateFormatService} from "../service/date-format-service";
import {ReleasesService} from "../service/releases-service";
import {UrlService} from "../service/url-service";

const loadingIndicatorService = new LoadingIndicatorService();
const alertService = new AlertService();
const urlService = new UrlService();
const dateFormatService = new DateFormatService();
const releasesRestClient = new ReleasesRestClient(urlService, dateFormatService);
const releasesRenderService = new ReleasesRenderService(dateFormatService, alertService, loadingIndicatorService);
const releasesService = new ReleasesService(releasesRestClient, releasesRenderService, urlService);

releasesService.fetchReleases();
