import {SettingsRenderService} from "../service/settings-render-service";
import {ToastService} from "../service/toast-service";
import {SpotifyRestClient} from "../clients/spotify-rest-client";

const toastService = new ToastService();
const spotifyAuthorizationRestClient = new SpotifyRestClient(toastService);
const settingsRenderService = new SettingsRenderService(toastService, spotifyAuthorizationRestClient);

settingsRenderService.render();
