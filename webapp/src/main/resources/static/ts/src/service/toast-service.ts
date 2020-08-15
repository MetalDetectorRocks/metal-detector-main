export class ToastService {

    private readonly toastWrapperElement: HTMLDivElement;

    constructor() {
        this.toastWrapperElement = document.getElementById("toast-wrapper") as HTMLDivElement;
    }

    public createInfoToast(text: string) {
        const infoToast = this.buildToastElement(text);
        infoToast.classList.add("info");
        this.toastWrapperElement.insertAdjacentElement("afterbegin", infoToast);
    }

    public createErrorToast(text: string) {
        const errorToast = this.buildToastElement(text);
        errorToast.classList.add("error");
        this.toastWrapperElement.insertAdjacentElement("afterbegin", errorToast);
    }

    private buildToastElement(text: string): HTMLDivElement {
        const toastElement = document.createElement("div");
        toastElement.id = "toast";
        toastElement.classList.add("show");
        toastElement.textContent = text;
        setTimeout(() => { toastElement.classList.remove("show"); },3000);

        return toastElement;
    }
}
