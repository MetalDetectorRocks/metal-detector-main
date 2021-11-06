let userTable;

$(document).ready(function () {
  userTable = requestUsersFromServer();

  // create administrator
  $("#create-user-button").button().on("click", createAdministrator);
  $("#cancel-create-user-button").button().on("click", resetCreateUserForm);
  $("#create-admin-user-form-close").button().on("click", resetCreateUserForm);

  // update user
  $("#update-user-button").button().on("click", updateUser);
  $("#cancel-update-user-button").button().on("click", resetUpdateUserForm);
  $(document).on("click", "#user-table tbody tr", showUpdateUserForm);
  $("#update-user-form-close").button().on("click", resetUpdateUserForm);

  // cleanup users
  $("#cleanup-users-button").button().on("click", cleanupUsers);
});

/**
 * Request users from REST endpoint via AJAX using DataTable jQuery Plugin.
 */
function requestUsersFromServer() {
  clearHtmlTable();
  return $("#user-table").DataTable({
    ajax: {
      url: "/rest/v1/users",
      type: "GET",
      dataSrc: "",
    },
    pagingType: "simple_numbers",
    columns: [
      { data: "publicId" },
      { data: "username" },
      { data: "email" },
      { data: "nativeUser" },
      { data: "role" },
      { data: "enabled" },
      { data: "lastLogin" },
      { data: "createdDateTime" },
    ],
    autoWidth: false, // fixes window resizing issue
    columnDefs: [
      {
        targets: [0],
        visible: false,
      },
      {
        targets: 3,
        render: function (data) {
          if (data) {
            return '<span class="badge bg-primary">Native</span>';
          } else {
            return '<span class="badge bg-success">OAuth</span>';
          }
        },
      },
      {
        targets: 4,
        render: function (data) {
          if (data === "Administrator") {
            return '<span class="badge bg-danger">' + data + "</span>";
          } else {
            return '<span class="badge bg-info">' + data + "</span>";
          }
        },
      },
      {
        targets: 5,
        render: function (data) {
          if (data) {
            return '<span class="badge bg-success">Enabled</span>';
          } else {
            return '<span class="badge bg-secondary">Disabled</span>';
          }
        },
      },
      {
        targets: [6, 7],
        render: formatUtcDateTime,
      },
    ],
  });
}

/**
 * Removes all tr elements from the table body.
 */
function clearHtmlTable() {
  $("#user-data tr").remove();
}

/**
 * Create a new administrator on the server.
 */
function createAdministrator() {
  $.post({
    url: "/rest/v1/users",
    data: createAdministratorCreateRequest(),
    type: "POST",
    headers: {
      Accept: "application/json",
      "Content-Type": "application/json",
    },
    success: onCreateAdministratorSuccess,
    error: function (errorResponse) {
      onCreateError(errorResponse, "#create-admin-user-validation-area");
    },
  });
}

/**
 * Creates the json payload from html form to create a new administrator.
 * @returns {string} Stringified json payload to create a new administrator.
 */
function createAdministratorCreateRequest() {
  return JSON.stringify({
    username: $("#username").val(),
    email: $("#email").val(),
    plainPassword: $("#plain-password").val(),
    verifyPlainPassword: $("#verify-plain-password").val(),
  });
}

/**
 * Success callback for creating a new administrator.
 * @param createResponse The json response
 */
function onCreateAdministratorSuccess(createResponse) {
  userTable.row.add(createResponse).draw(false);
  resetCreateUserForm();
  $("#create-admin-user-dialog").modal("hide");
}

/**
 * Error callback for creating a new administrator.
 * @param errorResponse     The json response
 * @param validationAreaId  ID of the area to display errors (create)
 */
function onCreateError(errorResponse, validationAreaId) {
  onError(errorResponse, validationAreaId);
}

/**
 * Error callback for updating a user.
 * @param errorResponse     The json response
 * @param validationAreaId  ID of the area to display errors (update)
 */
function onUpdateError(errorResponse, validationAreaId) {
  onError(errorResponse, validationAreaId);

  $("#update-role").val("Administrator");
  $("#update-status").val("Enabled");
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

  if (errorResponse.status === 400) {
    // BAD REQUEST
    validationMessageArea.append(
      "The following errors occurred during server-side validation:"
    );
    const errorsList = $("<ul>", { class: "errors mb-0" }).append(
      errorResponse.responseJSON.messages.map((message) =>
        $("<li>").text(message)
      )
    );
    validationMessageArea.append(errorsList);
  } else if (errorResponse.status === 409) {
    // CONFLICT
    validationMessageArea.append(errorResponse.responseJSON.messages[0]);
  } else {
    validationMessageArea.append(
      "An unexpected error has occurred. Please try again at a later time."
    );
  }
}

/**
 * Creates the json payload from html form to update a user.
 * @returns {string} Stringified json payload to update a user.
 */
function createUpdateUserRequest() {
  return JSON.stringify({
    publicUserId: $("#update-public-id").text(),
    role: $("#update-role").val(),
    enabled: $("#update-status").val() === "Enabled",
  });
}

/**
 * Shows the update form and fills form with values from the selected user.
 */
function showUpdateUserForm() {
  let data = userTable.row(this).data();
  $("#update-user-dialog").modal("show");

  // master data
  $("#update-public-id").text(data.publicId);
  $("#update-username").text(data.username);
  $("#update-email").text(data.email);
  $("#update-type").text(data.nativeUser ? "Native" : "OAuth");
  $("#update-role").val(data.role);
  $("#update-status").val(data.enabled ? "Enabled" : "Disabled");

  // meta data
  $("#update-last-login").text(formatUtcDateTime(data.lastLogin));
  $("#update-created-by").text(data.createdBy);
  $("#update-created-date-time").text(formatUtcDateTime(data.createdDateTime));
  $("#update-last-modified-by").text(data.lastModifiedBy);
  $("#update-last-modified-date-time").text(
    formatUtcDateTime(data.lastModifiedDateTime)
  );
}

/**
 * Sends the update request to the server.
 */
function updateUser() {
  $.post({
    url: "/rest/v1/users",
    data: createUpdateUserRequest(),
    type: "PUT",
    headers: {
      Accept: "application/json",
      "Content-Type": "application/json",
    },
    success: onUpdateUserSuccess,
    error: function (errorResponse) {
      onUpdateError(errorResponse, "#update-user-validation-area");
    },
  });
}

/**
 * Success callback for updating a user.
 * @param updateResponse The json response
 */
function onUpdateUserSuccess(updateResponse) {
  userTable
    .rows()
    .every(function (rowIndex) {
      if (userTable.cell(rowIndex, 1).data() === updateResponse.username) {
        userTable.cell(rowIndex, 4).data(updateResponse.role);
        userTable.cell(rowIndex, 5).data(updateResponse.enabled);
      }
    })
    .draw();

  resetUpdateUserForm();
  $("#update-user-dialog").modal("hide");
}

/**
 * Resets the user creation form.
 */
function resetCreateUserForm() {
  $("#create-admin-user-form")[0].reset();
  resetValidationArea("#create-admin-user-validation-area");
}

/**
 * Resets the user update form.
 */
async function resetUpdateUserForm() {
  await sleep(500);
  $("#update-user-form")[0].reset();
  resetValidationArea("#update-user-validation-area");
}

/**
 * Sends the cleanup request to the server.
 */
function cleanupUsers() {
  $.post({
    url: "/rest/v1/cleanup",
    type: "POST",
    success: function () {
      $("#cleanup-users-dialog").modal("hide");
    },
    error: function (errorResponse) {
      onUpdateError(errorResponse, "#cleanup-users-validation-area");
    },
  });
}
