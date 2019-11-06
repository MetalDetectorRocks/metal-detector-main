function registerLogoutListener() {
    document.getElementById('logout-link').addEventListener('click', function(event) {
        event.preventDefault();
        document.getElementById('logout-form').submit();
    });
}

function followArtist(artistId){
    const followArtistRequest =
        {
            "artistDiscogsId" : artistId
        };
    const followArtistRequestJson = JSON.stringify(followArtistRequest);
    $.ajax({
        method: "POST",
        dataType: "json",
        url: "/rest/v1/follow-artists",
        contentType: 'application/json',
        data: followArtistRequestJson,
        error: function(e){
            console.log(e.message);
        }
    });
    return false;
}

function unfollowArtist(artistId){
    const unfollowArtistRequest =
        {
            "artistDiscogsId" : artistId
        };
    const unfollowArtistRequestJson = JSON.stringify(unfollowArtistRequest);
    $.ajax({
        method: "DELETE",
        dataType: "json",
        url: "/rest/v1/follow-artists",
        contentType: 'application/json',
        data: unfollowArtistRequestJson,
        error: function(e){
            console.log(e.message);
        }
    });
    return false;
}
