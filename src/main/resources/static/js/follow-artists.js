/**
 * Send ajax request to follow an artist
 * @param artistName    Artist to follow
 * @param artistId      Artist's discogs id
 * @param el            Button that was clicked
 * @returns {boolean}
 */
function followArtist(artistName,artistId,el){
    $.ajax({
        method: "POST",
        url: "/rest/v1/artists/follow/" + artistId,
        contentType: 'application/json',
        dataType: "json",
        success: function(){
            el.childNodes[0].nodeValue = 'Unfollow';
            el.onclick = createOnClickFunctionFollowArtist(artistName,artistId,true,el);
        },
        error: function(e){
            console.log(e.message);
        }
    });

    return false;
}

/**
 * Send ajax request to unfollow an artist
 * @param artistName    Artist to unfollow
 * @param artistId      Artist's discogs id
 * @param el            Button that was clicked
 * @returns {boolean}
 */
function unfollowArtist(artistName,artistId,el){
    $.ajax({
        method: "DELETE",
        url: "/rest/v1/artists/unfollow/" + artistId,
        contentType: 'application/json',
        dataType: "json",
        success: function(){
            el.childNodes[0].nodeValue = 'Follow';
            el.onclick = createOnClickFunctionFollowArtist(artistName,artistId,false,el);
        },
        error: function(e){
            console.log(e.message);
        }
    });

    return false;
}

/**
 * Builds the onclick function
 * @param artistName    Artist to follow
 * @param artistId      Artist's discogs id
 * @param isFollowed    true if user follows given artist
 * @param button        Button that was clicked
 * @returns {Function}
 */
function createOnClickFunctionFollowArtist(artistName, artistId, isFollowed, button) {
    return function () {
        if (isFollowed)
            unfollowArtist(artistName,artistId,button);
        else
            followArtist(artistName,artistId,button);
    };
}

function createFollowArtistButton(artistName,artistId,isFollowed) {
    const followArtistButtonElement = document.createElement('button');
    followArtistButtonElement.id = "followArtistButton"+artistId;
    followArtistButtonElement.type = "button";
    followArtistButtonElement.className = "btn btn-primary btn-dark font-weight-bold";
    followArtistButtonElement.textContent = isFollowed ? "Unfollow" : "Follow";
    followArtistButtonElement.onclick =createOnClickFunctionFollowArtist(artistName,
        artistId,isFollowed,followArtistButtonElement);
    return followArtistButtonElement;
}