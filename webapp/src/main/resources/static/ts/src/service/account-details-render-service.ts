import { AccountDetailsRestClient } from "../clients/account-details-rest-client";
import { ToastService } from "./toast-service";
import { AxiosError } from "axios";

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
                const inputElement = document.getElementById("email-address")! as HTMLInputElement;
                inputElement.value = response.email;
            }
        });

        const updateEmailAddressButton = document.getElementById("update-email-address") as HTMLButtonElement;
        const updatePasswordButton = document.getElementById("update-password") as HTMLButtonElement;
        updateEmailAddressButton.addEventListener("click", this.onUpdateEmailAddressClicked.bind(this));
        updatePasswordButton.addEventListener("click", this.onUpdatePasswordClicked.bind(this));
    }

    private onUpdateEmailAddressClicked(): void {
        const inputElement = document.getElementById("email-address")! as HTMLInputElement;
        this.accountDetailsRestClient
            .updateEmailAddress(inputElement.value)
            .then((response) => (inputElement.value = response))
            .then(() => this.toastService.createInfoToast("Successfully updated email address!"));
    }

    private onUpdatePasswordClicked(): void {
        const oldPassword = document.getElementById("old-password")! as HTMLInputElement;
        const newPassword = document.getElementById("new-password")! as HTMLInputElement;
        const confirmPassword = document.getElementById("confirm-password")! as HTMLInputElement;
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
        const passwordMatchMessage = response.messages.find((value) => value.includes("The Passwords must match"));
        if (passwordMatchMessage != null) {
            const listItem = document.createElement("li") as HTMLLIElement;
            listItem.textContent = "The Passwords must match.";
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
