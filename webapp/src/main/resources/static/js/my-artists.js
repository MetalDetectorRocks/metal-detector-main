/**
 * Send ajax request for followed artists
 * @returns {boolean}
 */
function showMyArtists() {
  toggleLoader("artists-container");

  const url = new URL(window.location.href);
  const size = url.searchParams.get("size");
  let page = url.searchParams.get("page");

  if (page != null) page--;

  const parameter =
    {
      "page" : page,
      "size" : size
    };

  $.ajax({
    method: "GET",
    url: "/rest/v1/my-artists",
    data: parameter,
    dataType: "json",
    success: function(myArtists){
      buildResultsMyArtists(myArtists);
      toggleLoader("artists-container");
    },
    error: function(){
      const message = "No artists could be found!";
      validationOrAjaxFailed(message, 'artists-container');
      toggleLoader("artists-container");
    }
  });

  return false;
}

/**
 * Builds HTML with results and pagination
 * @param myArtists  JSON response
 */
function buildResultsMyArtists(myArtists) {
  createMyArtistsCards(myArtists);
  createNavigationElementMyArtists(myArtists);
}

/**
 * Builds HTML for the result cards
 * @param myArtists
 */
function createMyArtistsCards(myArtists){
  jQuery.each(myArtists.myArtists, function (i, artist) {
    const card = document.createElement('div');
    card.className = "card";

    const cardBody = document.createElement('div');
    cardBody.className = "card-body";

    const headingElement = document.createElement('h3');
    headingElement.innerText = artist.artistName;
    cardBody.append(headingElement);
    card.append(cardBody);

    if (artist.thumb){
      const thumbElement = document.createElement('img');
      thumbElement.className = "card-image";
      thumbElement.alt = 'Thumb for ' + artist.artistName;
      thumbElement.src = artist.thumb;
      cardBody.append(thumbElement);
    }

    const breakElement = document.createElement('br');
    cardBody.append(breakElement);

    const followArtistButtonElement = createFollowArtistButton(artist.artistName,
      artist.externalId, true);
    cardBody.append(followArtistButtonElement);

    document.getElementById('artists-container').append(card);
  });
}

/**
 * Builds HTML for pagination
 * @param myArtists  The json response
 */
function createNavigationElementMyArtists(myArtists) {
  const navElement = document.createElement("nav");
  const listElement = document.createElement("ul");
  listElement.className = "pagination pagination-sm justify-content-end";
  navElement.append(listElement);

  // Previous link
  if (myArtists.pagination.currentPage > 0)
    createPreviousOrNextItemMyArtists(myArtists, listElement, true);

  // Page links
  if (myArtists.pagination.totalPages <= 5) {
    for (let index = 0; index < myArtists.pagination.totalPages; index++) {
      createPageLinkMyArtists(myArtists, index, listElement);
    }
  } else {
    // Show first five pages
    for (let index = 0; index < 5; index++) {
      createPageLinkMyArtists(myArtists, index, listElement);
    }

    // Show first placeholder
    if (myArtists.pagination.currentPage < 4 || myArtists.pagination.currentPage > 6)
      createPlaceholder(listElement);

    // Show one before current page
    if (myArtists.pagination.currentPage > 5)
      createPageLinkMyArtists(myArtists, myArtists.pagination.currentPage - 1, listElement);

    // Show current page
    if (myArtists.pagination.currentPage > 4) {
      createPageLinkMyArtists(myArtists, myArtists.pagination.currentPage, listElement);
    }

    // Show one after current page
    if (myArtists.pagination.currentPage > 3 &&
        myArtists.pagination.currentPage < myArtists.pagination.totalPages - 2)
      createPageLinkMyArtists(myArtists, myArtists.pagination.currentPage + 1, listElement);

    // Show second placeholder
    if (myArtists.pagination.currentPage > 3 && myArtists.pagination.currentPage < myArtists.pagination.totalPages - 2)
      createPlaceholder(listElement);

    // Show last page
    if (myArtists.pagination.currentPage !== myArtists.pagination.totalPages - 1)
      createPageLinkMyArtists(myArtists, myArtists.pagination.totalPages - 1, listElement);
  }

  // Next link
  if (myArtists.pagination.currentPage + 1 < myArtists.pagination.totalPages)
    createPreviousOrNextItemMyArtists(myArtists, listElement, false);

  document.getElementById('artists-container').append(navElement);
}

/**
 * Creates pagination page link and appends it to the given element
 * @param myArtists         Result object
 * @param index             Index number of the link
 * @param element           Element to add page links to
 */
function createPageLinkMyArtists(myArtists, index, element) {
  const listItem = document.createElement("li");
  listItem.className = "page-item";

  if (index === myArtists.pagination.currentPage)
    listItem.classList.add("active");

  const link = document.createElement("a");
  link.className = "page-link";
  link.href = "/my-artists?page=" + (index+1) + "&size=" + myArtists.pagination.itemsPerPage;
  link.text = String(index+1);

  listItem.append(link);
  element.append(listItem);
}

/**
 * Create the previos or next pagination butteon
 * @param myArtists    Search result object
 * @param element           Element to append to
 * @param previous          True if previous shall be created, false for next
 */
function createPreviousOrNextItemMyArtists(myArtists, element, previous) {
  const item = document.createElement("li");
  item.className = "page-item";

  let text;
  let targetPage;
  let symbol;

  if (previous){
    text = "Previous";
    targetPage = myArtists.pagination.currentPage;
    symbol = "\u00AB";
    item.classList.add("prev");

  } else {
    text = "Next";
    targetPage = myArtists.pagination.currentPage + 2;
    symbol = "\u00BB";
    item.classList.add("next");
  }

  const link = document.createElement("a");
  link.setAttribute('aria-label', text);
  link.className = "page-link";
  link.href = "/my-artists?page=" + targetPage + "&size=" + myArtists.pagination.itemsPerPage;

  let span = document.createElement("span");
  span.setAttribute('aria-hidden', true);
  span.textContent = symbol;
  link.append(span);

  span = document.createElement("span");
  span.className = "sr-only";
  span.textContent = text;
  link.append(span);

  item.append(link);
  element.append(item);
}
