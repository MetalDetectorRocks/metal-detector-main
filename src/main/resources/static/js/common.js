function registerLogoutListener() {
    document.getElementById('logout-link').addEventListener('click', function(event) {
        event.preventDefault();
        document.getElementById('logout-form').submit();
    });
}

function followArtist(publicUserId,artistId,index){
    const followArtistRequest =
        {
            "publicUserId" : publicUserId,
            "artistDiscogsId" : artistId
        };
    const followArtistRequestJson = JSON.stringify(followArtistRequest);
    const button     = document.getElementById('followArtistButton' + index);
    const buttonText = button.innerText;
    const csrfToken  = $("input[name='_csrf']").val();

    if (buttonText==="Follow") {
        $.ajax({
            method: "POST",
            url: "/rest/v1/follow-artist",
            contentType: 'application/json',
            headers: {"X-CSRF-TOKEN": csrfToken},
            data: followArtistRequestJson,
            success: function(){
                button.childNodes[0].nodeValue = 'Unfollow';
            },
            error: function(e){
                console.log(e.message);
            }
        });
    }
    else {
        $.ajax({
            method: "DELETE",
            url: "/rest/v1/follow-artist",
            contentType: 'application/json',
            data: followArtistRequestJson,
            headers: {"X-CSRF-TOKEN": csrfToken},
            success: function(){
                button.childNodes[0].nodeValue = 'Follow';
            },
            error: function(e){
                console.log(e.message);
            }
        });
    }

    return false;
}
