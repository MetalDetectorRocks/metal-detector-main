export interface SliderComponentProps {
    readonly title: string;
    readonly items: HTMLDivElement[];
}

export class SliderComponent {
    private readonly props: SliderComponentProps;

    private readonly host: HTMLDivElement = document.createElement("div") as HTMLDivElement;
    private readonly sliderHeader: HTMLHeadElement = document.createElement("div") as HTMLHeadElement;
    private readonly prevItem: HTMLSpanElement = document.createElement("span") as HTMLSpanElement;
    private readonly nextItem: HTMLSpanElement = document.createElement("span") as HTMLSpanElement;
    private readonly slider: HTMLDivElement = document.createElement("div") as HTMLDivElement;
    private readonly sliderContent: HTMLDivElement = document.createElement("div") as HTMLDivElement;
    private readonly sliderItems: HTMLDivElement = document.createElement("div") as HTMLDivElement;

    private isMouseDown = false;
    private cursorXSpace = 0;

    private static readonly SLIDING_STEP_IN_PX = 315;

    // ToDo: Navigation arrows should be disabled if there are no items

    constructor(props: SliderComponentProps) {
        this.props = props;
        this.init();
    }

    private init(): void {
        this.createSliderHeader();
        this.createSliderMain();
        this.host.insertAdjacentElement("beforeend", this.sliderHeader);
        this.host.insertAdjacentElement("beforeend", this.slider);
        window.addEventListener("mouseup", () => {
            this.isMouseDown = false;
        });
        window.addEventListener("resize", () => {
            // ToDo: left neu ausrichten
            this.finalize();
        });
    }

    private createSliderHeader(): void {
        this.sliderHeader.classList.add("slider-header");

        const sliderTitle = document.createElement("h5") as HTMLHeadingElement;
        sliderTitle.textContent = this.props.title;
        sliderTitle.classList.add("mb-2");

        this.prevItem.classList.add("material-icons", "md-light", "md-30", "pointer", "unselectable");
        this.prevItem.innerText = "navigate_before";
        this.prevItem.addEventListener("click", this.onClickPrev.bind(this));
        this.nextItem.classList.add("material-icons", "md-light", "md-30", "pointer", "unselectable");
        this.nextItem.innerText = "navigate_next";
        this.nextItem.addEventListener("click", this.onClickNext.bind(this));

        const sliderNavigation = document.createElement("div") as HTMLDivElement;
        sliderNavigation.insertAdjacentElement("beforeend", this.prevItem);
        sliderNavigation.insertAdjacentElement("beforeend", this.nextItem);

        this.sliderHeader.insertAdjacentElement("beforeend", sliderTitle);
        this.sliderHeader.insertAdjacentElement("beforeend", sliderNavigation);
    }

    private createSliderMain(): void {
        this.slider.classList.add("slider");
        this.sliderContent.classList.add("slider-content");
        this.sliderItems.classList.add("slider-items");
        this.sliderItems.style.gridTemplateColumns = `repeat(${this.props.items.length}, 300px)`;

        this.slider.insertAdjacentElement("beforeend", this.sliderContent);
        this.sliderContent.insertAdjacentElement("beforeend", this.sliderItems);

        // ToDo: add dragging functionality for mobile devices
        // this.sliderItems.style.pointerEvents = "none";
        // this.sliderContent.addEventListener("mousedown", (event) => this.onSliderContentMouseDown(event));
        // this.sliderContent.addEventListener("mouseup", this.onSliderContentMouseUp.bind(this));
        // this.sliderContent.addEventListener("mousemove", (event) => this.onSliderContentMouseMove(event));
    }

    private onClickPrev(): void {
        const currentLeft = parseInt(this.sliderItems.style.left);
        this.sliderItems.style.left = currentLeft ? `${currentLeft + SliderComponent.SLIDING_STEP_IN_PX}px` : "0";
        this.boundCards();
    }

    private onClickNext(): void {
        if (!this.nextItem.classList.contains("md-inactive")) {
            const sliderContentRect = this.sliderContent.getBoundingClientRect();
            const itemsRect = this.sliderItems.getBoundingClientRect();
            const currentLeft = parseInt(this.sliderItems.style.left) || 0;
            const stepWidth = Math.min(SliderComponent.SLIDING_STEP_IN_PX, itemsRect.right - sliderContentRect.width);
            this.sliderItems.style.left = `${currentLeft - stepWidth}px`;
            this.boundCards();
            this.finalize();
        }
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

    private debug(): void {
        const sliderContentRect = this.sliderContent.getBoundingClientRect();
        const itemsRect = this.sliderItems.getBoundingClientRect();

        console.log(`sliderContentRectWidth: ${sliderContentRect.width}`); // the width of the visible slider content container
        console.log(`sliderContentRectRight: ${sliderContentRect.right}`);
        console.log(`itemsRectWidth: ${itemsRect.width}`); // the width of all items
        console.log(`itemsRectRight: ${itemsRect.right}`);
        console.log(`sliderItemsLeft: ${this.sliderItems.style.left}`); // can be negative
        console.log(`sliderItemsOffsetLeft: ${this.sliderItems.offsetLeft}`);
    }

    public finalize(): void {
        this.debug();
        const sliderContentRect = this.sliderContent.getBoundingClientRect();
        const itemsRect = this.sliderItems.getBoundingClientRect();
        const sliderItemsLeft = parseInt(this.sliderItems.style.left) || 0;

        // disable 'move next' if all items are visible
        if (sliderContentRect.width + (sliderItemsLeft * -1) >= itemsRect.width) {
            this.nextItem.classList.add("md-inactive");
            this.nextItem.classList.remove("pointer");
        } else {
            this.nextItem.classList.remove("md-inactive");
            this.nextItem.classList.add("pointer");
        }
    }
}
