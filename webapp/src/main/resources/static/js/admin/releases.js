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
        "data": function (d) {
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
  $('#artist').val(data.artist);
  $('#additionalArtist').val(data.additionalArtist);
  $('#albumTitle').val(data.albumTitle);
  $('#releaseDate').val(data.releaseDate);
  $('#estimatedReleaseDate').val(data.estimatedReleaseDate);
  $('#status').val(data.status);

  // details
  $('#genre').val(data.genre);
  $('#type').val(data.type);
  $('#metalArchivesArtistUrl').val(data.metalArchivesArtistUrl);
  $('#metalArchivesAlbumUrl').val(data.metalArchivesAlbumUrl);
  $('#source').val(data.source);
  $('#state').val(data.state);
}

/**
 * Resets the release update form.
 */
function resetUpdateReleaseForm() {
  $("#update-release-form")[0].reset();
  resetValidationArea('#update-release-validation-area');
}
