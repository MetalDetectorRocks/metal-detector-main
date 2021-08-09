$(document).ready(function () {
  const frequencyNotificationButton = document.getElementById(
    "frequency-notification-button"
  );
  frequencyNotificationButton.onclick = function () {
    sendNotification("frequency");
  };
  const releaseDateNotificationButton = document.getElementById(
    "release-date-notification-button"
  );
  releaseDateNotificationButton.onclick = function () {
    sendNotification("release-date");
  };
  const announcementDateNotificationButton = document.getElementById(
    "announcement-date-notification-button"
  );
  announcementDateNotificationButton.onclick = function () {
    sendNotification("announcement-date");
  };
});

function sendNotification(endpoint) {
  $.ajax({
    method: "POST",
    url: "/rest/v1/notify/" + endpoint,
    success: function () {
      createToast("Notification successfully sent!");
    },
    error: function (err) {
      createToast(`Error sending notification (Status ${err.status}).`);
    },
  });
}
