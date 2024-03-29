import { LoadingIndicatorService } from "../service/util/loading-indicator-service";
import { ArtistsRestClient } from "../clients/artists-rest-client";
import { FollowArtistService } from "../service/follow-artist-service";
import { ToastService } from "../service/util/toast-service";
import { SearchRenderService } from "../service/render/search-render-service";
import { AlertService } from "../service/util/alert-service";
import { UrlService } from "../service/util/url-service";
import { AuthenticationRestClient } from "../clients/authentication-rest-client";

const urlService = new UrlService();
const toastService = new ToastService();
const loadingIndicatorService = new LoadingIndicatorService();
const alertService = new AlertService();
const artistsRestClient = new ArtistsRestClient(urlService, toastService);
const authenticationRestClient = new AuthenticationRestClient();
const followArtistService = new FollowArtistService(artistsRestClient, toastService);
const searchRenderService = new SearchRenderService(
    followArtistService,
    authenticationRestClient,
    toastService,
    alertService,
    loadingIndicatorService,
);

const response = artistsRestClient.searchArtist();
searchRenderService.render(response);
