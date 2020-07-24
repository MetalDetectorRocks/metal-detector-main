/**
 * Toggles the loading indicator. Has to be called twice (on/off)
 * @param id    ID to show loader at
 */
function toggleLoader(id) {
    $(`#${id}`).toggleClass("loader");
}

/**
 * Checks if the provided value is empty.
 * @param value The value to check
 * @returns {boolean} true if the value is empty, false otherwise
 */
function isEmpty(value) {
  return typeof value === "undefined" || value.length === 0;
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
    const [{ value: month },,{ value: day },,{ value: year }] = dateFormat.formatToParts(date);

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
