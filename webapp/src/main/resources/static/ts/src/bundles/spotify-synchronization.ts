import { SpotifySynchronizationRenderService } from "../service/render/spotify-synchronization-render-service";
import { ToastService } from "../service/util/toast-service";
import { SpotifyRestClient } from "../clients/spotify-rest-client";
import { UrlService } from "../service/util/url-service";
import { LoadingIndicatorService } from "../service/util/loading-indicator-service";
import { AlertService } from "../service/util/alert-service";
import { OauthRestClient } from "../clients/oauth-rest-client";

const toastService = new ToastService();
const loadingIndicatorService = new LoadingIndicatorService();
const spotifyRestClient = new SpotifyRestClient(toastService);
const oauthRestClient = new OauthRestClient(toastService);
const urlService = new UrlService();
const alertService = new AlertService();
const spotifySynchronizationRenderService = new SpotifySynchronizationRenderService(
    spotifyRestClient,
    oauthRestClient,
    loadingIndicatorService,
    toastService,
    urlService,
    alertService,
);

spotifySynchronizationRenderService.init();
