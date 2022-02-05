import Swiper, { Navigation } from "swiper";
import "swiper/css";

export interface SwiperComponentProps {
    readonly uniqueCssClassSelector: string;
    readonly title: string;
    readonly items: HTMLDivElement[];
    readonly host: HTMLDivElement;
}

// TODO DanielW: Remove this component and use NewSwiperComponent instead
export class LegacySwiperComponent {
    private readonly props: SwiperComponentProps;

    private readonly swiperHeader = document.createElement("div") as HTMLHeadElement;
    private readonly swiperNavigation = document.createElement("div") as HTMLHeadElement;
    private readonly prevItem = document.createElement("span") as HTMLSpanElement;
    private readonly nextItem = document.createElement("span") as HTMLSpanElement;
    private readonly swiperDiv = document.createElement("div") as HTMLDivElement;
    private readonly swiperWrapper = document.createElement("div") as HTMLDivElement;

    constructor(props: SwiperComponentProps) {
        this.props = props;
        this.attach();
        Swiper.use([Navigation]);
        new Swiper(`.${this.props.uniqueCssClassSelector}`, {
            spaceBetween: 15,
            loop: this.props.items.length >= 4,
            loopFillGroupWithBlank: true,
            grabCursor: true,
            navigation: {
                nextEl: `.${this.props.uniqueCssClassSelector}-next`,
                prevEl: `.${this.props.uniqueCssClassSelector}-prev`,
            },
            breakpoints: {
                1280: {
                    slidesPerView: 4,
                },
                925: {
                    slidesPerView: 3,
                },
                600: {
                    slidesPerView: 2,
                },
            },
        });
    }

    private attach(): void {
        this.createSwiperHeader();
        this.createSwiperMain();
        const wrapper = document.createElement("div") as HTMLDivElement;
        wrapper.insertAdjacentElement("beforeend", this.swiperHeader);
        wrapper.insertAdjacentElement("beforeend", this.swiperDiv);
        this.props.host.insertAdjacentElement("beforeend", wrapper);
    }

    private createSwiperHeader(): void {
        this.swiperHeader.classList.add("swiper-header", "mt-3");

        const swiperTitle = document.createElement("h5") as HTMLHeadingElement;
        swiperTitle.textContent = this.props.title;
        swiperTitle.classList.add("mb-2");

        this.prevItem.classList.add(
            "material-icons",
            "md-light",
            "md-30",
            "pointer",
            "unselectable",
            "swiper-button-prev",
            `${this.props.uniqueCssClassSelector}-prev`,
        );
        this.prevItem.innerText = "navigate_before";
        this.nextItem.classList.add(
            "material-icons",
            "md-light",
            "md-30",
            "pointer",
            "unselectable",
            "swiper-button-next",
            `${this.props.uniqueCssClassSelector}-next`,
        );
        this.nextItem.innerText = "navigate_next";

        this.swiperNavigation.insertAdjacentElement("beforeend", this.prevItem);
        this.swiperNavigation.insertAdjacentElement("beforeend", this.nextItem);
        this.swiperHeader.insertAdjacentElement("beforeend", swiperTitle);
        this.swiperHeader.insertAdjacentElement("beforeend", this.swiperNavigation);
    }

    private createSwiperMain(): void {
        this.swiperDiv.classList.add("swiper", this.props.uniqueCssClassSelector);
        this.swiperWrapper.classList.add("swiper-wrapper");
        this.props.items.forEach((item) => {
            this.swiperWrapper.insertAdjacentElement("beforeend", item);
        });
        this.swiperDiv.insertAdjacentElement("beforeend", this.swiperWrapper);
    }
}
