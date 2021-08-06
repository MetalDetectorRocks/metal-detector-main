// equivalent of jQuerys '$(document).ready()' (doesn't work in older IEs)
document.addEventListener('DOMContentLoaded', function(){
  registerLogoutListener();
  registerSearchIconClickListener();
  setupScrollToTop();
}, false);

function registerLogoutListener() {
  Array.from(document.getElementsByClassName("logout-link")).forEach(
    (element) => {
      element.addEventListener("click", (event) => {
        event.preventDefault();
        document.getElementById("logout-form").submit();
      });
    }
  );
}

function registerSearchIconClickListener() {
  const searchIcon = document.getElementById("show-search");
  if (searchIcon) {
    searchIcon.addEventListener("click", () =>
      document.getElementById("mobile-query").focus()
    );
  }
}

function setupScrollToTop() {
  const scrollTopButton = document.querySelector(".scroll-top");
  if (scrollTopButton) {
    showScrollToTopButton(scrollTopButton);
    scrollTopButton.addEventListener("click", () => {
      window.scrollTo(0, 0);
    });
  }
  window.onscroll = () => showScrollToTopButton(scrollTopButton);
}

function showScrollToTopButton(scrollTopButton) {
  if (
    document.body.scrollTop > 200 ||
    document.documentElement.scrollTop > 200
  ) {
    scrollTopButton.style.display = "flex";
  } else {
    scrollTopButton.style.display = "none";
  }
}

/**
 * Creates a toast that is displayed for a short time.
 * @param text The toast text
 */
function createToast(text) {
  const toast = document.createElement("div");
  toast.id = "toast";
  toast.textContent = text;
  toast.classList.add("show", "success");
  setTimeout(function () {
    toast.classList.remove("show");
  }, 2900);
  document.getElementById("toast-wrapper").append(toast);
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
    const timeFormat = new Intl.DateTimeFormat("de", {
      hour: "2-digit",
      minute: "2-digit",
      second: "2-digit",
    });
    const [
      { value: hour },
      ,
      { value: minute },
      ,
      { value: second },
    ] = timeFormat.formatToParts(date);

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
    const dateFormat = new Intl.DateTimeFormat("de", {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
    });
    const [
      { value: day },
      ,
      { value: month },
      ,
      { value: year },
    ] = dateFormat.formatToParts(date);

    return `${year}-${month}-${day}`;
  }

  return "";
}

/**
 * Resets the validation area for the given form.
 * @param validationAreaId  ID of the area to reset
 */
function resetValidationArea(validationAreaId) {
  if (validationAreaId.startsWith("#")) {
    validationAreaId = validationAreaId.substring(1);
  }
  const validationMessageArea = document.getElementById(validationAreaId);
  validationMessageArea.classList.remove("alert", "alert-danger", "alert-success");
  validationMessageArea.textContent = "";
}
