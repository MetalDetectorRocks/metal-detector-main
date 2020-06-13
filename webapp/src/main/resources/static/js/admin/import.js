/**
 * Called when the page is loaded
 */
$(document).ready(function () {
  // ToDo DanielW: Load past import job results
});

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
