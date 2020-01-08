function registerLogoutListener() {
    document.getElementById('logout-link').addEventListener('click', function(event) {
        event.preventDefault();
        document.getElementById('logout-form').submit();
    });
}

/**
 * Toggles the loading indicator. Has to be called twice (on/off)
 * @param id    ID to show loader at
 */
function toggleLoader(id) {
    document.getElementById(id).classList.toggle("loader");
}

/**
 * Set the onscroll function
 */
$(document).ready(function () {
    window.onscroll = function() {onScrollFunction()};
});

/**
 * Function called on scrolling
 */
function onScrollFunction() {
    const backToTopButton = $("#backToTopButton");
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
    $('#backToTopButton').click(function () {
        $('body,html').animate({
            scrollTop: 0
        }, 800);
        return false;
    });
}