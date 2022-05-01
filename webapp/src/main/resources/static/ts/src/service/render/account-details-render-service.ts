import { AccountDetailsRestClient } from "../../clients/account-details-rest-client";
import { AlertService } from "../util/alert-service";
import { ToastService } from "../util/toast-service";
import { UNKNOWN_ERROR_MESSAGE } from "../../config/messages.config";
import { UserResponse } from "../../model/user-response.model";

export class AccountDetailsRenderService {
    private readonly toastService: ToastService;
    private readonly alertService: AlertService;
    private readonly accountDetailsRestClient: AccountDetailsRestClient;
    private readonly emailInput: HTMLInputElement;
    private readonly updateEmailButton: HTMLButtonElement;
    private readonly updateEmailErrorMessageHost: HTMLDivElement;
    private readonly updatePasswordButton: HTMLButtonElement;
    private readonly deleteAccountButton: HTMLButtonElement;
    private readonly deleteAccountErrorMessageHost: HTMLDivElement;
    private readonly oldPasswordInput: HTMLInputElement;
    private readonly newPasswordInput: HTMLInputElement;
    private readonly confirmPasswordInput: HTMLInputElement;
    private readonly updatePasswordErrorMessageHost: HTMLDivElement;

    constructor(
        alertService: AlertService,
        toastService: ToastService,
        accountDetailsRestClient: AccountDetailsRestClient,
    ) {
        this.alertService = alertService;
        this.toastService = toastService;
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
        this.updatePasswordButton = document.getElementById("update-password") as HTMLButtonElement;
        this.oldPasswordInput = document.getElementById("old-password") as HTMLInputElement;
        this.newPasswordInput = document.getElementById("new-password") as HTMLInputElement;
        this.confirmPasswordInput = document.getElementById("confirm-password") as HTMLInputElement;
        this.updatePasswordErrorMessageHost = document.getElementById(
            "update-password-error-message-wrapper",
        ) as HTMLDivElement;
    }

    public init(): void {
        this.accountDetailsRestClient
            .getAccountDetails()
            .then((response) => this.initUpdatableFields(response))
            .catch(() => this.renderServerError(this.updateEmailErrorMessageHost, UNKNOWN_ERROR_MESSAGE));

        this.updateEmailButton.addEventListener("click", this.onUpdateEmailClicked.bind(this));
        this.emailInput.addEventListener("keypress", (event) => this.onEnterPressedInEmailInput(event));
        this.deleteAccountButton.addEventListener("click", this.onDeleteAccountClicked.bind(this));
        this.updatePasswordButton.addEventListener("click", this.onUpdatePasswordClicked.bind(this));
        this.oldPasswordInput.addEventListener("keypress", (event) => this.onEnterPressedInPasswordInput(event));
        this.newPasswordInput.addEventListener("keypress", (event) => this.onEnterPressedInPasswordInput(event));
        this.confirmPasswordInput.addEventListener("keypress", (event) => this.onEnterPressedInPasswordInput(event));
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
                this.renderServerError(this.updateEmailErrorMessageHost, error.message);
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
        this.updatePasswordErrorMessageHost.innerHTML = "";
    }

    private onUpdatePasswordClicked(): void {
        this.updatePassword();
    }

    private onEnterPressedInPasswordInput(event: KeyboardEvent): void {
        if (event.key === "Enter") {
            this.updatePassword();
        }
    }

    private updatePassword(): void {
        this.clearErrorMessageHosts();
        const oldPassword = document.getElementById("old-password") as HTMLInputElement;
        const newPassword = document.getElementById("new-password") as HTMLInputElement;
        const confirmPassword = document.getElementById("confirm-password") as HTMLInputElement;
        this.accountDetailsRestClient
            .updatePassword(oldPassword.value, newPassword.value, confirmPassword.value)
            .then(() => {
                this.oldPasswordInput.classList.add("is-valid");
                this.oldPasswordInput.classList.remove("is-invalid");
                this.newPasswordInput.classList.add("is-valid");
                this.newPasswordInput.classList.remove("is-invalid");
                this.confirmPasswordInput.classList.add("is-valid");
                this.confirmPasswordInput.classList.remove("is-invalid");
            })
            .catch((error) => {
                this.oldPasswordInput.classList.add("is-invalid");
                this.oldPasswordInput.classList.remove("is-valid");
                this.newPasswordInput.classList.add("is-invalid");
                this.newPasswordInput.classList.remove("is-valid");
                this.confirmPasswordInput.classList.add("is-invalid");
                this.confirmPasswordInput.classList.remove("is-valid");
                this.renderServerError(this.updatePasswordErrorMessageHost, error.message);
            });
    }

    private initUpdatableFields(response: UserResponse): void {
        if (response.nativeUser) {
            this.emailInput.value = response.email;
        } else {
            const updateEmailArea = document.getElementById("update-email-address-area") as HTMLDivElement;
            const updatePasswordArea = document.getElementById("update-password-area") as HTMLDivElement;
            updateEmailArea.style.display = "none";
            updatePasswordArea.style.display = "none";
        }
    }
}
