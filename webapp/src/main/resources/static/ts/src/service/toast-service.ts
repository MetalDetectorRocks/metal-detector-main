export class ToastService {

    private readonly toastWrapperElement: HTMLDivElement;

    constructor() {
        this.toastWrapperElement = document.getElementById("toast-wrapper") as HTMLDivElement;
    }

    public createToast(text: string) {
        const toastElement = document.createElement("div");
        toastElement.id = "toast";
        toastElement.classList.add("show");
        toastElement.textContent = text;
        setTimeout(() => { toastElement.classList.remove("show"); },3000);

        this.toastWrapperElement.insertAdjacentElement("afterbegin", toastElement);
    }
}
