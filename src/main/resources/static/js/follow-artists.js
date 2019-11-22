/**
 * Send ajax request to follow an artist
 * @param artistName    Artist to follow
 * @param artistId      Artist's discogs id
 * @param el            Button that was clicked
 * @returns {boolean}
 */
function followArtist(artistName,artistId,el){
    const followArtistRequest =
        {
            "artistName" : artistName,
            "artistDiscogsId" : artistId
        };
    const followArtistRequestJson = JSON.stringify(followArtistRequest);
    const csrfToken  = $("input[name='_csrf']").val();

    $.ajax({
        method: "POST",
        url: "/rest/v1/follow-artist",
        contentType: 'application/json',
        headers: {"X-CSRF-TOKEN": csrfToken},
        data: followArtistRequestJson,
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
    const followArtistRequest =
        {
            "artistName" : artistName,
            "artistDiscogsId" : artistId
        };
    const followArtistRequestJson = JSON.stringify(followArtistRequest);
    const csrfToken  = $("input[name='_csrf']").val();

    $.ajax({
        method: "DELETE",
        url: "/rest/v1/follow-artist",
        contentType: 'application/json',
        data: followArtistRequestJson,
        headers: {"X-CSRF-TOKEN": csrfToken},
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
