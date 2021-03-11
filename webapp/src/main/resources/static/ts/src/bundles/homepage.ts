import { HomepageRestClient } from "../clients/homepage-rest-client";
import { HomepageRenderService } from "../service/homepage-render-service";
import { AlertService } from "../service/alert-service";
import { LoadingIndicatorService } from "../service/loading-indicator-service";
import { DateService } from "../service/date-service";
import { FollowArtistService } from "../service/follow-artist-service";
import { ArtistsRestClient } from "../clients/artists-rest-client";
import { UrlService } from "../service/url-service";
import { ToastService } from "../service/toast-service";

const alertService = new AlertService();
const loadingIndicatorService = new LoadingIndicatorService();
const dateService = new DateService();
const homepageRestClient = new HomepageRestClient();
const urlService = new UrlService();
const toastService = new ToastService();
const artistsRestClient = new ArtistsRestClient(urlService, toastService);
const followArtistService = new FollowArtistService(artistsRestClient, toastService);
const homepageRenderService = new HomepageRenderService(
    alertService,
    loadingIndicatorService,
    dateService,
    followArtistService,
);

const response = homepageRestClient.fetchHomepage();
homepageRenderService.render(response);
