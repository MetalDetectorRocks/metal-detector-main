let releaseTable;

/**
 * Called when the page is loaded
 */
$(document).ready(function () {
  releaseTable = getReleases();

  $("#update-release-button").button().on("click", updateRelease);
  $("#cancel-update-release-button").button().on("click", resetUpdateReleaseForm);
  $(document).on("click", "#releases-table tbody tr", showUpdateReleaseForm);
  $("#update-release-form-close").button().on("click", resetUpdateReleaseForm);
});

/**
 * Request releases from REST endpoint via AJAX using DataTable jQuery Plugin.
 */
function getReleases() {
  clearReleasesTable();

  let dateFrom = new Date();
  dateFrom.setDate(dateFrom.getDate() - 90);
  return $("#releases-table").DataTable({
      "ajax": {
        "url": `/rest/v1/releases/all?dateFrom=${formatUtcDate(dateFrom)}`,
        "type": "GET",
        "dataType": "json",
        "contentType": "application/json",
        "dataSrc": ""
      },
      "pagingType": "simple_numbers",
      "columns": [
        {"data": "artist"},
        {"data": "albumTitle"},
        {"data": "releaseDate"},
        {"data": "genre"},
        {"data": "type"},
        {"data": "source"},
        {"data": "state"},
        {"data": "id"}
      ],
      "autoWidth": false, // fixes window resizing issue
      "order": [[ 2, "asc" ], [0, "asc"]],
      "columnDefs": [
        {
          "targets": [2],
          "render": formatUtcDate
        },
        {
          "targets": [3, 4],
          "render": function (data) {
            if (isEmpty(data)) {
              return 'n/a';
            }
            else {
              return data;
            }
          }
        },
        {
          "targets": [6, 7],
          "visible": false
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
  $('#release-id').val(data.id);
  $('#artist').text(data.artist);

  if (isEmpty(data.additionalArtist)) {
    $('#additional-artist-row').addClass("d-none");
  }
  else {
    $('#additional-artist-row').removeClass("d-none");
    $('#additional-artist').text(data.additionalArtist);
  }

  $('#album-title').text(data.albumTitle);
  $('#release-date').text(formatUtcDate(data.releaseDate));
  $('#estimated-release-date').text(data.estimatedReleaseDate);
  $('#release-state').val(data.state);

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

/**
 * Updates a release's state
 */
function updateRelease() {
  $.post({
    url: '/rest/v1/release/update',
    data: createUpdateReleaseRequest(),
    type: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    success: onUpdateReleaseSuccess,
    error: function (errorResponse) {
      onUpdateError(errorResponse, '#update-release-validation-area')
    }
  });
}

/**
 * Creates the json payload from html form to update a release's state.
 * @returns {string} Stringified json payload to update a release's state.
 */
function createUpdateReleaseRequest() {
  return JSON.stringify({
    releaseId: $("#release-id").val(),
    state: $("#release-state").val()
  });
}

/**
 * Success callback for updating a release's state.
 */
function onUpdateReleaseSuccess() {
  releaseTable.rows().every(function (rowIndex) {
    if (releaseTable.cell(rowIndex, 7).data() === parseInt($("#release-id").val())) {
      releaseTable.cell(rowIndex, 6).data($('#release-state').val());
    }
  }).draw();

  resetUpdateReleaseForm();
  $('#update-release-dialog').modal('hide');
}
