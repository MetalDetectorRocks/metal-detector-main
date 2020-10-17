import {ProfileRenderService} from "../service/profile-render-service";
import {ToastService} from "../service/toast-service";
import {SpotifyRestClient} from "../clients/spotify-rest-client";

const toastService = new ToastService();
const spotifyAuthorizationRestClient = new SpotifyRestClient(toastService);
const profileRenderService = new ProfileRenderService(spotifyAuthorizationRestClient);

profileRenderService.render();
