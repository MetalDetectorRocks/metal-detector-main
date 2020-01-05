/**
 * Send ajax request to retrieve artist details
 * @returns {boolean}
 */
function artistDetails(){
    toggleLoader("artistDetailsContainer");

    const pathArray = window.location.pathname.split("/");
    const artistId = pathArray[pathArray.length-1];

    if (!validateArtistDetails(artistId)) {
        const message = "No data could be found for the given id: " + artistId;
        validationOrAjaxFailed(message, 'artistDetailsContainer');
        return false;
    }

    $.ajax({
        method: "GET",
        url: "/rest/v1/artists/" + artistId,
        dataType: "json",
        success: function(artistDetailsResponse){
            createArtistDetailsResultCard(artistDetailsResponse);
            toggleLoader("artistDetailsContainer");
        },
        error: function(){
            const message = "No data could be found for the given id: " + artistId;
            validationOrAjaxFailed(message, 'artistDetailsContainer');
        }
    });

    return false;
}

/**
 * Basic validation for search parameters
 * @param artistId      The artist's discogs id as path parameter
 * @returns {boolean}
 */
function validateArtistDetails(artistId) {
    return !Number.isNaN(artistId) && artistId > 0;
}

/**
 * Build the HTML for the result
 * @param artistDetailsResponse     The json response
 */
function createArtistDetailsResultCard(artistDetailsResponse) {
    document.getElementById('artistName').innerText = artistDetailsResponse.artistName;

    const card = document.createElement('div');
    card.className = "card";
    card.id = "artistDetails";
    document.getElementById('artistDetailsContainer').append(card);

    createCardNavigation(card, artistDetailsResponse);

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
    navList.append(navItemProfile);

    if (artistDetailsResponse.profile) {
        navItemProfile.classList.add("active");
        navItemProfile.onclick = function () {
            showProfile(artistDetailsResponse);
        };
    }
    else
        navItemProfile.classList.add("disabled");

    const navItemMember = createNavItem("member");
    navList.append(navItemMember);

    if (artistDetailsResponse.activeMember || artistDetailsResponse.formerMember) {
        if (!navItemProfile.classList.contains("active"))
            navItemMember.classList.add("active");
        navItemMember.onclick = function () {
            showMember(artistDetailsResponse);
        };
    }
    else
        navItemMember.classList.add("disabled");

    const navItemImages = createNavItem("images");
    navList.append(navItemImages);

    if (artistDetailsResponse.images) {
        if (!navItemProfile.classList.contains("active") && !navItemMember.classList.contains("active"))
            navItemImages.classList.add("active");
        navItemImages.onclick = function () {
            createImageGallery(artistDetailsResponse)
        };
    }
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
 * Switches active nav tab on click
 */
$(document).on('click', '.navbar-nav a', function () {
    $('.navbar-nav').find('li.active').removeClass('active');
    $(this).parent('li').addClass('active');
});

lightbox.option({
    'resizeDuration': 200
});
