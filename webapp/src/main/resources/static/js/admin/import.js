let importJobTable;

/**
 * Called when the page is loaded
 */
$(document).ready(function () {
  importJobTable = getImportJobs();
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
      {"data": "totalCountImported"}
    ],
    "autoWidth": false, // fixes window resizing issue
    "columnDefs": [
      {
        "targets": [1, 2],
        "render": utcDateTimeToLocalDateTime
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
    success: function(){
      createToast("Import job successfully created!");
    },
    error: function(err){
      createToast("Error during creating the import job: " + err);
    }
  });
}
