import Swiper, { Navigation } from "swiper";
import "swiper/css";

export interface SwiperComponentProps {
    readonly uniqueCssClassSelector: string;
    readonly items: HTMLDivElement[];
    readonly host: HTMLDivElement;
}

export class SwiperComponent {
    private readonly props: SwiperComponentProps;

    constructor(props: SwiperComponentProps) {
        this.props = props;
        this.attach();
        Swiper.use([Navigation]);
        new Swiper(`.${this.props.uniqueCssClassSelector}`, {
            slidesPerView: 1,
            slidesPerGroup: 1,
            spaceBetween: 15,
            loopFillGroupWithBlank: true,
            grabCursor: true,
            navigation: {
                nextEl: `.${this.props.uniqueCssClassSelector}-next`,
                prevEl: `.${this.props.uniqueCssClassSelector}-prev`,
            },
            breakpoints: {
                1280: {
                    slidesPerView: Math.min(4, this.props.items.length),
                },
                925: {
                    slidesPerView: Math.min(3, this.props.items.length),
                },
                600: {
                    slidesPerView: Math.min(2, this.props.items.length),
                },
            },
        });
    }

    private attach(): void {
        this.props.host.insertAdjacentHTML("beforeend", this.createPrevNavigation());
        this.props.host.insertAdjacentElement("beforeend", this.createSwiperDiv());
        this.props.host.insertAdjacentHTML("beforeend", this.createNextNavigation());
    }

    private createSwiperDiv(): HTMLElement {
        const swiperDiv = document.createElement("div") as HTMLDivElement;
        swiperDiv.classList.add("swiper", this.props.uniqueCssClassSelector);

        const swiperWrapper = document.createElement("div") as HTMLDivElement;
        swiperWrapper.classList.add("swiper-wrapper");
        this.props.items.forEach((item) => {
            swiperWrapper.insertAdjacentElement("beforeend", item);
        });
        swiperDiv.insertAdjacentElement("beforeend", swiperWrapper);

        return swiperDiv;
    }

    private createPrevNavigation(): string {
        return `
            <div class="swiper-navigation">
                <i class="material-icons md-30 md-light pointer ${this.props.uniqueCssClassSelector}-prev">arrow_back_ios</i>
            </div>
        `.trim();
    }

    private createNextNavigation(): string {
        return `
            <div class="swiper-navigation">
                <i class="material-icons md-30 md-light pointer ${this.props.uniqueCssClassSelector}-next">arrow_forward_ios</i>
            </div>
        `.trim();
    }
}
