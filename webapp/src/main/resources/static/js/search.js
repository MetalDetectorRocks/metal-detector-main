/**
 * Send ajax request to search for an artist
 * @returns {boolean}
 */
function search(){
    toggleLoader("loading-indicator");

    const url = new URL(window.location.href);
    const query = url.searchParams.get("query");
    let page = url.searchParams.get("page");
    let size = url.searchParams.get("size");

    if (!validateSearch(query, page, size)) {
        page = 1;
        size = 40;
    }

    const parameter = {
        "query" : query,
        "page" : page,
        "size" : size
    };

    $.ajax({
        method: "GET",
        url: "/rest/v1/artists/search",
        data: parameter,
        dataType: "json",
        success: function(searchResponse) {
            handleSearchResponse(query, searchResponse);
            toggleLoader("loading-indicator");
        },
        error: function() {
            $("#no-results-container").append(renderUnknownErrorOccurred());
            toggleLoader("loading-indicator");
        }
    });

    return false;
}

/**
 * Basic validation for search parameters
 * @returns {boolean}
 * @param query The search query
 * @param page  The wanted page
 * @param size  Elements per page
 */
function validateSearch(query, page, size) {
    return query && !Number.isNaN(page) && !Number.isNaN(size);
}

/**
 * Builds the whole HTML for the presentation of the search results.
 * @param query                                  The search query
 * @param searchResponse                         The search response
 * @param searchResponse.pagination.currentPage  The current page
 * @param searchResponse.searchResults           The search results array
 */
function handleSearchResponse(query, searchResponse) {
    const currentPage = searchResponse.pagination.currentPage;
    const totalPages = searchResponse.pagination.totalPages;
    const itemsPerPage = searchResponse.pagination.itemsPerPage;
    const itemsOnThisPage = searchResponse.searchResults.length;

    if (itemsOnThisPage === 0) {
        $("#no-results-container").append(renderNoResultsFoundInfo(query));
    }
    else {
        appendSearchHeadline(query, searchResponse);

        if (currentPage === 1) {
            appendTopSearchResult(searchResponse.searchResults[0]);
        }

        if (currentPage === 1 && itemsOnThisPage > 1 || currentPage > 1) {
            appendOtherSearchResults(searchResponse.searchResults, currentPage);
        }

        if (totalPages > 1) {
            const pagination = $("<div>").attr("id", "pagination");
            pagination.pagination({
                itemsOnPage: itemsPerPage,
                pages: totalPages,
                currentPage: currentPage,
                displayedPages: 5,
                cssStyle: "",
                hrefTextPrefix: `search?query=${query}&page=`,
                prevText: "&laquo;",
                nextText: "&raquo;",
                ellipsePageSet: false
            });

            $("#other-results-container").append(pagination);
        }
    }
}

/**
 * Appends an headline for the search results to the DOM.
 * @param query           The search query
 * @param searchResponse  The search response
 */
function appendSearchHeadline(query, searchResponse) {
    const headlineText = createHeadlineText(query, searchResponse);
    const searchHeadline = $("<h1>").text(headlineText).addClass("h4 mb-4");
    $("#search-result-headline").append(searchHeadline);
}

/**
 * Creates the text for the headline.
 * @param query                                   The search query
 * @param searchResponse                          The whole search response
 * @param searchResponse.pagination.totalPages    Total amount of pages
 * @param searchResponse.pagination.itemsPerPage  Amount of items per page
 * @param searchResponse.searchResults            The search results array
 * @returns {string}                              The text for the headline
 */
function createHeadlineText(query, searchResponse) {
    const totalPages = searchResponse.pagination.totalPages;
    const itemsPerPage = searchResponse.pagination.itemsPerPage;
    if (totalPages === 1 && searchResponse.searchResults.length === 1) {
        const amount = searchResponse.searchResults.length;
        return `${amount} result for "${query}"`;
    }
    else if (totalPages === 1) {
        const amount = searchResponse.searchResults.length;
        return `${amount} results for "${query}"`;
    }
    else {
        const estimatedAmountOfResults = (totalPages - 1) * itemsPerPage;
        return `More than ${estimatedAmountOfResults} results for "${query}"`;
    }
}

/**
 * Appends the top search result to the DOM.
 * @param topSearchResult           The top search result
 * @param topSearchResult.id        The external id of the artist
 * @param topSearchResult.name      The name of the artist
 * @param topSearchResult.imageUrl  The url of the artist thumbnail
 * @param topSearchResult.followed  True if the current user already follow this artist, false otherwise
 */
function appendTopSearchResult(topSearchResult) {
    const topResultHtml = renderTopResultHtml({
        externalId: topSearchResult.id,
        name: topSearchResult.name,
        imageUrl: topSearchResult.imageUrl,
        followedByUser: topSearchResult.followed,
        followedByAmount: 666
    });
    $("#top-result-container").append(topResultHtml);
}

/**
 * Append the other search results to the DOM.
 * @param searchResults  The search results as array
 * @param currentPage    The current page
 */
function appendOtherSearchResults(searchResults, currentPage) {
    const headline = $("<h2>").text("Other").addClass("h5 custom-border-bottom pb-1");
    const resultsContainer = $("#other-results-container");
    resultsContainer.append(headline);

    let rowWrapper = null;
    let counter = 0;
    $.each(searchResults, function (index, result) {
        if (currentPage === 1 && index === 0) { // this is the top result
            return;
        }
        else if (rowWrapper === null) {
            rowWrapper = $("<div>").addClass("row mt-1 mb-4");
        }
        else if (counter === 3) {
            resultsContainer.append(rowWrapper);
            rowWrapper = $("<div>").addClass("row mb-4");
            counter = 0;
        }

        rowWrapper.append(renderOtherSearchResultsHtml({
            externalId: result.id,
            name: result.name,
            imageUrl: result.imageUrl,
            followedByUser: result.followed
        }));
        counter++;
    });

    appendCols(counter, rowWrapper);
    resultsContainer.append(rowWrapper);
}

/**
 *  Appends columns, otherwise the remaining columns will take up the entire space.
 *  @param currentCols  The current amount of columns within the last row
 *  @param rowWrapper   The div container for the current row
 */
function appendCols(currentCols, rowWrapper) {
    const colsToAppend = 3 - currentCols;
    if (colsToAppend === 1) {
        rowWrapper.append($("<div>").addClass("col"));
    }
    else if (colsToAppend === 2) {
        rowWrapper.append($("<div>").addClass("col"));
        rowWrapper.append($("<div>").addClass("col"));
    }
}

/**
 * Renders the HTML to show an information if no results could be found for the given query.
 * @param query       The search query
 * @returns {string}  The whole HTML to display a general message that no result could be found
 */
function renderNoResultsFoundInfo(query) {
    return `
    <div class="mb-3 alert alert-light">
        <h3 class="h5">No results could be found for "${query}".</h3>
        <p class="text-white">Try changing your search query.</p>
    </div>`;
}

/**
 * Renders the HTML to show an information that an unknown error has occurred.
 * @returns {string}   The whole HTML to display a general error message
 */
function renderUnknownErrorOccurred() {
    return `
    <div class="mb-3 alert alert-danger">
        <h3 class="h5">An unknown error has occurred.</h3>
        <p class="text-white">Please try again later.</p>
    </div>`;
}

/**
 * Renders the HTML for the top search result.
 * @param artistInfo.externalId         The external id of the artist
 * @param artistInfo.name               The name of the artist
 * @param artistInfo.imageUrl           The url of the artist thumbnail
 * @param artistInfo.followedByUser     True if the current user already follow this artist, false otherwise
 * @param artistInfo.followedByAmount   The amount of users that follow this artist
 * @returns {string}         The whole HTML for the top search result
 */
function renderTopResultHtml(artistInfo) {
    return `
        <h2 class="h5">Top result</h2>
        <div class="row mb-5">
            <div class="col">
                <div class="card">
                    <div class="row">
                        <div class="col-md-auto">
                            <img class="top-search-card-img" src="${determineArtistImageUrl(artistInfo.imageUrl)}" alt="${artistInfo.name}">
                        </div>
                        <div class="col">
                            <div class="row">
                                <div class="col">
                                    <p class="h4 card-title mt-3">${artistInfo.name}</p>
                                </div>
                                <div class="col-md-auto">
                                    ${renderFollowOrUnfollowIcon(artistInfo.externalId, artistInfo.name, artistInfo.followedByUser)}
                                </div>
                            </div>
                            <div class="row">
                                <div class="col">
                                    <p class="h6 card-subtitle">Followed by ${artistInfo.followedByAmount} users</p>
                                </div>
                                <div class="col"></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col"></div>
        </div>`;
}

/**
 * Renders the HTML for the other search results.
 * @param artistInfo.externalId         The external id of the artist
 * @param artistInfo.name               The name of the artist
 * @param artistInfo.imageUrl           The url of the artist thumbnail
 * @param artistInfo.followedByUser     True if the current user already follow this artist, false otherwise
 * @returns {string}  The whole HTML for the other search results
 */
function renderOtherSearchResultsHtml(artistInfo) {
    return `
        <div class="col">
            <div class="card">
                <div class="row">
                    <div class="col-md-auto">
                        <img class="search-card-img" src="${determineArtistImageUrl(artistInfo.imageUrl)}" alt="${artistInfo.name}">
                    </div>
                    <div class="col">
                        <div class="row">
                            <div class="col">
                                <p class="h5 card-title mt-2">${artistInfo.name}</p>
                            </div>
                            <div class="col-md-auto">
                                ${renderFollowOrUnfollowIcon(artistInfo.externalId, artistInfo.name, artistInfo.followedByUser)}
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>`;
}

/**
 * Renders the HTML for the follow or unfollow icon.
 * @param artistId        The artist's external id
 * @param artistName      The artist's name
 * @param followedByUser  True if the current user already follow this artist, false otherwise
 * @returns {string}      The whole HTML for follow or unfollow icon
 */
function renderFollowOrUnfollowIcon(artistId, artistName, followedByUser) {
    artistName = artistName.replace(new RegExp("'", "g"), "");
    artistName = artistName.replace(new RegExp('"', "g"), "");
    return followedByUser ?
        `<i id="${artistId}" class="follow-icon float-right material-icons m-2" onclick="unfollowArtist('${artistId}', '${artistName}')">favorite</i>` :
        `<i id="${artistId}" class="follow-icon float-right material-icons m-2" onclick="followArtist('${artistId}', '${artistName}')">favorite_border</i>`
}

/**
 * Returns the artist image url. If the given image url is empty, a general artist image is returned.
 * @param imageUrl    The current image url, maybe empty
 * @returns {string}  Image url
 */
function determineArtistImageUrl(imageUrl) {
    return imageUrl.trim() ? imageUrl : "/images/unknown-artist-img.jpg";
}
