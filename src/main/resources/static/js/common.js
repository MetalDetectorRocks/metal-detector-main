function registerLogoutListener() {
    document.getElementById('logout-link').addEventListener('click', function(event) {
        event.preventDefault();
        document.getElementById('logout-form').submit();
    });
}

function followArtist(artistId,index){
    const followArtistRequest =
        {
            "artistDiscogsId" : artistId
        };
    const followArtistRequestJson = JSON.stringify(followArtistRequest);
    const button = document.getElementById('followArtistButton' + index);

    const buttonText = button.innerText;
    if (buttonText==="Follow") {
        $.ajax({
            method: "POST",
            dataType: "json",
            url: "/rest/v1/follow-artist",
            contentType: 'application/json',
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
