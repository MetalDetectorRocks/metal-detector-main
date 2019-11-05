function registerLogoutListener() {
    document.getElementById('logout-link').addEventListener('click', function(event) {
        event.preventDefault();
        document.getElementById('logout-form').submit();
    });
}

function followArtist(){
    const followArtistRequest =
        {
            "artistDiscogsId" : document.getElementById('discogsArtistId').innerText
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

function unfollowArtist(){
    const unfollowArtistRequest =
        {
            "artistDiscogsId" : document.getElementById('discogsArtistId').innerText
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
