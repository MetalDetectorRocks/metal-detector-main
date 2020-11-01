import {LoadingIndicatorService} from "../service/loading-indicator-service";
import {ArtistsRestClient} from "../clients/artists-rest-client";
import {FollowArtistService} from "../service/follow-artist-service";
import {ToastService} from "../service/toast-service";
import {SearchRenderService} from "../service/search-render-service";
import {AlertService} from "../service/alert-service";
import {UrlService} from "../service/url-service";

const urlService = new UrlService();
const toastService = new ToastService();
const loadingIndicatorService = new LoadingIndicatorService();
const alertService = new AlertService();
const artistsRestClient = new ArtistsRestClient(urlService, toastService);
const followArtistService = new FollowArtistService(artistsRestClient, toastService);
const searchRenderService = new SearchRenderService(followArtistService, alertService, loadingIndicatorService);

const response = artistsRestClient.searchArtist();
searchRenderService.render(response);
