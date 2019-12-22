/**
 * Send ajax request to retrieve artist details
 * @param artistName        The artist's name
 * @param artistId          The artist's discogs id
 * @returns {boolean}
 */
function artistDetails(artistName,artistId){
    clear();
    toggleLoader("artistDetailsContainer");

    $.ajax({
        method: "GET",
        url: "/rest/v1/artists/" + artistId,
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
    card.id = "artistDetails";
    document.getElementById('artistDetailsContainer').append(card);

    createCardNavigation(card, artistDetailsResponse);

    const titleContainer = document.createElement("div");
    titleContainer.className = "row ml-2 mt-2";
    card.append(titleContainer);

    const cardTitle = document.createElement('h2');
    cardTitle.className = "card-title";
    cardTitle.innerText = artistDetailsResponse.artistName;
    titleContainer.append(cardTitle);

    const cardBodyButton = document.createElement('div');
    cardBodyButton.append(createFollowArtistButton(artistDetailsResponse.artistName,artistDetailsResponse.artistId,artistDetailsResponse.isFollowed));
    titleContainer.append(cardBodyButton);

    const cardBody = document.createElement('div');
    cardBody.id = "artistDetailsCardBody";
    cardBody.className = "card-body";
    card.append(cardBody);

    if (artistDetailsResponse.profile)
        showProfile(artistDetailsResponse);
    else if (artistDetailsResponse.activeMember || artistDetailsResponse.formerMember)
        showMember(artistDetailsResponse);
    else if (artistDetailsResponse.images)
        createImageGallery(artistDetailsResponse);
}

/**
 * Builds the HTML for the navigation bar
 * @param card  The artist details card
 * @param artistDetailsResponse The json response
 */
function createCardNavigation(card, artistDetailsResponse) {
    const cardHeader = document.createElement("div");
    cardHeader.className = "card-header";
    cardHeader.id = "topheader";
    card.append(cardHeader);

    const navbarElement = document.createElement("nav");
    navbarElement.className = "navbar navbar-expand-lg";
    cardHeader.append(navbarElement);

    const navList = document.createElement("ul");
    navList.className = "nav navbar-nav";
    navbarElement.append(navList);

    const navItemProfile = createNavItem("profile");
    navItemProfile.classList.add("active");
    navList.append(navItemProfile);

    if (artistDetailsResponse.profile)
        navItemProfile.onclick = function () {
            showProfile(artistDetailsResponse);
        };
    else
        navItemProfile.classList.add("disabled");

    const navItemMember = createNavItem("member");
    navList.append(navItemMember);

    if (artistDetailsResponse.activeMember || artistDetailsResponse.formerMember)
        navItemMember.onclick = function () {
            showMember(artistDetailsResponse);
        };
    else
        navItemMember.classList.add("disabled");

    const navItemImages = createNavItem("images");
    navList.append(navItemImages);

    if (artistDetailsResponse.images)
        navItemImages.onclick = function () {
            createImageGallery(artistDetailsResponse)
        };
    else
        navItemImages.classList.add("disabled");
}

/**
 * Create a standard HTML li with a link
 * @param name  The name of the tab
 * @returns {HTMLLIElement} The li element
 */
function createNavItem(name) {
    const listItem = document.createElement("li");

    const link = document.createElement("a");
    link.href = "#";
    link.text = name;
    listItem.append(link);

    return listItem;
}

/**
 * Clears the card and shows the profile
 * @param artistDetailsResponse The json response
 */
function showProfile(artistDetailsResponse) {
    const cardBody = document.getElementById("artistDetailsCardBody");

    while (cardBody.firstChild)
        cardBody.removeChild(cardBody.firstChild);

    if (artistDetailsResponse.profile) {
        const profile = document.createElement('p');
        profile.className = "card-text";
        profile.innerText = artistDetailsResponse.profile;
        cardBody.append(profile);
    }
}

/**
 * Clears the card and shows the member lists
 * @param artistDetailsResponse The json response
 */
function showMember(artistDetailsResponse) {
    const cardBody = document.getElementById("artistDetailsCardBody");

    while (cardBody.firstChild)
        cardBody.removeChild(cardBody.firstChild);

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

/**
 * Clears the card and shows the image gallery
 * @param artistDetailsResponse The json response
 */
function createImageGallery(artistDetailsResponse) {
    const cardBody = document.getElementById("artistDetailsCardBody");

    while (cardBody.firstChild)
        cardBody.removeChild(cardBody.firstChild);

    const imageRow = document.createElement("div");
    imageRow.className = "image-row";
    cardBody.append(imageRow);

    const imageSet = document.createElement("div");
    imageSet.className = "image-set";
    imageRow.append(imageSet);

    jQuery.each(artistDetailsResponse.images, function (i, image) {
        const link = document.createElement("a");
        link.className = "image-link";
        link.href = image;
        link.setAttribute("data-lightbox","example-set");
        imageSet.append(link);

        const image1 = document.createElement("img");
        image1.className = "image";
        image1.src = image;
        link.append(image1);
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

/**
 * Switches active nav tab on click
 */
$(document).on('click', '#topheader .navbar-nav a', function () {
    $('#topheader .navbar-nav').find('li.active').removeClass('active');
    $(this).parent('li').addClass('active');
});

lightbox.option({
    'resizeDuration': 200
});
