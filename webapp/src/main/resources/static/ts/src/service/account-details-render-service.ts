import { AccountDetailsRestClient } from "../clients/account-details-rest-client";
import { AlertService } from "./alert-service";
import { UNKNOWN_ERROR_MESSAGE } from "../config/messages.config";

export class AccountDetailsRenderService {
    private readonly alertService: AlertService;
    private readonly accountDetailsRestClient: AccountDetailsRestClient;
    private readonly emailInput: HTMLInputElement;
    private readonly updateEmailButton: HTMLButtonElement;
    private readonly updateEmailErrorMessageHost: HTMLDivElement;
    private readonly deleteAccountButton: HTMLButtonElement;
    private readonly deleteAccountErrorMessageHost: HTMLDivElement;

    constructor(alertService: AlertService, accountDetailsRestClient: AccountDetailsRestClient) {
        this.alertService = alertService;
        this.accountDetailsRestClient = accountDetailsRestClient;
        this.emailInput = document.getElementById("email-address") as HTMLInputElement;
        this.updateEmailErrorMessageHost = document.getElementById(
            "update-email-error-message-wrapper",
        ) as HTMLDivElement;
        this.updateEmailButton = document.getElementById("update-email-address") as HTMLButtonElement;
        this.deleteAccountButton = document.getElementById("delete-account") as HTMLButtonElement;
        this.deleteAccountErrorMessageHost = document.getElementById(
            "delete-account-error-message-wrapper",
        ) as HTMLDivElement;
    }

    public init(): void {
        this.accountDetailsRestClient
            .getAccountDetails()
            .then((response) => (this.emailInput.value = response.email))
            .catch(() => this.renderServerError(this.updateEmailErrorMessageHost, UNKNOWN_ERROR_MESSAGE));

        this.updateEmailButton.addEventListener("click", this.onUpdateEmailClicked.bind(this));
        this.emailInput.addEventListener("keypress", (event) => this.onEnterPressedInEmailInput(event));
        this.deleteAccountButton.addEventListener("click", this.onDeleteAccountClicked.bind(this));
    }

    private onEnterPressedInEmailInput(event: KeyboardEvent): void {
        if (event.key === "Enter") {
            this.updateEmail();
        }
    }

    private onUpdateEmailClicked(): void {
        this.updateEmail();
    }

    private updateEmail(): void {
        this.clearErrorMessageHosts();
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
                this.renderServerError(this.updateEmailErrorMessageHost, error.response.data.messages);
            });
    }

    private onDeleteAccountClicked(): void {
        this.clearErrorMessageHosts();
        this.accountDetailsRestClient
            .deleteAccount()
            .then(() => (window.location.href = "/"))
            .catch(() => {
                const cancelButton = document.getElementById("close-delete-account-dialog") as HTMLButtonElement;
                cancelButton.click();
                this.renderServerError(
                    this.deleteAccountErrorMessageHost,
                    "An unknown error has occurred, which is why your account cannot be deleted. Please try again later.",
                );
            });
    }

    private renderServerError(host: HTMLDivElement, message: string): void {
        const alert = this.alertService.renderErrorAlert(message, true);
        host.insertAdjacentElement("afterbegin", alert);
    }

    private clearErrorMessageHosts(): void {
        this.updateEmailErrorMessageHost.innerHTML = "";
        this.deleteAccountErrorMessageHost.innerHTML = "";
    }
}
