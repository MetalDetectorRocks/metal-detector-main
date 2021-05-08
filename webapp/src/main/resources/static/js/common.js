$(document).ready(function () {
    registerLogoutListener();
    registerSearchIconClickListener();
    setupScrollToTop();
});

function registerLogoutListener() {
    Array.from(document.getElementsByClassName("logout-link")).forEach(element => {
        element.addEventListener("click", (event) => {
            event.preventDefault();
            document.getElementById("logout-form").submit();
        });
    });
}

function registerSearchIconClickListener() {
    const searchIcon = document.getElementById("show-search");
    searchIcon.addEventListener("click", () => document.getElementById("mobile-query").focus());
}

function setupScrollToTop() {
    window.onscroll = () => showScrollToTopButton();
    const backToTopButton = document.getElementById("back-to-top-button");
    if (backToTopButton) {
        backToTopButton.onclick = () => scrollToTop();
    }
}

function showScrollToTopButton() {
    const backToTopButton = $('#back-to-top-button');
    if (document.body.scrollTop > 200 || document.documentElement.scrollTop > 200) {
        backToTopButton.fadeIn();
    } else {
        backToTopButton.fadeOut();
    }
}

/**
 * Animates scrolling back to the top
 */
function scrollToTop() {
    $('#back-to-top-button').click(function () {
        $('body,html').animate({
            scrollTop: 0
        }, 800);
        return false;
    });
}

/**
 * Creates a toast that is displayed for a short time.
 * @param text  The toast text
 */
function createToast(text) {
    const toast = $("<div>");
    toast.attr("id", "toast");
    toast.addClass("show success");
    toast.text(text);
    setTimeout(function() {
            toast.removeClass("show");
        },
        2900
    );
    $("#toast-wrapper").append(toast);
}

/**
 * Checks if the provided value is empty.
 * @param value The value to check
 * @returns {boolean} true if the value is empty, false otherwise
 */
function isEmpty(value) {
    return typeof value === "undefined" || value === null || value.length === 0;
}

/**
 * Formats a UTC DateTime according to the browser locale
 * @param dateTimeInput the UTC DateTime to format
 * @return {string} the formatted DateTime
 */
function formatUtcDateTime(dateTimeInput) {
    if (dateTimeInput) {
        const formattedDate = formatUtcDate(dateTimeInput);
        const date = new Date(Date.parse(dateTimeInput));
        const timeFormat = new Intl.DateTimeFormat('de', { hour: "2-digit", minute: "2-digit", second: "2-digit" });
        const [{ value: hour },,{ value: minute },,{ value: second }] = timeFormat.formatToParts(date);

        return `${formattedDate} ${hour}:${minute}:${second}`;
    }

    return "";
}

/**
 * Formats a UTC Date according to the browser locale
 * @param dateInput the UTC DateTime to format
 * @return {string} the formatted Date
 */
function formatUtcDate(dateInput) {
    if (dateInput) {
        const date = new Date(Date.parse(dateInput));
        const dateFormat = new Intl.DateTimeFormat('de', { year: "numeric", month: "2-digit", day: "2-digit" });
        const [{ value: day },,{ value: month },,{ value: year }] = dateFormat.formatToParts(date);

        return `${year}-${month}-${day}`;
    }

    return "";
}

/**
 * Resets the validation area for the given form.
 * @param validationAreaId  ID of the area to reset
 */
function resetValidationArea(validationAreaId) {
    const validationMessageArea = $(validationAreaId);
    validationMessageArea.removeClass('alert alert-danger alert-success');
    validationMessageArea.empty();
}
