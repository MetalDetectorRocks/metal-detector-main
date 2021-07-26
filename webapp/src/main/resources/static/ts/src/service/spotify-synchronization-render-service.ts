import { SpotifyRestClient } from "../clients/spotify-rest-client";
import { ToastService } from "./toast-service";
import { UrlService } from "./url-service";
import { LoadingIndicatorService } from "./loading-indicator-service";
import { SpotifyArtist } from "../model/spotify-artist.model";
import { AlertService } from "./alert-service";
import { AxiosError } from "axios";
import { OauthRestClient } from "../clients/oauth-rest-client";

export class SpotifySynchronizationRenderService {
    private static readonly OAUTH2_CLIENT_REGISTRATION_ID = "spotify-user";
    private static readonly ARTISTS_SELECTION_BAR_ID = "artists-selection-bar";
    private static readonly ARTISTS_CONTAINER_ID = "spotify-artists-container";

    private connected = false;
    private readonly spotifyRestClient: SpotifyRestClient;
    private readonly oauthRestClient: OauthRestClient;
    private readonly loadingIndicatorService: LoadingIndicatorService;
    private readonly toastService: ToastService;
    private readonly urlService: UrlService;
    private readonly alertService: AlertService;

    private readonly artistSelectionElement: HTMLDivElement;
    private readonly artistContainerElement: HTMLDivElement;

    private readonly spotifyConnectionLink: HTMLSpanElement;
    private readonly spotifyConnectionStatus: HTMLSpanElement;
    private readonly fetchArtistsButton: HTMLButtonElement;
    private readonly synchronizeArtistsButton: HTMLButtonElement;

    constructor(
        spotifyRestClient: SpotifyRestClient,
        oauthRestClient: OauthRestClient,
        loadingIndicatorService: LoadingIndicatorService,
        toastService: ToastService,
        urlService: UrlService,
        alertService: AlertService,
    ) {
        this.spotifyRestClient = spotifyRestClient;
        this.oauthRestClient = oauthRestClient;
        this.loadingIndicatorService = loadingIndicatorService;
        this.toastService = toastService;
        this.urlService = urlService;
        this.alertService = alertService;

        this.spotifyConnectionLink = document.getElementById("spotify-connection-link") as HTMLSpanElement;
        this.spotifyConnectionStatus = document.getElementById("spotify-connection-status") as HTMLSpanElement;
        this.fetchArtistsButton = document.getElementById("fetch-artists-button") as HTMLButtonElement;
        this.synchronizeArtistsButton = document.getElementById("synchronize-artists-button") as HTMLButtonElement;
        this.artistSelectionElement = document.getElementById(
            SpotifySynchronizationRenderService.ARTISTS_SELECTION_BAR_ID,
        ) as HTMLDivElement;
        this.artistContainerElement = document.getElementById(
            SpotifySynchronizationRenderService.ARTISTS_CONTAINER_ID,
        ) as HTMLDivElement;

        this.addEventListener();
    }

    private addEventListener(): void {
        this.spotifyConnectionLink.addEventListener("click", this.onSpotifyConnectionLinkClicked.bind(this));
        this.synchronizeArtistsButton.addEventListener("click", this.onSynchronizeArtistsClicked.bind(this));
        document
            .getElementById("fetch-from-saved-albums")!
            .addEventListener("click", this.onFetchSpotifyArtistsFromAlbumsClicked.bind(this));
        document
            .getElementById("fetch-from-saved-artists")!
            .addEventListener("click", this.onFetchSpotifyArtistsFromArtistsClicked.bind(this));
        document
            .getElementById("fetch-from-both")!
            .addEventListener("click", this.onFetchSpotifyArtistsFromBothClicked.bind(this));
        document
            .getElementById("select-all-link")!
            .addEventListener("click", this.onSelectOrDeselectAllArtistsClicked.bind(this, true));
        document
            .getElementById("deselect-all-link")!
            .addEventListener("click", this.onSelectOrDeselectAllArtistsClicked.bind(this, false));
    }

    public init(): void {
        const response = this.oauthRestClient.existsAuthorization(
            SpotifySynchronizationRenderService.OAUTH2_CLIENT_REGISTRATION_ID,
        );
        response.then((response) => {
            if (response.exists) {
                this.onConnected();
            } else {
                this.onDisconnected();
            }
        });
    }

    private onSpotifyConnectionLinkClicked(): void {
        if (this.connected) {
            this.oauthRestClient
                .deleteAuthorization(SpotifySynchronizationRenderService.OAUTH2_CLIENT_REGISTRATION_ID)
                .then(() => {
                    this.onDisconnected();
                })
                .catch((error: AxiosError) => {
                    console.log(error);
                });
        } else {
            this.oauthRestClient.authenticate(SpotifySynchronizationRenderService.OAUTH2_CLIENT_REGISTRATION_ID);
        }
    }

    private onConnected(): void {
        this.connected = true;
        this.spotifyConnectionLink.textContent = "Disconnect";
        this.spotifyConnectionStatus.textContent = "Connected";
        this.spotifyConnectionStatus.classList.replace("font-color-red", "font-color-green");
        [this.fetchArtistsButton, this.synchronizeArtistsButton].forEach((button) => {
            button.classList.remove("invisible");
        });
    }

    private onDisconnected(): void {
        this.connected = false;
        this.spotifyConnectionLink.textContent = "Connect";
        this.spotifyConnectionStatus.textContent = "Disconnected";
        this.spotifyConnectionStatus.classList.replace("font-color-green", "font-color-red");
        [this.fetchArtistsButton, this.synchronizeArtistsButton, this.artistSelectionElement].forEach((el) => {
            el.classList.add("invisible");
        });
        this.clearArtistsContainer();
    }

    private onFetchSpotifyArtistsFromAlbumsClicked(): void {
        this.fetchArtists(["ALBUMS"]);
    }

    private onFetchSpotifyArtistsFromArtistsClicked(): void {
        this.fetchArtists(["ARTISTS"]);
    }

    private onFetchSpotifyArtistsFromBothClicked(): void {
        this.fetchArtists(["ALBUMS", "ARTISTS"]);
    }

    private fetchArtists(fetchTypes: string[]): void {
        this.loadingIndicatorService.showLoadingIndicator(SpotifySynchronizationRenderService.ARTISTS_CONTAINER_ID);
        this.clearArtistsContainer();
        const savedArtists = this.spotifyRestClient.fetchSavedArtists(fetchTypes);
        let savedArtistsAvailable = false;
        savedArtists
            .then((response) => {
                savedArtistsAvailable = response.artists.length > 0;
                response.artists.forEach((artist) => {
                    const artistTemplateElement = document.getElementById("artist-card") as HTMLTemplateElement;
                    const artistTemplateNode = document.importNode(artistTemplateElement.content, true);
                    const artistDivElement = artistTemplateNode.firstElementChild as HTMLDivElement;
                    const artistThumbElement = artistDivElement.querySelector("#thumb") as HTMLImageElement;
                    const artistNameElement = artistDivElement.querySelector("#artist-name") as HTMLParagraphElement;
                    const artistInfoElement = artistDivElement.querySelector("#artist-info") as HTMLParagraphElement;

                    artistDivElement.id = artist.id;
                    artistThumbElement.src = artist.smallImage;
                    artistNameElement.textContent = artist.name;
                    artistInfoElement.insertAdjacentElement("beforeend", this.buildArtistInfoElement(artist));
                    artistDivElement.addEventListener("click", this.onArtistClicked.bind(this, artistDivElement));
                    this.artistContainerElement.insertAdjacentElement("beforeend", artistDivElement);
                });
            })
            .finally(() => {
                if (savedArtistsAvailable) {
                    this.artistSelectionElement.classList.remove("invisible");
                    this.synchronizeArtistsButton?.classList.remove("disabled");
                } else {
                    const infoIcon = '<span class="material-icons">info</span>';
                    const infoMessage = `${infoIcon} You already follow all the artists on Metal Detector that you also follow on Spotify.`;
                    const infoMessageElement = this.alertService.renderInfoAlert(infoMessage, true);
                    this.artistContainerElement.insertAdjacentElement("beforeend", infoMessageElement);
                }
                this.loadingIndicatorService.hideLoadingIndicator(
                    SpotifySynchronizationRenderService.ARTISTS_CONTAINER_ID,
                );
            });
    }

    private buildArtistInfoElement(artist: SpotifyArtist): HTMLSpanElement {
        const artistInfoElement = document.createElement("span");
        artistInfoElement.insertAdjacentElement("beforeend", this.buildGenreTagsElement(artist));
        artistInfoElement.insertAdjacentHTML("beforeend", "<br />");
        artistInfoElement.insertAdjacentElement("beforeend", this.buildSpotifyFollowerElement(artist));
        return artistInfoElement;
    }

    private buildGenreTagsElement(artist: SpotifyArtist): HTMLSpanElement {
        const genreElement = document.createElement("span");
        artist.genres.slice(0, 3).forEach((genre) => {
            const genreBadge = document.createElement("span");
            genreBadge.classList.add("badge", "badge-dark", "me-1", "mb-1");
            genreBadge.textContent = genre;
            genreElement.insertAdjacentElement("beforeend", genreBadge);
        });
        return genreElement;
    }

    private buildSpotifyFollowerElement(artist: SpotifyArtist): HTMLSpanElement {
        const followerCount = new Intl.NumberFormat("en-us", { minimumFractionDigits: 0 }).format(artist.follower);
        const followerElement = document.createElement("span");
        followerElement.innerText = `${followerCount} followers on Spotify`;
        return followerElement;
    }

    private onSynchronizeArtistsClicked(): void {
        const disabled = this.synchronizeArtistsButton.classList.contains("disabled");
        if (!disabled) {
            this.synchronizeArtists();
        }
    }

    private synchronizeArtists(): void {
        const artistCards = this.artistContainerElement.getElementsByClassName("spotify-synchro-card");
        const selectedArtistIds: string[] = [];
        Array.from(artistCards).forEach((artistCard) => {
            const artistCheckbox = artistCard.querySelector("#artist-check-box") as HTMLSpanElement;
            const isSelected = artistCheckbox.innerText === "check_box";
            if (isSelected) {
                selectedArtistIds.push(artistCard.id);
            }
        });

        this.clearArtistsContainer();
        this.loadingIndicatorService.showLoadingIndicator(SpotifySynchronizationRenderService.ARTISTS_CONTAINER_ID);
        this.spotifyRestClient
            .synchronizeArtists(selectedArtistIds)
            .then((response) => {
                const successIcon = '<span class="material-icons">check_circle</span>';
                const artistOrArtistsLabel = response.artistsCount > 1 ? "artists" : "artist";
                const successMessage = `${successIcon} You are now following ${response.artistsCount} new ${artistOrArtistsLabel} on Metal Detector.`;
                const successMessageElement = this.alertService.renderSuccessAlert(successMessage, true);
                this.artistContainerElement.insertAdjacentElement("beforeend", successMessageElement);
            })
            .finally(() => {
                this.loadingIndicatorService.hideLoadingIndicator(
                    SpotifySynchronizationRenderService.ARTISTS_CONTAINER_ID,
                );
                this.artistSelectionElement.classList.add("invisible");
                this.synchronizeArtistsButton?.classList.add("disabled");
            });
    }

    private onArtistClicked(artistDivElement: HTMLDivElement): void {
        const artistCheckbox = artistDivElement.querySelector("#artist-check-box") as HTMLSpanElement;
        this.handleSelection(artistDivElement, artistCheckbox, artistCheckbox.innerText !== "check_box");
    }

    private onSelectOrDeselectAllArtistsClicked(shouldSelect: boolean): void {
        const artistCards = this.artistContainerElement.getElementsByClassName("spotify-synchro-card");
        Array.from(artistCards).forEach((artistCard) => {
            const artistCheckbox = artistCard.querySelector("#artist-check-box") as HTMLSpanElement;
            this.handleSelection(artistCard as HTMLDivElement, artistCheckbox, shouldSelect);
        });
    }

    private handleSelection(
        artistDivElement: HTMLDivElement,
        artistCheckbox: HTMLSpanElement,
        shouldSelect: boolean,
    ): void {
        const artistThumbElement = artistDivElement.querySelector("#thumb") as HTMLImageElement;
        const artistNameElement = artistDivElement.querySelector("#artist-name") as HTMLParagraphElement;
        const artistInfoElement = artistDivElement.querySelector("#artist-info") as HTMLParagraphElement;

        if (shouldSelect) {
            artistDivElement.classList.add("active");
            artistCheckbox.innerText = "check_box";
            artistCheckbox.classList.add("md-success");
            artistCheckbox.classList.remove("md-light");
            artistThumbElement.classList.remove("img-inactive");
            artistNameElement.classList.remove("text-muted");
            artistInfoElement.classList.remove("text-muted");
        } else {
            artistDivElement.classList.remove("active");
            artistCheckbox.innerText = "check_box_outline_blank";
            artistCheckbox.classList.add("md-light");
            artistCheckbox.classList.remove("md-success");
            artistThumbElement.classList.add("img-inactive");
            artistNameElement.classList.add("text-muted");
            artistInfoElement.classList.add("text-muted");
        }
    }

    private clearArtistsContainer(): void {
        this.artistContainerElement.innerHTML = "";
    }
}
