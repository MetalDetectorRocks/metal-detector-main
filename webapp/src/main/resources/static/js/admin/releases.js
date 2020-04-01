let releaseTable;

/**
 * Called when the page is loaded
 */
$(document).ready(function () {
  releaseTable = getReleases();

  $("#update-release-button").button().on("click", resetUpdateReleaseForm);
  $("#cancel-update-release-button").button().on("click", resetUpdateReleaseForm);
  $(document).on("click", "#releases-table tbody tr", showUpdateReleaseForm);
  $("#update-release-form-close").button().on("click", resetUpdateReleaseForm);
});

/**
 * Request releases from REST endpoint via AJAX using DataTable jQuery Plugin.
 */
function getReleases() {
  clearReleasesTable();

  return $("#releases-table").DataTable({
      "ajax": {
        "url": "/rest/v1/releases",
        "type": "POST",
        "dataType": "json",
        "contentType": "application/json",
        "data": function () {
          return JSON.stringify({"artists": []});
        },
        "dataSrc": ""
      },
      "pagingType": "simple_numbers",
      "columns": [
        {"data": "artist"},
        {"data": "additionalArtists"},
        {"data": "albumTitle"},
        {"data": "releaseDate"},
        {"data": "estimatedReleaseDate"}
      ],
      "autoWidth": false // fixes window resizing issue
  });
}

/**
 * Removes all tr elements from the table body.
 */
function clearReleasesTable() {
  $("#releases-data tr").remove();
}

/**
 * Shows the update form and fills form with values from the selected release.
 */
function showUpdateReleaseForm() {
  let data = releaseTable.row(this).data();
  $('#update-release-dialog').modal('show');

  // master data
  $('#artist').text(data.artist);
  $('#additionalArtist').text(data.additionalArtist);
  $('#albumTitle').text(data.albumTitle);
  $('#releaseDate').text(data.releaseDate);
  $('#estimatedReleaseDate').text(data.estimatedReleaseDate);
  $('#updateStatus').val(data.state);

  // details
  $('#genre').text(data.genre);
  $('#type').text(data.type);
  $('#source').text(data.source);

  const artistUrl = $('#metalArchivesArtistUrl');
  artistUrl.text(data.metalArchivesArtistUrl);
  artistUrl.attr("href", data.metalArchivesArtistUrl);
  const albumUrl = $('#metalArchivesAlbumUrl');
  albumUrl.text(data.metalArchivesAlbumUrl);
  albumUrl.attr("href", data.metalArchivesAlbumUrl);
}

/**
 * Resets the release update form.
 */
function resetUpdateReleaseForm() {
  $("#update-release-form")[0].reset();
  resetValidationArea('#update-release-validation-area');
}

function createExternalLink(url) {
  const link = document.createElement("a");
  link.href = url;
  link.text = "External Link";
  return link;
}