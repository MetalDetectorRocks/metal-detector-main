/**
 * Send ajax request to search for an artist
 * @param page          Requested page
 * @param size          Requested page size
 * @returns {boolean}
 */
function search(searchRequest){
    clear();
    toggleLoader("searchResultsContainer");

    const parameter =
        {
            "query" : searchRequest.query,
            "page" : searchRequest.page,
            "size" : searchRequest.size
        };

    $.ajax({
        method: "GET",
        url: "/rest/v1/artists/search",
        data: parameter,
        dataType: "json",
        success: function(searchResponse){
            buildResults(searchResponse);
            toggleLoader("searchResultsContainer");
        },
        error: function(){
            createNoResultsMessage(searchRequest.query);
            toggleLoader("searchResultsContainer");
        }
    });

    return false;
}

/**
 * Builds HTML with results and pagination
 * @param searchResponse  JSON response
 */
function buildResults(searchResponse) {
    createNavigationElement(searchResponse);
    createResultCards(searchResponse);
    createNavigationElement(searchResponse);
}

/**
 * Clears all containers for new search responses
 */
function clear() {
    $("#searchResultsContainer").empty();
    $("#artistDetailsContainer").empty(); // todo: adapat clear for multiple pages

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
 * @param searchResponse
 */
function createResultCards(searchResponse){
    jQuery.each(searchResponse.searchResults, function (i, searchResult) {
        const card = document.createElement('div');
        card.className = "card";

        const cardBody = document.createElement('div');
        cardBody.className = "card-body";

        const headingElement = document.createElement('h3');
        headingElement.innerText = searchResult.artistName;
        cardBody.append(headingElement);
        card.append(cardBody);

        console.log(headingElement.innerText);

        if (searchResult.thumb !== ""){
            const thumbElement = document.createElement('img');
            thumbElement.alt = 'Thumb for ' + searchResult.artistName;
            thumbElement.src = searchResult.thumb;
            cardBody.append(thumbElement);
        }

        const artistIdElement = document.createElement('p');
        artistIdElement.innerText = searchResult.id;
        cardBody.append(artistIdElement);

        const artistDetailsElement = document.createElement('a');
        artistDetailsElement.href = "#";
        artistDetailsElement.text = "Details for " + searchResult.artistName;
        artistDetailsElement.onclick = function func(){
            artistDetails(searchResult.artistName,searchResult.id);
        };
        cardBody.append(artistDetailsElement);

        const breakElement = document.createElement('br');
        cardBody.append(breakElement);

        const followArtistButtonElement = document.createElement('button');
        followArtistButtonElement.id = "followArtistButton" + searchResult.id;
        followArtistButtonElement.type = "button";
        followArtistButtonElement.className = "btn btn-primary btn-dark font-weight-bold";
        followArtistButtonElement.textContent = searchResult.isFollowed ? "Unfollow" : "Follow";
        followArtistButtonElement.onclick = createOnClickFunctionFollowArtist(searchResult.artistName,
            searchResult.id,searchResult.isFollowed,followArtistButtonElement);
        cardBody.append(followArtistButtonElement);

        document.getElementById('searchResultsContainer').append(card);
    });
}

/**
 * Builds HTML for pagination
 * @param searchResponse  The json response
 */
function createNavigationElement(searchResponse) {
    const navElement = document.createElement("nav");
    const listElement = document.createElement("ul");
    listElement.className = "pagination pagination-sm justify-content-end";
    navElement.append(listElement);

    // Previous link
    createPreviousOrNextItem(searchResponse, listElement, true);
    // Page links
    for (let index = 1; index <= searchResponse.pagination.totalPages; index++) {
        createPageLinks(searchResponse.pagination.currentPage, searchResponse.pagination.itemsPerPage,
            index, listElement);
    }
    // Next link
    createPreviousOrNextItem(searchResponse, listElement, false);

    document.getElementById('searchResultsContainer').append(navElement);
}

/**
 * Builds HTML for the message for an empty result
 */
function createNoResultsMessage(query) {
    const noResultsMessageElement = document.createElement('div');
    noResultsMessageElement.className = "mb-3 alert alert-danger";
    noResultsMessageElement.role = "alert";
    noResultsMessageElement.id = "noResultsMessageElement";
    noResultsMessageElement.innerText =  "No results could be found for the given query: " + query;

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
            search(page, itemsPerPage)
        };
    })(index, itemsPerPage);

    listItem.append(link);
    element.append(listItem);
}

/**
 * Create the previos or next pagination butteon
 * @param searchResponse  Search result
 * @param element                   Element to append to
 * @param previous                  True if previous shall be created, false for next
 */
function createPreviousOrNextItem(searchResponse, element, previous) {
    const item = document.createElement("li");
    item.className = "page-item";

    let text;
    let targetPage;
    let symbol;

    if (previous){
        text = "Previous";
        targetPage = searchResponse.pagination.currentPage - 1;
        symbol = "\u00AB";
        item.classList.add("prev");
        if (searchResponse.pagination.currentPage === 1)
            item.classList.add("disabled");

    } else {
        text = "Next";
        targetPage = searchResponse.pagination.currentPage + 1;
        symbol = "\u00BB";
        item.classList.add("next");
        if (searchResponse.pagination.currentPage === searchResponse.pagination.totalPages)
            item.classList.add("disabled");
    }

    const link = document.createElement("a");
    link.setAttribute('aria-label', text);
    link.className = "page-link";
    link.href = "#";
    link.onclick = (function (page, itemsPerPage) {
        return function () {
            search(page, itemsPerPage)
        };
    })(targetPage, searchResponse.pagination.itemsPerPage);

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
