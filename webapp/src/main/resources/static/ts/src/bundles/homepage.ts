import { HomepageRestClient } from "../clients/homepage-rest-client";
import { HomepageRenderService } from "../service/homepage-render-service";
import { AlertService } from "../service/alert-service";
import { LoadingIndicatorService } from "../service/loading-indicator-service";
import { DateFormatService } from "../service/date-format-service";

const alertService = new AlertService();
const loadingIndicatorService = new LoadingIndicatorService();
const dateFormatService = new DateFormatService();
const homepageRestClient = new HomepageRestClient();
const homepageRenderService = new HomepageRenderService(alertService, loadingIndicatorService, dateFormatService);

const response = homepageRestClient.fetchHomepage();
homepageRenderService.render(response);
