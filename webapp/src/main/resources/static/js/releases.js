/**
 * Request releases from REST endpoint via AJAX using DataTable jQuery Plugin.
 */
function getReleases() {
  clearReleasesTable();

  const options = {year: 'numeric', month: '2-digit', day: '2-digit'};
  const dateFormatter = new Intl.DateTimeFormat('de-DE', options);

  $("#releases-table").DataTable({
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
      {"data": "estimatedReleaseDate"},
      {"data": "followed"}
    ],
    "columnDefs": [
      {
        "render": function (data) {
          if (data) {
            return "true";
          }
          else {
            return "false";
          }
        },
        "visible": false,
        "targets": 5
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
  function (settings, data) {
    const showOnlyFollowedArtists = document.getElementById("followed-artists-checkbox").checked;

    if (showOnlyFollowedArtists) {
      return data[5] === "true";
    }
    return true;
  }
);

/**
 *  Filtering function that will search for a date range
 */
$.fn.dataTable.ext.search.push(
  function (settings, data) {
    const dateFrom = document.getElementById("date-from").value;
    const dateTo = document.getElementById("date-to").value;

    if (dateFrom && dateTo) {
      return data[6] >= dateFrom && data[6] <= dateTo;
    }
    if (dateFrom) {
      return data[6] >= dateFrom;
    }
    else if (dateTo) {
      return data[6] <= dateTo;
    }

    return true;
  }
);

/**
 * Called when the page is loaded
 */
$(document).ready(function () {
  getReleases();

  const table = $("#releases-table").DataTable();
  $("#followed-artists-checkbox").change(function () {
    table.draw();
  });

  $("#date-from").change(function () {
    table.draw();
  });

  $("#date-to").change(function () {
    table.draw();
  });
});
