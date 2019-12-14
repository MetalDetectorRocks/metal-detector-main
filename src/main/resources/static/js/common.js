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
