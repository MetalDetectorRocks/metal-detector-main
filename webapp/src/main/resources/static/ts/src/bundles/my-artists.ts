import { ArtistsRestClient } from "../clients/artists-rest-client";
import { MyArtistsRenderService } from "../service/my-artists-render-service";
import { FollowArtistService } from "../service/follow-artist-service";
import { ToastService } from "../service/toast-service";
import { AlertService } from "../service/alert-service";
import { LoadingIndicatorService } from "../service/loading-indicator-service";

const toastService = new ToastService();
const alertService = new AlertService();
const loadingIndicatorService = new LoadingIndicatorService();
const artistsRestClient = new ArtistsRestClient();
const followArtistService = new FollowArtistService(artistsRestClient, toastService);
const myArtistsRenderService = new MyArtistsRenderService(followArtistService, alertService, loadingIndicatorService);

const response = artistsRestClient.fetchMyArtists();
myArtistsRenderService.render(response);
