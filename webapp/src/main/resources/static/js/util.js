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
 * Builds HTML for the message for an empty result
 */
function createNoResultsMessage(text) {
  const noResultsMessageElement = document.createElement('div');
  noResultsMessageElement.className = "mb-3 alert alert-danger";
  noResultsMessageElement.role = "alert";
  noResultsMessageElement.id = "noResultsMessageElement";
  noResultsMessageElement.innerText = text;

  document.getElementById('noResultsMessageContainer').append(noResultsMessageElement);
}

/**
 * Builds the onclick function
 * @param artistName    Artist to follow
 * @param artistId      Artist's discogs id
 * @param isFollowed    true if user follows given artist
 * @param button        Button that was clicked
 * @returns {Function}
 */
function createOnClickFunctionFollowArtist(artistName, artistId, isFollowed, button) {
  return function () {
    isFollowed ? unfollowArtist(artistId, artistName) : followArtist(artistId, artistName);
  };
}

/**
 * Builds an HTML button to follow an artist
 * @param artistName    The artists name
 * @param artistId      The artists discogs id
 * @param isFollowed    The information if the artist is already followed
 * @returns {HTMLButtonElement}
 */
function createFollowArtistButton(artistName, artistId, isFollowed) {
  const followArtistButtonElement = document.createElement('button');
  followArtistButtonElement.id = "followArtistButton" + artistId;
  followArtistButtonElement.type = "button";
  followArtistButtonElement.className = "btn btn-primary btn-dark font-weight-bold mt-2";
  followArtistButtonElement.textContent = isFollowed ? "Unfollow" : "Follow";
  followArtistButtonElement.onclick = createOnClickFunctionFollowArtist(artistName, artistId, isFollowed, followArtistButtonElement);
  return followArtistButtonElement;
}

/**
 * Tasks when validation or ajax fails
 * @param message  The message to be displayed
 */
function validationOrAjaxFailed(message) {
  createNoResultsMessage(message);
}

/**
 * Creates a pagination placeholder
 * @param element   Element to add placeholder to
 */
function createPlaceholder(element) {
  const listItem = document.createElement("li");
  listItem.className = "page-item";

  const text = document.createElement("p");
  text.className = "page-link";
  text.textContent = "...";

  listItem.append(text);
  element.append(listItem);
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
