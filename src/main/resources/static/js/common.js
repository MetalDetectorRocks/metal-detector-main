function registerLogoutListener() {
    document.getElementById('logout-link').addEventListener('click', function(event) {
        event.preventDefault();
        document.getElementById('logout-form').submit();
    });
}

function followingAction(publicUserId,artistName,artistId,el){
    if (el.innerText === "Follow") {
        followArtist(publicUserId, artistName, artistId, el);
    }
    else {
        unfollowArtist(publicUserId, artistName, artistId, el);
    }
}

function followArtist(publicUserId,artistName,artistId,el){
    const followArtistRequest =
        {
            "publicUserId" : publicUserId,
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
        },
        error: function(e){
            console.log(e.message);
        }
    });

    return false;
}

function unfollowArtist(publicUserId,artistName,artistId,el){
    const followArtistRequest =
        {
            "publicUserId" : publicUserId,
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
        },
        error: function(e){
            console.log(e.message);
        }
    });

    return false;
}