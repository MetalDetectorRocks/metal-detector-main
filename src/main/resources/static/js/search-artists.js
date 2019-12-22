/**
 * Send ajax request to search for an artist
 * @param page          Requested page
 * @param size          Requested page size
 * @returns {boolean}
 */
function searchArtist(page,size){
    clear();
    toggleLoader("searchResultsContainer");

    const artistName = document.getElementById('artistName').value;
    const searchArtistRequest =
        {
            "artistName" : artistName,
            "page" : page,
            "size" : size
        };

    $.ajax({
        method: "GET",
        url: "/rest/v1/artists",
        data: searchArtistRequest,
        dataType: "json",
        success: function(artistNameSearchResponse){
            buildResults(artistNameSearchResponse);
            toggleLoader("searchResultsContainer");
        },
        error: function(){
            createNoArtistNameSearchResultsMessage(artistName);
            toggleLoader("searchResultsContainer");
        }
    });

    return false;
}

/**
 * Builds HTML with results and pagination
 * @param artistNameSearchResponse  JSON response
 */
function buildResults(artistNameSearchResponse) {
    createNavigationElement(artistNameSearchResponse);
    createResultCards(artistNameSearchResponse);
    createNavigationElement(artistNameSearchResponse);
}

/**
 * Clears all containers for new search responses
 */
function clear() {
    $("#searchResultsContainer").empty();
    $("#artistDetailsContainer").empty();

    const noResultsMessageElement = document.getElementById('noResultsMessageElement');
    if (noResultsMessageElement != null) {
        const noResultsMessageContainer = document.getElementById('noResultsMessageContainer');
        while (noResultsMessageContainer.firstChild) {
            noResultsMessageContainer.removeChild(noResultsMessageContainer.firstChild);
        }
    }
}

/**
 * Builds HTML for the result cards
 * @param artistNameSearchResponse
 */
function createResultCards(artistNameSearchResponse){
    jQuery.each(artistNameSearchResponse.artistSearchResults, function (i, artistSearchResult) {

        const card = document.createElement('div');
        card.className = "card";

        const cardBody = document.createElement('div');
        cardBody.className = "card-body";

        const headingElement = document.createElement('h3');
        headingElement.innerText = artistSearchResult.artistName;
        cardBody.append(headingElement);
        card.append(cardBody);

        if (artistSearchResult.thumb !== ""){
            const thumbElement = document.createElement('img');
            thumbElement.alt = 'Thumb for ' + artistSearchResult.artistName;
            thumbElement.src = artistSearchResult.thumb;
            cardBody.append(thumbElement);
        }

        const artistIdElement = document.createElement('p');
        artistIdElement.innerText = artistSearchResult.id;
        cardBody.append(artistIdElement);

        const artistDetailsElement = document.createElement('a');
        artistDetailsElement.href = "#";
        artistDetailsElement.text = "Details for " + artistSearchResult.artistName;
        artistDetailsElement.onclick = function func(){
            artistDetails(artistSearchResult.artistName,artistSearchResult.id);
        };
        cardBody.append(artistDetailsElement);

        const breakElement = document.createElement('br');
        cardBody.append(breakElement);

        const followArtistButtonElement = document.createElement('button');
        followArtistButtonElement.id = "followArtistButton" + artistSearchResult.id;
        followArtistButtonElement.type = "button";
        followArtistButtonElement.className = "btn btn-primary btn-dark font-weight-bold";
        followArtistButtonElement.textContent = artistSearchResult.isFollowed ? "Unfollow" : "Follow";
        followArtistButtonElement.onclick = createOnClickFunctionFollowArtist(artistSearchResult.artistName,
            artistSearchResult.id,artistSearchResult.isFollowed,followArtistButtonElement);
        cardBody.append(followArtistButtonElement);

        document.getElementById('searchResultsContainer').append(card);
    });
}

/**
 * Builds HTML for pagination
 * @param artistNameSearchResponse  The json response
 */
function createNavigationElement(artistNameSearchResponse) {
    const navElement = document.createElement("nav");
    const listElement = document.createElement("ul");
    listElement.className = "pagination pagination-sm justify-content-end";
    navElement.append(listElement);

    // Previous link
    createPreviousOrNextItem(artistNameSearchResponse, listElement, true);
    // Page links
    for (let index = 1; index <= artistNameSearchResponse.pagination.totalPages; index++) {
        createPageLinks(artistNameSearchResponse.pagination.currentPage, artistNameSearchResponse.pagination.itemsPerPage,
            index, listElement);
    }
    // Next link
    createPreviousOrNextItem(artistNameSearchResponse, listElement, false);

    document.getElementById('searchResultsContainer').append(navElement);
}

/**
 * Builds HTML for the message for an empty result
 */
function createNoArtistNameSearchResultsMessage(artistName) {
    const noResultsMessageElement = document.createElement('div');
    noResultsMessageElement.className = "mb-3 alert alert-danger";
    noResultsMessageElement.role = "alert";
    noResultsMessageElement.id = "noResultsMessageElement";
    noResultsMessageElement.innerText =  "No artists could be found for the given name: " + artistName;

    document.getElementById('noResultsMessageContainer').append(noResultsMessageElement);
}

/**
 * Creates pagination page links and appends them to the given element
 * @param currentPage   The current page
 * @param itemsPerPage  Max items per page
 * @param index         Index number of the link
 * @param element       Element to add page links to
 */
function createPageLinks(currentPage, itemsPerPage, index, element) {
    const listItem = document.createElement("li");
    listItem.className = "page-item";

    if (index === currentPage)
        listItem.classList.add("active");

    const link = document.createElement("a");
    link.className = "page-link";
    link.href = "#";
    link.text = String(index);
    link.onclick = (function (page, itemsPerPage) {
        return function () {
            searchArtist(page, itemsPerPage)
        };
    })(index, itemsPerPage);

    listItem.append(link);
    element.append(listItem);
}

/**
 * Create the previos or next pagination butteon
 * @param artistNameSearchResponse  Search result
 * @param element                   Element to append to
 * @param previous                  True if previous shall be created, false for next
 */
function createPreviousOrNextItem(artistNameSearchResponse, element, previous) {
    const item = document.createElement("li");
    item.className = "page-item";

    let text;
    let targetPage;
    let symbol;

    if (previous){
        text = "Previous";
        targetPage = artistNameSearchResponse.pagination.currentPage - 1;
        symbol = "\u00AB";
        item.classList.add("prev");
        if (artistNameSearchResponse.pagination.currentPage === 1)
            item.classList.add("disabled");

    } else {
        text = "Next";
        targetPage = artistNameSearchResponse.pagination.currentPage + 1;
        symbol = "\u00BB";
        item.classList.add("next");
        if (artistNameSearchResponse.pagination.currentPage === artistNameSearchResponse.pagination.totalPages)
            item.classList.add("disabled");
    }

    const link = document.createElement("a");
    link.setAttribute('aria-label', text);
    link.className = "page-link";
    link.href = "#";
    link.onclick = (function (page, itemsPerPage) {
        return function () {
            searchArtist(page, itemsPerPage)
        };
    })(targetPage, artistNameSearchResponse.pagination.itemsPerPage);

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
