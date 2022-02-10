import { DashboardRestClient } from "../clients/dashboard-rest-client";
import { DashboardRenderService } from "../service/render/dashboard-render-service";
import { AlertService } from "../service/util/alert-service";
import { LoadingIndicatorService } from "../service/util/loading-indicator-service";
import { DateService } from "../service/util/date-service";
import { FollowArtistService } from "../service/follow-artist-service";
import { ArtistsRestClient } from "../clients/artists-rest-client";
import { UrlService } from "../service/util/url-service";
import { ToastService } from "../service/util/toast-service";

const alertService = new AlertService();
const loadingIndicatorService = new LoadingIndicatorService();
const dateService = new DateService();
const dashboardRestClient = new DashboardRestClient();
const urlService = new UrlService();
const toastService = new ToastService();
const artistsRestClient = new ArtistsRestClient(urlService, toastService);
const followArtistService = new FollowArtistService(artistsRestClient, toastService);
const dashboardRenderService = new DashboardRenderService(
    alertService,
    loadingIndicatorService,
    dateService,
    followArtistService,
);

const response = dashboardRestClient.fetchDashboard();
dashboardRenderService.render(response);
