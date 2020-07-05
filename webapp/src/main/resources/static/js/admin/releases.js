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
        {"data": "albumTitle"},
        {"data": "releaseDate"},
        {"data": "genre"},
        {"data": "type"},
        {"data": "source"}
      ],
      "autoWidth": false, // fixes window resizing issue
      "columnDefs": [
        {
          "targets": [2],
          "render": utcDateToLocalDate
        }
      ]
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

  if (isEmpty(data.additionalArtist)) {
    $('#additional-artist-row').addClass("d-none");
  }
  else {
    $('#additional-artist-row').removeClass("d-none");
    $('#additional-artist').text(data.additionalArtist);
  }

  $('#album-title').text(data.albumTitle);
  $('#release-date').text(utcDateToLocalDate(data.releaseDate));
  $('#estimated-release-date').text(data.estimatedReleaseDate);
  $('#update-status').val(data.state);

  // details
  $('#genre').text(data.genre);
  $('#type').text(data.type);
  $('#source').text(data.source);

  const artistUrl = $('#metal-archives-artist-url');
  artistUrl.text(data.metalArchivesArtistUrl);
  artistUrl.attr("href", data.metalArchivesArtistUrl);
  const albumUrl = $('#metal-archives-album-url');
  albumUrl.text(data.metalArchivesAlbumUrl);
  albumUrl.attr("href", data.metalArchivesAlbumUrl);
}

/**
 * Resets the release update form.
 */
function resetUpdateReleaseForm() {
  $("#update-release-form")[0].reset();
  resetValidationArea('#release-validation-area');
}
