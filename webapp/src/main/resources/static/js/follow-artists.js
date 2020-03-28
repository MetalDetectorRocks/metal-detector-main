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
        method: "POST",
        url: "/rest/v1/artists/unfollow/" + artistId,
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
