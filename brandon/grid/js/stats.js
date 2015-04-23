$(function() {
  $('.stats').on('webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend', '*', function() {
    var elem = $(this);
    if (elem.hasClass('pulse-big')) {
      elem.removeClass('animated pulse-big');
    }
  });

  if (window["WebSocket"]) {
      conn = new WebSocket("ws://" + host + "/ws");
      conn.onerror = function(evt) {
        console.log(evt);
      }
      conn.onclose = function(evt) {
        console.log(evt);
      }
      conn.onmessage = function(evt) { // Message received. evt.data is something
        // Parse the JSON out of the data
        var data = JSON.parse(evt.data);

        if (data.type != 'stat') {
          return;
        }

        $('.total-clicks').bounce(data.TotalClicks);
        $('.active-users').bounce(data.ActiveUsers);

        $('.run-average-resp').bounce(data.RunningAverage);
        $('.cum-average-resp').bounce(data.CumulativeAverage);

        for (var gridName in data.GridClicks) {
          if (data.GridClicks.hasOwnProperty(gridName)) {
            $('[data-name="' + gridName + '"]').bounce(data.GridClicks[gridName]);
          }
        }

      }
  } else {
      // Your browser does not support WebSockets
  }
});

jQuery.fn.extend({
  bounce: function (newData) {
    var elem = $(this[0]);
    var text = String(newData);
    // Only animate if we aren't currently, and it's a new value
    if (!elem.hasClass('pulse-big') && elem.text() != text) {
      elem.addClass('animated pulse-big');
    }
    elem.text(text);
  }
});
