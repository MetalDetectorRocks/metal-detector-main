let importJobTable;

/**
 * Called when the page is loaded
 */
$(document).ready(function () {
  importJobTable = getImportJobs();
  const importButton = document.getElementById("import-button");
  importButton.onclick = function () {createImportReleasesJob()};
  const retryCoverDownloadButton = document.getElementById("retry-cover-download-button");
  retryCoverDownloadButton.onclick = function () {createRetryCoverDownloadJob()};
});

/**
 * Request import jobs from REST endpoint via AJAX using DataTable jQuery Plugin.
 */
function getImportJobs() {
  $("#import-job-data tr").remove();

  return $("#import-job-table").DataTable({
    "ajax": {
      "url": "/rest/v1/releases/import",
      "type": "GET",
      "dataType": "json",
      "dataSrc": ""
    },
    "pagingType": "simple_numbers",
    "columns": [
      {"data": "source"},
      {"data": "startTime"},
      {"data": "endTime"},
      {"data": "durationInSeconds"},
      {"data": "totalCountRequested"},
      {"data": "totalCountImported"},
      {"data": "state"}
    ],
    "autoWidth": false, // fixes window resizing issue
    "order": [[ 1, "desc" ]],
    "columnDefs": [
      {
        "targets": [1, 2],
        "render": formatUtcDateTime
      },
      {
        "targets": [3],
        "render": function (durationInSeconds) {
          const minutes = Math.floor(durationInSeconds / 60);
          const seconds = durationInSeconds % 60;
          return `${minutes}:${('0' + seconds).slice(-2)}`;
        }
      },
      {
        "targets": [6],
        "render": function (state) {
          switch (state) {
            case "Running": return '<span class="badge badge-primary">Running</span>';
            case "Successful": return '<span class="badge badge-success">Successful</span>';
            case "Error": return '<span class="badge badge-danger">Error</span>';
          }
        }
      }
    ]
  });
}

/**
 * Creates an import job.
 * @returns {boolean}
 */
function createImportReleasesJob() {
  $.ajax({
    method: "POST",
    url: "/rest/v1/releases/import",
    success: function() {
      $("#confirm-import-dialog").modal("hide");
      createToast("Import job successfully created!");
    },
    error: function(err) {
      createToast("Error creating the import job: " + err);
    }
  });
}

/**
 * Creates a job for retrying cover downloads
 * @returns {boolean}
 */
function createRetryCoverDownloadJob() {
  $.ajax({
    method: "POST",
    url: "/rest/v1/releases/cover-reload",
    success: function() {
      $("#confirm-cover-retry-dialog").modal("hide");
      createToast("Job for retrying cover downloads successfully created!");
    },
    error: function(err) {
      createToast("Error creating the cover download job: " + err);
    }
  });
}
