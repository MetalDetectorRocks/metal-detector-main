/**
 * Load all users on load or reload
 */
$(document).ready(function () {
    let userTable = requestUsersFromServer();

    // ToDo DanielW: move in separate method
    $(document).on("click", "#user-table tbody tr", function() {
        let data = userTable.row( this ).data();
        $('#update-user-dialog').modal('show');
    });

    // create form
    $("#create-user-button").button().on("click", createUser);
    $("#cancel-create-user-button").button().on("click", resetCreateUserForm);

    // update form
    $("#update-user-button").button().on("click", updateUser);
    $("#cancel-update-user-button").button().on("click", resetUpdateUserForm);
});

/**
 * Request users from REST endpoint via AJAX using DataTable jQuery Plugin.
 */
function requestUsersFromServer() {
    clearHtmlTable();
    return $('#user-table').DataTable({
        'ajax': {
            'url': '/rest/v1/users',
            'type': 'GET',
            'dataSrc': ''
        },
        'pagingType': 'simple_numbers',
        'columns': [
            {'data': 'publicId'},
            {'data': 'username'},
            {'data': 'email'},
            {'data': 'role'},
            {'data': 'enabled'},
            {'data': 'lastLogin'},
            {'data': 'creationDate'}
        ],
        "columnDefs": [
            {
                "render": function (data) {
                    if (data === 'Administrator') {
                        return '<span class="badge badge-danger">' + data + '</span>';
                    }
                    else {
                        return '<span class="badge badge-info">' + data + '</span>';
                    }
                },
                "targets": 3
            },
            {
                "render": function (data) {
                    if (data) {
                        return '<span class="badge badge-success">Enabled</span>';
                    }
                    else {
                        return '<span class="badge badge-secondary">Disabled</span>';
                    }
                },
                "targets": 4
            },
            {
                "targets": [0],
                "visible": false
            }
        ]
    });
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
    sendCreateUserRequest();
    resetCreateUserForm();
    $('#create-admin-user-dialog').modal('hide');
}

/**
 * Updates certain user.
 */
function updateUser () {
    sendUpdateUserRequest();
    resetUpdateUserForm();
    $('#update-user-dialog').modal('hide');
}

/**
 * Create a new user on the server.
 */
function sendCreateUserRequest() {
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
 * Update a certain user on the server.
 */
function sendUpdateUserRequest() {
    alert("ToDo"); // ToDo DanielW: Implement
}

/**
 * Reset the user create form
 */
function resetCreateUserForm() {
    $("#create-admin-user-form")[0].reset();
}

/**
 * Reset the user update form
 */
function resetUpdateUserForm() {
    $("#update-user-form")[0].reset();
}
