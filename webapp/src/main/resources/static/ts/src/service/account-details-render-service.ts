import { AccountDetailsRestClient } from "../clients/account-details-rest-client";
import { AlertService } from "./alert-service";
import { UNKNOWN_ERROR_MESSAGE } from "../config/messages.config";
import { ToastService } from "./toast-service";
import { AxiosError } from "axios";

export class AccountDetailsRenderService {
    private readonly toastService: ToastService;
    private readonly alertService: AlertService;
    private readonly accountDetailsRestClient: AccountDetailsRestClient;
    private readonly emailInput: HTMLInputElement;
    private readonly updateEmailButton: HTMLButtonElement;
    private readonly updateEmailErrorMessageHost: HTMLDivElement;
    private readonly deleteAccountButton: HTMLButtonElement;
    private readonly deleteAccountErrorMessageHost: HTMLDivElement;

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
    }

    public init(): void {
        this.accountDetailsRestClient
            .getAccountDetails()
            .then((response) => (this.emailInput.value = response.email))
            .catch(() => this.renderServerError(this.updateEmailErrorMessageHost, UNKNOWN_ERROR_MESSAGE));

        this.updateEmailButton.addEventListener("click", this.onUpdateEmailClicked.bind(this));
        this.emailInput.addEventListener("keypress", (event) => this.onEnterPressedInEmailInput(event));
        this.deleteAccountButton.addEventListener("click", this.onDeleteAccountClicked.bind(this));

        const updatePasswordButton = document.getElementById("update-password") as HTMLButtonElement;
        updatePasswordButton.addEventListener("click", this.onUpdatePasswordClicked.bind(this));
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

    private onUpdatePasswordClicked(): void {
        const oldPassword = document.getElementById("old-password") as HTMLInputElement;
        const newPassword = document.getElementById("new-password") as HTMLInputElement;
        const confirmPassword = document.getElementById("confirm-password") as HTMLInputElement;
        this.accountDetailsRestClient
            .updatePassword(oldPassword.value, newPassword.value, confirmPassword.value)
            .then(() => this.renderSuccess())
            .catch((error: AxiosError) => {
                this.renderPasswordUpdateErrors(error);
            });
    }

    private renderSuccess(): void {
        const errorMessageAlert = document.getElementById("alert-message") as HTMLDivElement;
        errorMessageAlert.classList.remove("alert-danger");
        errorMessageAlert.classList.add("alert", "alert-success", "alert-dismissible", "dismissible-button");
        errorMessageAlert.setAttribute("role", "alert");

        errorMessageAlert.innerHTML = "";

        const closeButton = document.createElement("button") as HTMLButtonElement;
        closeButton.type = "button";
        closeButton.classList.add("close");
        closeButton.innerHTML = "&times;";
        closeButton.setAttribute("data-dismiss", "alert");

        const messagesSpan = document.createElement("span") as HTMLSpanElement;
        messagesSpan.textContent = "Successfully updated password!";

        errorMessageAlert.appendChild(closeButton);
        errorMessageAlert.appendChild(messagesSpan);
    }

    private renderPasswordUpdateErrors(error: AxiosError): void {
        interface ErrorResponse {
            messages: string[];
        }

        const response: ErrorResponse = error.response?.data;
        const errorMessageAlert = document.getElementById("alert-message") as HTMLDivElement;
        errorMessageAlert.classList.remove("alert-success");
        errorMessageAlert.classList.add("alert", "alert-danger", "alert-dismissible", "dismissible-button");
        errorMessageAlert.setAttribute("role", "alert");

        errorMessageAlert.innerHTML = "";

        const closeButton = document.createElement("button") as HTMLButtonElement;
        closeButton.type = "button";
        closeButton.classList.add("close");
        closeButton.innerHTML = "&times;";
        closeButton.setAttribute("data-dismiss", "alert");

        const messagesSpan = document.createElement("span") as HTMLSpanElement;
        const messages = document.createElement("ul") as HTMLUListElement;

        const passwordLengthMessage = response.messages.find((value) =>
            value.includes("Password length must be at least 8 characters"),
        );
        if (passwordLengthMessage != null) {
            const listItem = document.createElement("li") as HTMLLIElement;
            listItem.textContent = "Password length must be at least 8 characters.";
            messages.appendChild(listItem);
        }
        const passwordMatchMessage = response.messages.find((value) => value.includes("The passwords must match"));
        if (passwordMatchMessage != null) {
            const listItem = document.createElement("li") as HTMLLIElement;
            listItem.textContent = "The passwords must match.";
            messages.appendChild(listItem);
        }
        const oldPasswordMathMessage = response.messages.find((value) => value.includes("Old password does not match"));
        if (oldPasswordMathMessage != null) {
            const listItem = document.createElement("li") as HTMLLIElement;
            listItem.textContent = "Old password does not match.";
            messages.appendChild(listItem);
        }

        messagesSpan.innerHTML = messages.innerHTML;
        errorMessageAlert.appendChild(closeButton);
        errorMessageAlert.appendChild(messagesSpan);
    }
}
