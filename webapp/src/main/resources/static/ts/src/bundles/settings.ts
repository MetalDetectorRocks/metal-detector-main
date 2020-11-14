import {SettingsRenderService} from "../service/settings-render-service";
import {ToastService} from "../service/toast-service";
import {SpotifyRestClient} from "../clients/spotify-rest-client";
import {UrlService} from "../service/url-service";

const toastService = new ToastService();
const urlService = new UrlService();
const spotifyAuthorizationRestClient = new SpotifyRestClient(toastService);
const settingsRenderService = new SettingsRenderService(toastService, spotifyAuthorizationRestClient, urlService);

settingsRenderService.render();
