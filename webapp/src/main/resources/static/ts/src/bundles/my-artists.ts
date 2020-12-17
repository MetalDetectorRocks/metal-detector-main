import {ArtistsRestClient} from "../clients/artists-rest-client";
import {MyArtistsRenderService} from "../service/my-artists-render-service";
import {FollowArtistService} from "../service/follow-artist-service";
import {ToastService} from "../service/toast-service";
import {AlertService} from "../service/alert-service";
import {LoadingIndicatorService} from "../service/loading-indicator-service";
import {DateService} from "../service/date-service";
import {UrlService} from "../service/url-service";

const urlService = new UrlService();
const toastService = new ToastService();
const alertService = new AlertService();
const loadingIndicatorService = new LoadingIndicatorService();
const artistsRestClient = new ArtistsRestClient(urlService, toastService);
const followArtistService = new FollowArtistService(artistsRestClient, toastService);
const dateService = new DateService();
const myArtistsRenderService = new MyArtistsRenderService(followArtistService, dateService, alertService, loadingIndicatorService);

const response = artistsRestClient.fetchMyArtists();
myArtistsRenderService.render(response);
