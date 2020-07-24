import axios, { AxiosError, AxiosResponse } from "axios";
import { axiosConfig } from "../config/axios.config";
import { MyArtistsResponse } from "../model/my-artists-response.model";

export class ArtistsRestClient {

    private readonly MY_ARTISTS_URL = "/rest/v1/my-artists";
    private readonly FOLLOW_ARTISTS_URL = "/rest/v1/artists/follow";
    private readonly UNFOLLOW_ARTISTS_URL = "/rest/v1/artists/unfollow";

    constructor() {
    }

    public async fetchMyArtists(): Promise<MyArtistsResponse> {
        return axios.get(this.MY_ARTISTS_URL, axiosConfig)
            .then((response: AxiosResponse<MyArtistsResponse>) => {
                const { data } = response;
                return {
                    myArtists: data.myArtists,
                    pagination: data.pagination
                }
            })
            .catch((error: AxiosError) => {
                console.error(error);
                throw error;
            });
    }

    public followArtist(artistId: string): void {
        axios.post(`${this.FOLLOW_ARTISTS_URL}/${artistId}?source=Discogs`, axiosConfig)
            .catch((error: AxiosError) => {
                throw error;
            });
    }

    public unfollowArtist(artistId: string): void {
        axios.post(`${this.UNFOLLOW_ARTISTS_URL}/${artistId}`, axiosConfig)
            .catch((error: AxiosError) => {
                throw error;
            });
    }
}
