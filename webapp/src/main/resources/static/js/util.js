/**
 * Toggles the loading indicator. Has to be called twice (on/off)
 * @param id    ID to show loader at
 */
function toggleLoader(id) {
  document.getElementById(id).classList.toggle("loader");
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
    if (isFollowed)
      unfollowArtist(artistName,artistId,button);
    else
      followArtist(artistName,artistId,button);
  };
}

/**
 * Builds an HTML button to follow an artist
 * @param artistName    The artists name
 * @param artistId      The artists discogs id
 * @param isFollowed    The information if the artist is already followed
 * @returns {HTMLButtonElement}
 */
function createFollowArtistButton(artistName,artistId,isFollowed) {
  const followArtistButtonElement = document.createElement('button');
  followArtistButtonElement.id = "followArtistButton"+artistId;
  followArtistButtonElement.type = "button";
  followArtistButtonElement.className = "btn btn-primary btn-dark font-weight-bold mt-2";
  followArtistButtonElement.textContent = isFollowed ? "Unfollow" : "Follow";
  followArtistButtonElement.onclick =createOnClickFunctionFollowArtist(artistName,
    artistId,isFollowed,followArtistButtonElement);
  return followArtistButtonElement;
}

/**
 * Tasks when validation or ajax fails
 * @param message       The message to be displayed
 * @param containerId   The container to toggle the loader at
 */
function validationOrAjaxFailed(message, containerId) {
  createNoResultsMessage(message);
  toggleLoader(containerId);
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
