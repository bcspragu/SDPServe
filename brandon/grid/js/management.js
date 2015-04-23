var id;
var newHTML;

$(function () {
  id = makeID();
  $('select').selectpicker();

  $('.management').on('webkitTransitionEnd transitionend msTransitionEnd oTransitionEnd', '.progress-bar', function() {
    reloadManagement(newHTML); });

  $('.management').on('click', '.master-volume .progress', function (e) {
    var bar = $(this).find('.progress-bar');
    var percentage = Math.round(e.offsetX*100/bar.parent().width());
    bar.css({width:  percentage + "%"});
    $.post('/settings', {master: percentage, id: id}, function (data) {
      newHTML = data;
    });
  });

  $('.management').on('click', '.duration .progress', function (e) {
    var bar = $(this).find('.progress-bar');
    var percentage = Math.round(e.offsetX*100/bar.parent().width());
    bar.css({width:  percentage + "%"});
    $.post('/settings', {duration: percentage, id: id}, function (data) {
      newHTML = data;
    });
  });

  $('.management').on('click', '.instrument .progress', function (e) {
    var bar = $(this).find('.progress-bar');
    var percentage = Math.round(e.offsetX*100/bar.parent().width());
    var id = parseInt($(this).parents('.instrument').data('id'));
    bar.css({width:  percentage + "%"});
    $.post('/settings', {velocity: percentage, vID: id}, function (data) {
      newHTML = data;
    });
  });

  $('.management').on('click', '.snapshot', function (e) {
    e.preventDefault();
    var name = $('.preset-name').val();
    $.post('/settings', {snapshot: name, id: id}, function(data) {
      reloadManagement(data);
    });
  });

  $('.management').on('click', '.load-preset', function (e) {
    e.preventDefault();
    var name = $('.preset-select').val();
    $.post('/settings', {preset: name, id: id}, function(data) {
      reloadManagement(data);
    });
  });

  $('.management').on('change', '.instrument-select', function (e) {
    var inst = $(this).parents('.instrument');
    var oldID = inst.data('id');
    var newID = inst.find('.instrument-select').val();
    $.post('/settings', {instrumentID: newID, oldID: oldID, id: id}, function(data) {
      reloadManagement(data);
    });
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

        if (data.type != 'management') {
          return;
        }

        if (data.id != id) {
          $('.alert').removeClass('hide').text("Warning: Another user is changing settings in the management console, please reload the page.");
        }

      }
  } else {
      // Your browser does not support WebSockets
  }
});

function reloadManagement(data) {
  $('.alert').addClass('hide');
  $('.management').html(data);
  $('select').selectpicker();
}

function makeID () {
  var text = "";
  var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

  for( var i=0; i < 5; i++ ) {
    text += possible.charAt(Math.floor(Math.random() * possible.length));
  }

  return text;
}
