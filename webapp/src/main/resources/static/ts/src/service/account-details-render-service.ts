import { AccountDetailsRestClient } from "../clients/account-details-rest-client";
import { ToastService } from "./toast-service";

export class AccountDetailsRenderService {
    private readonly toastService: ToastService;
    private readonly accountDetailsRestClient: AccountDetailsRestClient;

    constructor(toastService: ToastService, accountDetailsRestClient: AccountDetailsRestClient) {
        this.toastService = toastService;
        this.accountDetailsRestClient = accountDetailsRestClient;
    }

    public init(): void {
        const response = this.accountDetailsRestClient.getAccountDetails();
        response.then((response) => {
            if (response.email) {
                const inputElement = document.getElementById("email-address") as HTMLInputElement;
                inputElement.value = response.email;
            }
        });

        const updateEmailAddressButton = document.getElementById("update-email-address") as HTMLButtonElement;
        updateEmailAddressButton.addEventListener("click", this.onUpdateEmailAddressClicked.bind(this));

        const deleteAccountButton = document.getElementById("delete-account") as HTMLButtonElement;
        deleteAccountButton.addEventListener("click", this.onDeleteAccountClicked.bind(this));
    }

    private onUpdateEmailAddressClicked(): void {
        const inputElement = document.getElementById("email-address") as HTMLInputElement;
        this.accountDetailsRestClient
            .updateEmailAddress(inputElement.value)
            .then((response) => (inputElement.value = response))
            .then(() => this.toastService.createInfoToast("Successfully updated email address!"));
    }

    private onDeleteAccountClicked(): void {
        this.accountDetailsRestClient.deleteAccount().then(() => {
            window.location.href = "/";
        });
    }
}
