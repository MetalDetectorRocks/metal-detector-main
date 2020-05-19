/**
 * Send ajax request to follow an artist
 * @param artistId    Artist's discogs id
 * @param artistName  The name of the artist
 * @returns {boolean}
 */
function followArtist(artistId, artistName) {
    $.ajax({
        method: "POST",
        url: "/rest/v1/artists/follow/" + artistId,
        success: function() {
            const icon = $(`#${artistId}`);
            icon.text("favorite");
            icon.attr("onClick", `unfollowArtist('${artistId}', '${artistName}')`);
            const toastText = `You are now following "${artistName}"`;
            createToast(toastText);
        },
        error: function(e){
            console.log(e.message);
        }
    });
}

/**
 * Send ajax request to unfollow an artist
 * @param artistId    Artist's discogs id
 * @param artistName  The name of the artist
 * @returns {boolean}
 */
function unfollowArtist(artistId, artistName) {
    $.ajax({
        method: "POST",
        url: "/rest/v1/artists/unfollow/" + artistId,
        success: function() {
            const icon = $(`#${artistId}`);
            icon.text("favorite_border");
            icon.attr("onClick", `followArtist('${artistId}', '${artistName}')`);
            const toastText = `You no longer follow "${artistName}"`;
            createToast(toastText);
        },
        error: function(e){
            console.log(e.message);
        }
    });
}
