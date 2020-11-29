import {SpotifySynchronizationRenderService} from "../service/spotify-synchronization-render-service";
import {ToastService} from "../service/toast-service";
import {SpotifyRestClient} from "../clients/spotify-rest-client";
import {UrlService} from "../service/url-service";
import {LoadingIndicatorService} from "../service/loading-indicator-service";

const toastService = new ToastService();
const loadingIndicatorService = new LoadingIndicatorService();
const spotifyRestClient = new SpotifyRestClient(toastService);
const urlService = new UrlService();
const spotifySynchronizationRenderService = new SpotifySynchronizationRenderService(spotifyRestClient, loadingIndicatorService, toastService, urlService);

spotifySynchronizationRenderService.init();
