import axios, { AxiosError, AxiosResponse } from "axios";
import { axiosConfig } from "../config/axios.config";
import { AuthenticationResponseModel } from "../model/authentication-response.model";

export class AuthenticationRestClient {
    private readonly AUTHENTICATION_URL = "/rest/v1/authentication";

    public async getAuthentication(): Promise<AuthenticationResponseModel> {
        return await axios
            .get(this.AUTHENTICATION_URL, axiosConfig)
            .then((response: AxiosResponse<AuthenticationResponseModel>) => {
                return response.data;
            })
            .catch((error: AxiosError) => {
                console.error(error);
                throw error;
            });
    }
}
