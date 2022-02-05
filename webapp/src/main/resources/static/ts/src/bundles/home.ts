import { AlertService } from "../service/util/alert-service";
import { LoadingIndicatorService } from "../service/util/loading-indicator-service";
import { HomeResponse } from "../model/home-response.model";
import { MostFollowedArtistsSwiperRenderer } from "../service/render/most-followed-artists-swiper-renderer";
import { MostExpectedReleasesSwiperRenderer } from "../service/render/most-expected-releases-swiper-renderer";
import { DateService } from "../service/util/date-service";

const alertService = new AlertService();
const loadingIndicatorService = new LoadingIndicatorService();
const dateService = new DateService();

const mostFollowedArtistsSwiperRenderer = new MostFollowedArtistsSwiperRenderer(alertService, loadingIndicatorService);
const mostExpectedReleasesSwiperRenderer = new MostExpectedReleasesSwiperRenderer(
    alertService,
    loadingIndicatorService,
    dateService,
);

const promiseResponse: Promise<HomeResponse> = new Promise((resolve) => {
    const response = {
        mostFollowedArtists: [
            {
                externalId: "123",
                artistName: "Iron Maiden",
                thumbnailImage: "https://i.scdn.co/image/ab6761610000e5ebdc52c8e309e46aa8430a0fa0",
                smallImage: "",
                mediumImage: "",
                largeImage: "",
                followedSince: "",
                source: "",
                follower: 215,
            },
            {
                externalId: "123",
                artistName: "Iron Maiden",
                thumbnailImage: "https://i.scdn.co/image/ab6761610000e5ebdc52c8e309e46aa8430a0fa0",
                smallImage: "",
                mediumImage: "",
                largeImage: "",
                followedSince: "",
                source: "",
                follower: 215,
            },
            {
                externalId: "123",
                artistName: "Iron Maiden",
                thumbnailImage: "https://i.scdn.co/image/ab6761610000e5ebdc52c8e309e46aa8430a0fa0",
                smallImage: "",
                mediumImage: "",
                largeImage: "",
                followedSince: "",
                source: "",
                follower: 215,
            },
            {
                externalId: "123",
                artistName: "Iron Maiden",
                thumbnailImage: "https://i.scdn.co/image/ab6761610000e5ebdc52c8e309e46aa8430a0fa0",
                smallImage: "",
                mediumImage: "",
                largeImage: "",
                followedSince: "",
                source: "",
                follower: 215,
            },
            {
                externalId: "123",
                artistName: "Iron Maiden",
                thumbnailImage: "https://i.scdn.co/image/ab6761610000e5ebdc52c8e309e46aa8430a0fa0",
                smallImage: "",
                mediumImage: "",
                largeImage: "",
                followedSince: "",
                source: "",
                follower: 215,
            },
        ],
        mostExpectedReleases: [
            {
                artist: "Iron Maiden",
                additionalArtists: [],
                albumTitle: "Iron Maiden",
                releaseDate: "2022-03-29",
                announcementDate: "",
                estimatedReleaseDate: "",
                genre: "",
                type: "",
                metalArchivesArtistUrl: "",
                metalArchivesAlbumUrl: "",
                source: "",
                state: "",
                coverUrl: "https://i.scdn.co/image/ab6761610000e5ebdc52c8e309e46aa8430a0fa0",
                reissue: false,
            },
            {
                artist: "Iron Maiden",
                additionalArtists: [],
                albumTitle: "Iron Maiden",
                releaseDate: "2022-03-29",
                announcementDate: "",
                estimatedReleaseDate: "",
                genre: "",
                type: "",
                metalArchivesArtistUrl: "",
                metalArchivesAlbumUrl: "",
                source: "",
                state: "",
                coverUrl: "https://i.scdn.co/image/ab6761610000e5ebdc52c8e309e46aa8430a0fa0",
                reissue: false,
            },
            {
                artist: "Iron Maiden",
                additionalArtists: [],
                albumTitle: "Iron Maiden",
                releaseDate: "2022-03-29",
                announcementDate: "",
                estimatedReleaseDate: "",
                genre: "",
                type: "",
                metalArchivesArtistUrl: "",
                metalArchivesAlbumUrl: "",
                source: "",
                state: "",
                coverUrl: "https://i.scdn.co/image/ab6761610000e5ebdc52c8e309e46aa8430a0fa0",
                reissue: false,
            },
            {
                artist: "Iron Maiden",
                additionalArtists: [],
                albumTitle: "Iron Maiden",
                releaseDate: "2022-03-29",
                announcementDate: "",
                estimatedReleaseDate: "",
                genre: "",
                type: "",
                metalArchivesArtistUrl: "",
                metalArchivesAlbumUrl: "",
                source: "",
                state: "",
                coverUrl: "https://i.scdn.co/image/ab6761610000e5ebdc52c8e309e46aa8430a0fa0",
                reissue: false,
            },
            {
                artist: "Iron Maiden",
                additionalArtists: [],
                albumTitle: "Iron Maiden",
                releaseDate: "2022-03-29",
                announcementDate: "",
                estimatedReleaseDate: "",
                genre: "",
                type: "",
                metalArchivesArtistUrl: "",
                metalArchivesAlbumUrl: "",
                source: "",
                state: "",
                coverUrl: "https://i.scdn.co/image/ab6761610000e5ebdc52c8e309e46aa8430a0fa0",
                reissue: false,
            },
        ],
    };
    resolve(response);
});

mostFollowedArtistsSwiperRenderer.render(promiseResponse);
mostExpectedReleasesSwiperRenderer.render(promiseResponse);
