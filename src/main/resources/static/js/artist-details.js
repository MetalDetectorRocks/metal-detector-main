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
const createArtistDetailsResultCard = function(artistDetailsResponse) {
    const card = document.createElement('div');
    card.className = "card";

    const cardTitle = document.createElement('h2');
    cardTitle.className = "card-title";
    cardTitle.innerText = artistDetailsResponse.artistName;
    card.appendChild(cardTitle);

    const cardBodyButton = document.createElement('div');
    cardBodyButton.className = "card-body";
    cardBodyButton.append(createFollowArtistButton(artistDetailsResponse.artistName,artistDetailsResponse.artistId,artistDetailsResponse.isFollowed));
    card.appendChild(cardBodyButton);

    if (artistDetailsResponse.profile) {
        const cardBodyProfile = buildDefaultCardBody("Profile");
        card.appendChild(cardBodyProfile);

        const profile = document.createElement('p');
        profile.innerText = artistDetailsResponse.profile;
        cardBodyProfile.appendChild(profile);
    }

    if (artistDetailsResponse.activeMember) {
        const cardBodyActiveMember = buildListCardBody("Active Member",artistDetailsResponse.activeMember);
        card.appendChild(cardBodyActiveMember);
    }

    if (artistDetailsResponse.formerMember) {
        const cardBodyFormerMember = buildListCardBody("Former Member",artistDetailsResponse.formerMember);
        card.appendChild(cardBodyFormerMember);
    }

    if (artistDetailsResponse.images) {
        const cardBodyImages = buildDefaultCardBody("Images");
        card.appendChild(cardBodyImages);

        jQuery.each(artistDetailsResponse.images, function (i, image){
            const imageElement = document.createElement('img');
            imageElement.alt = "Image of " + artistDetailsResponse.artistName;
            imageElement.src = image;
            cardBodyImages.appendChild(imageElement);
        });
    }

    document.getElementById('artistDetailsContainer').appendChild(card);
};

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
    cardBody.appendChild(headingElement);

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
    cardBody.appendChild(headingElement);

    const listElement = document.createElement('ul');
    jQuery.each(list, function (i, listItem){
        const listItemElement = document.createElement('li');
        listItemElement.innerText = listItem;
        listElement.appendChild(listItemElement);
    });
    cardBody.appendChild(listElement);

    return cardBody;
}

/**
 * Builds HTML for the message for an empty result
 */
const createNoArtistDetailsMessage = function (artistName,artistId) {
    const noResultsMessageElement = document.createElement('div');
    noResultsMessageElement.className = "mb-3 alert alert-danger";
    noResultsMessageElement.role = "alert";
    noResultsMessageElement.id = "noResultsMessageElement";

    const messageTextElement = document.createElement('p');
    messageTextElement.innerText = "No data could be found for the given parameters:";
    noResultsMessageElement.appendChild(messageTextElement);

    const parameterListElement = document.createElement('ul');

    const listItemName = document.createElement('li');
    listItemName.innerText = "Artist name: " + artistName;
    parameterListElement.appendChild(listItemName);

    const listItemId = document.createElement('li');
    listItemId.innerText = "Artist id: " + artistId;
    parameterListElement.appendChild(listItemId);

    noResultsMessageElement.appendChild(parameterListElement);

    document.getElementById('noResultsMessageContainer').appendChild(noResultsMessageElement);
};
