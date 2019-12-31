/**
 * Load all users on load or reload
 */
$(document).ready(function () {
    requestUsersFromServer();
    $("#create-user-button").button().on("click", createUser);
    $("#cancel-create-user-button").button().on("click", resetUserForm);
});

/**
 * Request users from REST endpoint via AJAX using DataTable jQuery Plugin.
 */
function requestUsersFromServer() {
    clearHtmlTable();

    // ToDo DanielW: Cell rendering
    $('#user-table').DataTable( {
        'ajax': {
            'url': '/rest/v1/users',
            'type': 'GET',
            'dataSrc': ''
        },
        'pagingType': 'simple_numbers',
        'columns': [
            {'data': 'username'},
            {'data': 'email'},
            {'data': 'roleAsHtml'},
            {'data': 'statusAsHtml'},
            {'data': 'lastLogin'},
            {'data': 'actionsAsHtml'}
        ],
        'columnDefs': [
            {
                'target': [5],
                'orderable': false
            }
        ]
    } );
}

/**
 * Removes all tr elements from the table body.
 */
function clearHtmlTable() {
    $("#user-data tr").remove();
}

/**
 * Creates a new administrator user.
 */
function createUser () {
    sendUserCreateRequest();
    resetUserForm();
    $('#admin-user-create-dialog').modal('hide');
}

/**
 * Create a new user on the server.
 */
function sendUserCreateRequest() {
    const user = {
        username: $("#username").val(),
        email: $("#email").val(),
        plainPassword: $("#plainPassword").val(),
        verifyPlainPassword: $("#verifyPlainPassword").val()
    };

    const successCallback = function () {
        requestUsersFromServer();
    };

    $.post({
        url: '/rest/v1/users',
        data: JSON.stringify(user),
        type: 'POST',
        contentType: 'application/json',
        success: successCallback
    });
}

/**
 * Reset the user create form
 */
function resetUserForm() {
    document.getElementById("create-admin-user-form").reset();
}
