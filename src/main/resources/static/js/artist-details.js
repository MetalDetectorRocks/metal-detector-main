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

    const cardHeader = document.createElement("div");
    cardHeader.className = "card-header";
    card.append(cardHeader);

    const navList = document.createElement("ul");
    navList.className = "nav nav-tabs card-header-tabs";
    cardHeader.append(navList);

    const navItemProfile = document.createElement("li");
    navItemProfile.className = "nav-item";
    navList.append(navItemProfile);

    const navLinkProfile = document.createElement("a");
    navLinkProfile.className = "nav-link active";
    navLinkProfile.id = "profileTab";
    navLinkProfile.href = "#";
    navLinkProfile.text = "Profile";
    navItemProfile.append(navLinkProfile);

    const navItemMembe = document.createElement("li");
    navItemMembe.className = "nav-item";
    navList.append(navItemMembe);

    const navLinkMember = document.createElement("a");
    navLinkMember.className = "nav-link";
    navLinkMember.id = "MemberTab";
    navLinkMember.href = "#";
    navLinkMember.text = "Member";
    navLinkMember.onclick = function () {
        showMember(artistDetailsResponse, card, navLinkProfile, navLinkMember);
    };
    navItemMembe.append(navLinkMember);

    const navItemImages = document.createElement("li");
    navItemImages.className = "nav-item";
    navList.append(navItemImages);

    const navLinkImages = document.createElement("a");
    navLinkImages.className = "nav-link";
    navLinkImages.id = "imagesTab";
    navLinkImages.href = "#";
    navLinkImages.text = "Images";
    navLinkImages.onclick = function () {
        showImages(artistDetailsResponse, card, navLinkProfile, navLinkImages);
    };
    navItemImages.append(navLinkImages);

    const cardTitle = document.createElement('h2');
    cardTitle.className = "card-title";
    cardTitle.innerText = artistDetailsResponse.artistName;
    card.append(cardTitle);

    const cardBodyButton = document.createElement('div');
    cardBodyButton.className = "card-body";
    cardBodyButton.append(createFollowArtistButton(artistDetailsResponse.artistName,artistDetailsResponse.artistId,artistDetailsResponse.isFollowed));
    card.append(cardBodyButton);

    if (artistDetailsResponse.profile) {
        const cardBodyProfile = buildDefaultCardBody("");
        cardBodyProfile.id = "card-body";
        card.append(cardBodyProfile);

        const profile = document.createElement('p');
        profile.innerText = artistDetailsResponse.profile;
        cardBodyProfile.append(profile);
    }

    if (artistDetailsResponse.images) {

    }

    document.getElementById('artistDetailsContainer').append(card);
}

function showProfile(artistDetailsResponse, card) {
    $("#card-body").empty();
    $("#memberTab").classList.remove("active");
    $("#profileTab").classList.add("active");
    $("#ImagesTab").classList.remove("active");

    const cardBodyProfile = buildDefaultCardBody("");
    cardBodyProfile.id = "card-body";
    card.append(cardBodyProfile);

    const profile = document.createElement('p');
    profile.innerText = artistDetailsResponse.profile;
    cardBodyProfile.append(profile);
}

function showMember(artistDetailsResponse, card) {
    $("#card-body").empty();
    $("#memberTab").classList.add("active");
    $("#profileTab").classList.remove("active");
    $("#ImagesTab").classList.remove("active");

    if (artistDetailsResponse.activeMember) {
        const cardBodyActiveMember = buildListCardBody("Active Member",artistDetailsResponse.activeMember);
        card.append(cardBodyActiveMember);
    }

    if (artistDetailsResponse.formerMember) {
        const cardBodyFormerMember = buildListCardBody("Former Member",artistDetailsResponse.formerMember);
        card.append(cardBodyFormerMember);
    }
}

function showImages(artistDetailsResponse, card) {
    $("#card-body").empty();
    $("#memberTab").classList.remove("active");
    $("#profileTab").classList.remove("active");
    $("#ImagesTab").classList.add("active");

    const cardBodyImages = buildDefaultCardBody("");
    card.append(cardBodyImages);

    jQuery.each(artistDetailsResponse.images, function (i, image){
        const imageElement = document.createElement('img');
        imageElement.alt = "Image of " + artistDetailsResponse.artistName;
        imageElement.src = image;
        cardBodyImages.append(imageElement);
    });
}

/**
 * Build a an empty card body with a title
 * @param title     The card's title
 * @returns {HTMLDivElement}
 */
function buildDefaultCardBody(title) {
    const cardBody = document.createElement('div');
    cardBody.className = "card-body";

    const headingElement = document.createElement('h3');
    headingElement.innerText = title;
    cardBody.append(headingElement);

    return cardBody;
}

/**
 * Builds a card body with a title and a list of Strings
 * @param title     The card's title
 * @param list      The list of Strings
 * @returns {HTMLDivElement}
 */
function buildListCardBody(title,list) {
    const cardBody = document.createElement('div');
    cardBody.className = "card-body";

    const headingElement = document.createElement('h3');
    headingElement.innerText = title;
    cardBody.append(headingElement);

    const listElement = document.createElement('ul');
    jQuery.each(list, function (i, listItem){
        const listItemElement = document.createElement('li');
        listItemElement.innerText = listItem;
        listElement.append(listItemElement);
    });
    cardBody.append(listElement);

    return cardBody;
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
