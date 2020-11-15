import {SpotifySynchronizationRenderService} from "../service/spotify-synchronization-render-service";
import {ToastService} from "../service/toast-service";
import {SpotifyRestClient} from "../clients/spotify-rest-client";

const toastService = new ToastService();
const spotifyRestClient = new SpotifyRestClient(toastService);
const settingsRenderService = new SpotifySynchronizationRenderService(spotifyRestClient, toastService);

settingsRenderService.init();
