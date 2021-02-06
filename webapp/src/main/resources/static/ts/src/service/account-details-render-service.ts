import { AccountDetailsRestClient } from "../clients/account-details-rest-client";
import { AlertService } from "./alert-service";
import { UNKNOWN_ERROR_MESSAGE } from "../config/messages.config";

export class AccountDetailsRenderService {
    private readonly alertService: AlertService;
    private readonly accountDetailsRestClient: AccountDetailsRestClient;
    private readonly emailInput: HTMLInputElement;
    private readonly updateEmailButton: HTMLButtonElement;
    private readonly emailUpdateErrorMessageHost: HTMLDivElement;
    private readonly deleteAccountButton: HTMLButtonElement;

    constructor(alertService: AlertService, accountDetailsRestClient: AccountDetailsRestClient) {
        this.alertService = alertService;
        this.accountDetailsRestClient = accountDetailsRestClient;
        this.emailInput = document.getElementById("email-address") as HTMLInputElement;
        this.emailUpdateErrorMessageHost = document.getElementById(
            "email-update-error-message-wrapper",
        ) as HTMLDivElement;
        this.updateEmailButton = document.getElementById("update-email-address") as HTMLButtonElement;
        this.deleteAccountButton = document.getElementById("delete-account") as HTMLButtonElement;
    }

    public init(): void {
        this.accountDetailsRestClient
            .getAccountDetails()
            .then((response) => (this.emailInput.value = response.email))
            .catch(() => this.renderEmailUpdateServerError(UNKNOWN_ERROR_MESSAGE));

        this.updateEmailButton.addEventListener("click", this.onUpdateEmailAddressClicked.bind(this));
        this.deleteAccountButton.addEventListener("click", this.onDeleteAccountClicked.bind(this));
    }

    private onUpdateEmailAddressClicked(): void {
        this.clearErrorMessageHost();
        this.accountDetailsRestClient
            .updateEmailAddress(this.emailInput.value)
            .then((response) => {
                this.emailInput.value = response;
                this.emailInput.classList.add("is-valid");
                this.emailInput.classList.remove("is-invalid");
            })
            .catch((error) => {
                this.emailInput.classList.add("is-invalid");
                this.emailInput.classList.remove("is-valid");
                this.renderEmailUpdateServerError(error.response.data.messages);
            });
    }

    private onDeleteAccountClicked(): void {
        this.accountDetailsRestClient.deleteAccount().then(() => {
            window.location.href = "/";
        });
    }

    private renderEmailUpdateServerError(message: string): void {
        const alert = this.alertService.renderErrorAlert(message, true);
        this.emailUpdateErrorMessageHost.insertAdjacentElement("afterbegin", alert);
    }

    private clearErrorMessageHost(): void {
        this.emailUpdateErrorMessageHost.innerHTML = "";
    }
}
