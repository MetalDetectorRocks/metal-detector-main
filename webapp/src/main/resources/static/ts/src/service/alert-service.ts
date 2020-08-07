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

    public renderErorAlert(text: string, dismissible: boolean): HTMLDivElement {
        return this.renderAlert("alert-danger", text, dismissible);
    }

    private renderAlert(alertType: string, text: string, dismissible: boolean): HTMLDivElement {
        const alertElement = document.createElement("div");
        alertElement.classList.add("alert", alertType);
        alertElement.innerHTML = text;
        alertElement.setAttribute("role", "alert");

        if (dismissible) {
            const closeButton = document.createElement("button");
            closeButton.classList.add("close");
            closeButton.setAttribute("data-dismiss", "alert");

            closeButton.innerHTML = "&times;";
            alertElement.insertAdjacentElement("afterbegin", closeButton);
        }

        return alertElement;
    }
}
