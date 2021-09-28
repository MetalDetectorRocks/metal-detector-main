export interface SliderComponentProps {
    readonly title: string;
    readonly items: HTMLDivElement[];
}

export class SliderComponent {
    private readonly props: SliderComponentProps;

    private readonly host: HTMLDivElement = document.createElement("div") as HTMLDivElement;
    private readonly slider: HTMLDivElement = document.createElement("div") as HTMLDivElement;
    private readonly sliderContent: HTMLDivElement = document.createElement("div") as HTMLDivElement;
    private readonly sliderItems: HTMLDivElement = document.createElement("div") as HTMLDivElement;
    private readonly sliderTitle: HTMLHeadElement = document.createElement("h5") as HTMLHeadElement;

    private isMouseDown = false;
    private cursorXSpace = 0;

    // ToDo: add navigation to top right

    constructor(props: SliderComponentProps) {
        this.props = props;
        this.init();
        window.addEventListener("mouseup", () => {
            this.isMouseDown = false;
        });
    }

    private init(): void {
        this.host.insertAdjacentElement("beforeend", this.sliderTitle);
        this.host.insertAdjacentElement("beforeend", this.slider);

        this.sliderTitle.textContent = this.props.title;
        this.sliderTitle.classList.add("mb-2");

        this.slider.classList.add("slider");
        this.slider.insertAdjacentElement("beforeend", this.sliderContent);

        this.sliderContent.classList.add("slider-content");
        this.sliderContent.insertAdjacentElement("beforeend", this.sliderItems);

        this.sliderItems.classList.add("slider-items");
        this.sliderItems.style.gridTemplateColumns = `repeat(${this.props.items.length}, 300px)`;

        // ToDo: add dragging functionality for mobile devices
        // this.sliderItems.style.pointerEvents = "none";
        // this.sliderContent.addEventListener("mousedown", (event) => this.onSliderContentMouseDown(event));
        // this.sliderContent.addEventListener("mouseup", this.onSliderContentMouseUp.bind(this));
        // this.sliderContent.addEventListener("mousemove", (event) => this.onSliderContentMouseMove(event));
    }

    private onSliderContentMouseDown(event: MouseEvent): void {
        this.isMouseDown = true;
        this.cursorXSpace = event.offsetX - this.sliderItems.offsetLeft;
        this.sliderContent.classList.add("grabbing");
        this.sliderContent.classList.remove("grabbable");
    }

    private onSliderContentMouseUp(): void {
        this.sliderContent.classList.add("grabbable");
        this.sliderContent.classList.remove("grabbing");
    }

    private onSliderContentMouseMove(event: MouseEvent): void {
        if (!this.isMouseDown) {
            return;
        }
        event.preventDefault();
        this.sliderItems.style.left = `${event.offsetX - this.cursorXSpace}px`;
        this.boundCards();
    }

    private boundCards(): void {
        const sliderContentRect = this.sliderContent.getBoundingClientRect();
        const itemsRect = this.sliderItems.getBoundingClientRect();

        if (itemsRect.width <= sliderContentRect.width) {
            // prevent sliding at all if all items are visible within the content container
            this.sliderItems.style.left = "0";
        } else if (parseInt(this.sliderItems.style.left) > 0) {
            // prevent sliding to the right for the first item
            this.sliderItems.style.left = "0";
        } else if (itemsRect.right < sliderContentRect.right) {
            // prevent sliding to the left for the last item
            this.sliderItems.style.left = `-${itemsRect.width - sliderContentRect.width}px`;
        }
    }

    public render(): HTMLDivElement | null {
        if (this.props.items.length > 0) {
            this.props.items.forEach((item) => {
                this.sliderItems.insertAdjacentElement("beforeend", item);
            });

            return this.host;
        }
        return null;
    }
}
