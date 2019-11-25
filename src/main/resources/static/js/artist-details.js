function artistDetails(artistName,artistId){
    const artistDetailsRequest =
        {
            "artistName" : artistName,
            "artistId" : artistId
        };
    const artistDetailsRequestJson = JSON.stringify(artistDetailsRequest);
    const csrfToken  = $("input[name='_csrf']").val();

    $.ajax({
        method: "POST",
        url: "/rest/v1/artist-details",
        contentType: 'application/json',
        headers: {"X-CSRF-TOKEN": csrfToken},
        data: artistDetailsRequestJson,
        dataType: "json",
        success: function(artistDetailsResponse){
            createArtistDetailsResultCard(artistDetailsResponse);
        },
        error: function(e){
            console.log(e.message);
        }
    });

    return false;
}

const createArtistDetailsResultCard = function(artistDetailsResponse) {
    clear();

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

        const images = document.createElement('div');
        jQuery.each(artistDetailsResponse.images, function (i, image){
            const imageElement = document.createElement('img');
            imageElement.alt = "Image of " + artistDetailsResponse.artistName;
            imageElement.src = image;
            images.appendChild(imageElement);
        });
        cardBodyImages.appendChild(images);
    }

    document.getElementById('artistDetailsContainer').appendChild(card);
};

function buildDefaultCardBody(title) {
    const cardBody = document.createElement('div');
    cardBody.className = "card-body";

    const headingElement = document.createElement('h3');
    headingElement.innerText = title;
    cardBody.appendChild(headingElement);

    return cardBody
}

function buildListCardBody(title,list) {
    const cardBody = document.createElement('div');
    cardBody.className = "card-body";

    const headingElement = document.createElement('h3');
    headingElement.innerText = title;
    cardBody.appendChild(headingElement);

    const listArea = document.createElement('div');
    cardBody.appendChild(listArea);

    const listElement = document.createElement('ul');
    jQuery.each(list, function (i, listItem){
        const listItemElement = document.createElement('li');
        listItemElement.innerText = listItem;
        listElement.appendChild(listItemElement);
    });
    cardBody.appendChild(listElement);

    return cardBody
}
