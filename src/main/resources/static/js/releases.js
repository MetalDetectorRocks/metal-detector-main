/**
 * Request users from REST endpoint via AJAX using DataTable jQuery Plugin.
 */
function getReleases() {
  clearReleasesTable();

  const request = {"artists": []};

  const requestString = JSON.stringify(request);

  $("#releases-table").DataTable( {
    "ajax": {
      "url": "/rest/v1/releases",
      "type": "POST",
      "dataType": "json",
      "contentType": "application/json",
      "data": function ( d ) {
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
      {"data": "estimatedReleaseDate"},
      {"data": "isFollowed"}
    ],
    "columnDefs": [
      {
        "targets": [5],
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
 *  Filtering function that will search followed artists
 */
$.fn.dataTable.ext.search.push(
  function( settings, data ) {
    const showOnlyFollowedArtists = document.getElementById("followed-artists-checkbox").checked;

    if (showOnlyFollowedArtists) {
      return data[5] === "true";
    }
    return true;
  }
);
