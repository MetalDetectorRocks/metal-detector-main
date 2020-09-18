import {ProfileRenderService} from "../service/profile-render-service";
import {ToastService} from "../service/toast-service";
import {SpotifyAuthorizationRestClient} from "../clients/spotify-authorization-rest-client";

const toastService = new ToastService();
const spotifyAuthorizationRestClient = new SpotifyAuthorizationRestClient(toastService);
const profileRenderService = new ProfileRenderService(spotifyAuthorizationRestClient);

profileRenderService.render();
