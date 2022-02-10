import { AlertService } from "../service/util/alert-service";
import { LoadingIndicatorService } from "../service/util/loading-indicator-service";
import { MostFollowedArtistsSwiperRenderer } from "../service/render/most-followed-artists-swiper-renderer";
import { MostExpectedReleasesSwiperRenderer } from "../service/render/most-expected-releases-swiper-renderer";
import { DateService } from "../service/util/date-service";
import { ArtistsRestClient } from "../clients/artists-rest-client";
import { ReleasesRestClient } from "../clients/releases-rest-client";
import { ToastService } from "../service/util/toast-service";
import { UrlService } from "../service/util/url-service";

const alertService = new AlertService();
const loadingIndicatorService = new LoadingIndicatorService();
const dateService = new DateService();
const toastService = new ToastService();
const urlService = new UrlService();
const artistsRestClient = new ArtistsRestClient(urlService, toastService);
const releasesRestClient = new ReleasesRestClient(urlService, dateService);

const mostFollowedArtistsSwiperRenderer = new MostFollowedArtistsSwiperRenderer(alertService, loadingIndicatorService);
const mostFollowedArtistsResponse = artistsRestClient.fetchTopArtists();
mostFollowedArtistsSwiperRenderer.render(mostFollowedArtistsResponse);

const mostExpectedReleasesSwiperRenderer = new MostExpectedReleasesSwiperRenderer(
    alertService,
    loadingIndicatorService,
    dateService,
);
const mostExpectedReleasesResponse = releasesRestClient.fetchTopReleases();
mostExpectedReleasesSwiperRenderer.render(mostExpectedReleasesResponse);
