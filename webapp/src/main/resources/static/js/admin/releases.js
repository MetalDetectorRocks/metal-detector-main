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
        {"data": "state"},
        {"data": "artist"},
        {"data": "albumTitle"},
        {"data": "releaseDate"},
        {"data": "genre"},
        {"data": "type"},
        {"data": "source"},
        {"data": "id"}
      ],
      "autoWidth": false, // fixes window resizing issue
      "order": [[ 3, "asc" ], [1, "asc"]],
      "columnDefs": [
        {
          "targets": [0],
          "render": function (data) {
            if (data === 'Ok') {
              return '<span class="badge bg-success">' + data + '</span>';
            }
            else if (data === 'Demo') {
              return '<span class="badge bg-warning">' + data + '</span>';
            }
            else if (data === "Faulty") {
              return '<span class="badge bg-danger">' + data + '</span>';
            }
            else {
              return '<span class="badge bg-info">' + data + '</span>';
            }
          }
        },
        {
          "targets": [3],
          "render": formatUtcDate
        },
        {
          "targets": [4, 5],
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
          "targets": [7],
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
  $('#current-row-index').text(releaseTable.row(this).index());

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

  // cover
  $('#cover').attr("src", data.coverUrl);

  // details
  $('#genre').text(data.genre);
  $('#type').text(data.type);
  $('#source').text(data.source);

  const artistUrl = $('#artist-details');
  artistUrl.text(`Go to ${data.source}`);
  artistUrl.attr("href", data.artistDetailsUrl);
  const albumUrl = $('#release-details');
  albumUrl.text(`Go to ${data.source}`);
  albumUrl.attr("href", data.releaseDetailsUrl);
}

/**
 * Resets the release update form.
 */
async function resetUpdateReleaseForm() {
  await sleep(500);
  $("#update-release-form")[0].reset();
  resetValidationArea('#update-release-validation-area');
}

/**
 * Updates a release's state
 */
function updateRelease() {
  const pathParam = $("#release-id").val();
  $.ajax({
    url: '/rest/v1/releases/' + pathParam,
    data: createUpdateReleaseRequest(),
    type: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    success: onUpdateReleaseSuccess,
    error: function (errorResponse) {
      onError(errorResponse, '#update-release-validation-area')
    }
  });
}

/**
 * Creates the json payload from html form to update a release's state.
 * @returns {string} Stringified json payload to update a release's state.
 */
function createUpdateReleaseRequest() {
  return JSON.stringify({
    state: $("#release-state").val()
  });
}

/**
 * Success callback for updating a release's state.
 */
function onUpdateReleaseSuccess() {
  const currentRowIndex = parseInt($('#current-row-index').text());
  releaseTable.cell(currentRowIndex, 0).data($("#release-state").val()).draw();

  resetUpdateReleaseForm();
  $('#update-release-dialog').modal("hide");
}

/**
 * Error callback.
 * @param errorResponse     The json response
 * @param validationAreaId  ID of the area to display errors (create/update)
 */
function onError(errorResponse, validationAreaId) {
  resetValidationArea(validationAreaId);
  const validationMessageArea = $(validationAreaId);
  validationMessageArea.addClass("alert alert-danger");

  if (errorResponse.status === 400) { // BAD REQUEST
    validationMessageArea.append("The following errors occurred during server-side validation:");
    const errorsList = $('<ul>', {class: "errors mb-0"}).append(
      errorResponse.responseJSON.messages.map(message =>
        $("<li>").text(message)
      )
    );
    validationMessageArea.append(errorsList);
  }
  else {
    validationMessageArea.append("An unexpected error has occurred. Please try again at a later time.");
  }
}
