var chart;

var seriesOptions = [
  { strokeStyle: 'rgba(255, 0, 0, 1)', fillStyle: 'rgba(255, 0, 0, 0.1)', lineWidth: 3 },
  { strokeStyle: 'rgba(0, 255, 0, 1)', fillStyle: 'rgba(0, 255, 0, 0.1)', lineWidth: 3 },
  { strokeStyle: 'rgba(0, 0, 255, 1)', fillStyle: 'rgba(0, 0, 255, 0.1)', lineWidth: 3 },
  { strokeStyle: 'rgba(255, 255, 0, 1)', fillStyle: 'rgba(255, 255, 0, 0.1)', lineWidth: 3 }
];

var dataSets = [new TimeSeries(), new TimeSeries(), new TimeSeries(), new TimeSeries()];

$(function() {
  initGraph();
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

        addData([getFloat(data.CumulativeAverage), getFloat(data.RunningAverage), getInt(data.Median)]);

        $('.total-clicks').bounce(data.TotalClicks);
        $('.active-users').bounce(data.ActiveUsers);

        $('.run-average-resp').bounce(data.RunningAverage);
        $('.cum-average-resp').bounce(data.CumulativeAverage);

        $('.median-resp').bounce(data.Median);
        $('.mode-resp').bounce(data.Mode);

        $('.max-resp').bounce(data.Max);
        $('.min-resp').bounce(data.Min);

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

function initGraph() {
  $('#chart').attr('width', $('.chart-holder').width());

  // Build the timeline
  var timeline = new SmoothieChart({ millisPerPixel: 20, grid: { strokeStyle: '#555555', lineWidth: 1, millisPerLine: 1000, verticalSections: 4 }});
  for (var i = 0; i < dataSets.length; i++) {
    timeline.addTimeSeries(dataSets[i], seriesOptions[i]);
  }
  timeline.streamTo($('#chart').get(0), 1000);

  $(window).resize(function() {
    $('#chart').attr('width', $('.chart-holder').width());
  });
}

function addData(data) {
  for (var i = 0; i < dataSets.length; i++) {
    dataSets[i].append(new Date().getTime(), data[i]);
  }
}

function getInt(str) {
  return parseInt(str.split(" ")[0]);
}

function getFloat(str) {
  return parseFloat(str.split(" ")[0]);
}
