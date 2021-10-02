export interface SliderComponentProps {
    readonly title: string;
    readonly items: HTMLDivElement[];
}

export enum SLIDE_DIRECTION {
    NEXT,
    PREV,
}

export class SliderComponent {
    private readonly props: SliderComponentProps;

    private readonly host: HTMLDivElement = document.createElement("div") as HTMLDivElement;
    private readonly sliderHeader: HTMLHeadElement = document.createElement("div") as HTMLHeadElement;
    private readonly sliderNavigation: HTMLHeadElement = document.createElement("div") as HTMLHeadElement;
    private readonly prevItem: HTMLSpanElement = document.createElement("span") as HTMLSpanElement;
    private readonly nextItem: HTMLSpanElement = document.createElement("span") as HTMLSpanElement;
    private readonly slider: HTMLDivElement = document.createElement("div") as HTMLDivElement;
    private readonly sliderContent: HTMLDivElement = document.createElement("div") as HTMLDivElement;
    private readonly sliderItems: HTMLDivElement = document.createElement("div") as HTMLDivElement;

    private isMobile = false;
    private isFullDesktop = false;
    private isMouseDown = false;
    private cursorXSpace = 0;

    private static readonly MAX_MOBILE_WIDTH_IN_PX = 700;
    private static readonly MIN_FULL_DESKTOP_WIDTH_IN_PX = 1280;
    private static readonly SLIDING_STEP_IN_PX = 315;

    constructor(props: SliderComponentProps) {
        this.props = props;
        this.init();
    }

    private init(): void {
        this.createSliderHeader();
        this.createSliderMain();
        this.host.insertAdjacentElement("beforeend", this.sliderHeader);
        this.host.insertAdjacentElement("beforeend", this.slider);
        this.checkWindowWidth();

        window.addEventListener("mouseup", () => {
            this.isMouseDown = false;
        });
        window.addEventListener("resize", () => {
            // ToDo: left neu ausrichten (Bsp: auf kleinem Monitor bis ans Ende scrollen und dann Fenster maximieren)
            this.checkWindowWidth();
            this.toggleSlidingBehavior();
        });
    }

    private checkWindowWidth(): void {
        this.isMobile = window.innerWidth < SliderComponent.MAX_MOBILE_WIDTH_IN_PX;
        this.isFullDesktop = window.innerWidth >= SliderComponent.MIN_FULL_DESKTOP_WIDTH_IN_PX;
    }

    private toggleSlidingBehavior(): void {
        if (this.isMobile) {
            this.sliderContent.addEventListener("mousedown", (event) => this.onSliderContentMouseDown(event));
            this.sliderContent.addEventListener("mouseup", this.onSliderContentMouseUp.bind(this));
            this.sliderContent.addEventListener("mousemove", (event) => this.onSliderContentMouseMove(event));
            this.sliderNavigation.classList.add("d-none");
        } else {
            this.sliderNavigation.classList.remove("d-none");
            this.finalize();
        }
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

        this.sliderNavigation.insertAdjacentElement("beforeend", this.prevItem);
        this.sliderNavigation.insertAdjacentElement("beforeend", this.nextItem);
        if (window.innerWidth < SliderComponent.MAX_MOBILE_WIDTH_IN_PX) {
            this.sliderNavigation.classList.add("d-none");
        }

        this.sliderHeader.insertAdjacentElement("beforeend", sliderTitle);
        this.sliderHeader.insertAdjacentElement("beforeend", this.sliderNavigation);
    }

    private createSliderMain(): void {
        this.slider.classList.add("slider");
        this.sliderContent.classList.add("slider-content", "grabbable");
        this.sliderItems.classList.add("slider-items");
        this.sliderItems.style.gridTemplateColumns = `repeat(${this.props.items.length}, 300px)`;

        this.slider.insertAdjacentElement("beforeend", this.sliderContent);
        this.sliderContent.insertAdjacentElement("beforeend", this.sliderItems);
    }

    private onClickPrev(): void {
        if (!this.prevItem.classList.contains("md-inactive")) {
            this.move(SLIDE_DIRECTION.PREV);
        }
    }

    private onClickNext(): void {
        if (!this.nextItem.classList.contains("md-inactive")) {
            this.move(SLIDE_DIRECTION.NEXT);
        }
    }

    private move(direction: SLIDE_DIRECTION): void {
        const sliderContentRect = this.sliderContent.getBoundingClientRect();
        const itemsRect = this.sliderItems.getBoundingClientRect();
        const currentLeft = parseInt(this.sliderItems.style.left) || 0;

        // ToDo: Feinjustierung wenn window.width > 700 und < 1280
        if (direction === SLIDE_DIRECTION.NEXT) {
            const stepWidth = this.isFullDesktop
                ? SliderComponent.SLIDING_STEP_IN_PX
                : Math.min(SliderComponent.SLIDING_STEP_IN_PX, itemsRect.right - sliderContentRect.width);
            this.sliderItems.style.left = `${currentLeft - stepWidth}px`;
        } else if (direction === SLIDE_DIRECTION.PREV) {
            const stepWidth = this.isFullDesktop
                ? SliderComponent.SLIDING_STEP_IN_PX
                : Math.min(SliderComponent.SLIDING_STEP_IN_PX, itemsRect.right - sliderContentRect.width);
            this.sliderItems.style.left = `${currentLeft + stepWidth}px`;
        }

        this.finalize();
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

    public finalize(): void {
        const sliderContentRect = this.sliderContent.getBoundingClientRect();
        const itemsRect = this.sliderItems.getBoundingClientRect();
        const sliderItemsLeft = parseInt(this.sliderItems.style.left) || 0;

        // disable 'move next' if all items are visible
        if (sliderContentRect.width + sliderItemsLeft * -1 >= itemsRect.width) {
            this.nextItem.classList.add("md-inactive");
            this.nextItem.classList.remove("pointer");
        } else {
            this.nextItem.classList.remove("md-inactive");
            this.nextItem.classList.add("pointer");
        }

        // disable 'move prev' if all items are visible or there are no items in overflow section
        if (itemsRect.width <= sliderContentRect.width || sliderItemsLeft === 0) {
            this.prevItem.classList.add("md-inactive");
            this.prevItem.classList.remove("pointer");
        } else {
            this.prevItem.classList.remove("md-inactive");
            this.prevItem.classList.add("pointer");
        }
    }

    /************************** MOBILE SPECIFIC METHODS ******************************/

    private onSliderContentMouseDown(event: MouseEvent): void {
        if (this.isMobile) {
            this.isMouseDown = true;
            this.cursorXSpace = event.offsetX - this.sliderItems.offsetLeft;
            this.sliderContent.classList.add("grabbing");
            this.sliderContent.classList.remove("grabbable");
        }
    }

    private onSliderContentMouseUp(): void {
        if (this.isMobile) {
            this.sliderContent.classList.add("grabbable");
            this.sliderContent.classList.remove("grabbing");
        }
    }

    private onSliderContentMouseMove(event: MouseEvent): void {
        if (!this.isMouseDown || !this.isMobile) {
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
}
