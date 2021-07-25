export class AlertService {
    public renderInfoAlert(text: string, dismissible: boolean): HTMLDivElement {
        return this.renderAlert("alert-light", text, dismissible);
    }

    public renderSuccessAlert(text: string, dismissible: boolean): HTMLDivElement {
        return this.renderAlert("alert-success", text, dismissible);
    }

    public renderWarningAlert(text: string, dismissible: boolean): HTMLDivElement {
        return this.renderAlert("alert-warning", text, dismissible);
    }

    public renderErrorAlert(text: string, dismissible: boolean): HTMLDivElement {
        return this.renderAlert("alert-danger", text, dismissible);
    }

    private renderAlert(alertType: string, text: string, dismissible: boolean): HTMLDivElement {
        const alertElement = document.createElement("div");
        alertElement.classList.add("alert", alertType);
        alertElement.innerHTML = text;
        alertElement.setAttribute("role", "alert");

        if (dismissible) {
            alertElement.classList.add("alert-dismissible");
            const closeButton = document.createElement("button");
            closeButton.classList.add("btn-close");
            closeButton.setAttribute("data-bs-dismiss", "alert");
            alertElement.insertAdjacentElement("afterbegin", closeButton);
        }

        return alertElement;
    }
}
