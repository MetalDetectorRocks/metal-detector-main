import {HomepageRestClient} from "../clients/homepage-rest-client";
import {HomepageRenderService} from "../service/homepage-render-service";
import {AlertService} from "../service/alert-service";
import {LoadingIndicatorService} from "../service/loading-indicator-service";

const alertService = new AlertService();
const loadingIndicatorService = new LoadingIndicatorService();
const homepageRestClient = new HomepageRestClient();
const homepageRenderService = new HomepageRenderService(alertService, loadingIndicatorService);

const response = homepageRestClient.fetchHomepage();
homepageRenderService.render(response);
