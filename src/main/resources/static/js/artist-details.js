/**
 * Send ajax request to retrieve artist details
 * @param artistName        The artist's name
 * @param artistId          The artist's discogs id
 * @returns {boolean}
 */
function artistDetails(artistName,artistId){
    clear();
    toggleLoader("artistDetailsContainer");

    const artistDetailsRequest =
        {
            "artistName" : artistName,
            "artistId" : artistId
        };

    $.ajax({
        method: "GET",
        url: "/rest/v1/artist-details",
        data: artistDetailsRequest,
        dataType: "json",
        success: function(artistDetailsResponse){
            createArtistDetailsResultCard(artistDetailsResponse);
            toggleLoader("artistDetailsContainer");
        },
        error: function(){
            createNoArtistDetailsMessage(artistName,artistId);
            toggleLoader("artistDetailsContainer");
        }
    });

    return false;
}

/**
 * Build the HTML for the result
 * @param artistDetailsResponse     The json response
 */
function createArtistDetailsResultCard(artistDetailsResponse) {
    const card = document.createElement('div');
    card.className = "card";
    document.getElementById('artistDetailsContainer').append(card);

    createCardNavigation(card, artistDetailsResponse);

    const cardTitle = document.createElement('h2');
    cardTitle.className = "card-title";
    cardTitle.innerText = artistDetailsResponse.artistName;
    card.append(cardTitle);

    const cardBodyButton = document.createElement('div');
    cardBodyButton.className = "card-body";
    cardBodyButton.append(createFollowArtistButton(artistDetailsResponse.artistName,artistDetailsResponse.artistId,artistDetailsResponse.isFollowed));
    card.append(cardBodyButton);

    const cardBody = document.createElement('div');
    cardBody.id = "artistDetailsCardBody";
    cardBody.className = "card-body";
    card.append(cardBody);

    if (artistDetailsResponse.profile)
        showProfile(artistDetailsResponse);
    else if (artistDetailsResponse.activeMember || artistDetailsResponse.formerMember)
        showMember(artistDetailsResponse);
    else if (artistDetailsResponse.images)
        showImages(artistDetailsResponse);
}

function createCardNavigation(card, artistDetailsResponse) {
    const cardHeader = document.createElement("div");
    cardHeader.className = "card-header";
    card.append(cardHeader);

    const navElement = document.createElement("nav");
    navElement.className = "navbar navbar-default";
    cardHeader.append(navElement);

    const navList = document.createElement("ul");
    navList.className = "nav card-header-tabs";
    // navList.className = "nav navbar-nav";
    navElement.append(navList);

    const navItemProfile = createNavItem(artistDetailsResponse, "Profile");
    navList.append(navItemProfile);

    if (artistDetailsResponse.profile)
        navItemProfile.onclick = function () {
            showProfile(artistDetailsResponse);
        };
    else
        navItemProfile.classList.add("disabled");

    const navItemMember = createNavItem(artistDetailsResponse, "Member");
    navList.append(navItemMember);

    if (artistDetailsResponse.activeMember || artistDetailsResponse.formerMember)
        navItemMember.onclick = function () {
            showMember(artistDetailsResponse);
        };
    else
        navItemMember.classList.add("disabled");

    const navItemImages = createNavItem(artistDetailsResponse, "Images");
    navList.append(navItemImages);

    if (artistDetailsResponse.images)
        navItemImages.onclick = function () {
            showImages(artistDetailsResponse);
        };
    else
        navItemImages.classList.add("disabled");
}

function createNavItem(artistDetailsResponse, name) {
    const navItem = document.createElement("li");
    navItem.className = "nav-item";

    const navLink = document.createElement("a");
    navLink.className = "nav-link";
    navLink.id = name.toLowerCase() + "Tab";
    navLink.href = "#";
    navLink.text = name;
    navItem.append(navLink);

    return navItem;
}

function showProfile(artistDetailsResponse) {
    const cardBody = document.getElementById("artistDetailsCardBody");

    while (cardBody.firstChild)
        cardBody.removeChild(cardBody.firstChild);

    document.getElementById("profileTab").classList.add("active");
    document.getElementById("memberTab").classList.remove("active");
    document.getElementById("imagesTab").classList.remove("active");

    if (artistDetailsResponse.profile) {
        const profile = document.createElement('p');
        profile.className = "card-text";
        profile.innerText = artistDetailsResponse.profile;
        cardBody.append(profile);
    }
}

function showMember(artistDetailsResponse) {
    const cardBody = document.getElementById("artistDetailsCardBody");

    while (cardBody.firstChild)
        cardBody.removeChild(cardBody.firstChild);

    document.getElementById("memberTab").classList.add("active");
    document.getElementById("profileTab").classList.remove("active");
    document.getElementById("imagesTab").classList.remove("active");

    if (artistDetailsResponse.activeMember) {
        const headerElement = document.createElement("h4");
        headerElement.innerText = "Active Member";
        const listElement = createListElement(artistDetailsResponse.activeMember);
        cardBody.append(headerElement);
        cardBody.append(listElement);
    }

    if (artistDetailsResponse.formerMember) {
        const headerElement = document.createElement("h4");
        headerElement.innerText = "Former Member";
        const listElement = createListElement(artistDetailsResponse.formerMember);
        cardBody.append(headerElement);
        cardBody.append(listElement);
    }
}

function showImages(artistDetailsResponse) {
    const cardBody = document.getElementById("artistDetailsCardBody");

    while (cardBody.firstChild)
        cardBody.removeChild(cardBody.firstChild);

    document.getElementById("imagesTab").classList.add("active");
    document.getElementById("profileTab").classList.remove("active");
    document.getElementById("memberTab").classList.remove("active");

    jQuery.each(artistDetailsResponse.images, function (i, image){
        const imageElement = document.createElement('img');
        imageElement.alt = "Image of " + artistDetailsResponse.artistName;
        imageElement.src = image;
        cardBody.append(imageElement);
    });
}

/**
 * Builds a list of Strings
 * @param list      The list of Strings
 * @returns {HTMLUListElement}
 */
function createListElement(list) {
    const listElement = document.createElement('ul');
    jQuery.each(list, function (i, listItem){
        const listItemElement = document.createElement('li');
        listItemElement.innerText = listItem;
        listElement.append(listItemElement);
    });

    return listElement;
}

/**
 * Builds HTML for the message for an empty result
 */
function createNoArtistDetailsMessage(artistName,artistId) {
    const noResultsMessageElement = document.createElement('div');
    noResultsMessageElement.className = "mb-3 alert alert-danger";
    noResultsMessageElement.role = "alert";
    noResultsMessageElement.id = "noResultsMessageElement";

    const messageTextElement = document.createElement('p');
    messageTextElement.innerText = "No data could be found for the given parameters:";
    noResultsMessageElement.append(messageTextElement);

    const parameterListElement = document.createElement('ul');

    const listItemName = document.createElement('li');
    listItemName.innerText = "Artist name: " + artistName;
    parameterListElement.append(listItemName);

    const listItemId = document.createElement('li');
    listItemId.innerText = "Artist id: " + artistId;
    parameterListElement.append(listItemId);

    noResultsMessageElement.append(parameterListElement);

    document.getElementById('noResultsMessageContainer').append(noResultsMessageElement);
}
