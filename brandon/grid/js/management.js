$(function () {
  $('select').selectpicker();

  $('.duration .progress').click(function (e) {
    var bar = $(this).find('.progress-bar');
    var percentage = Math.round(e.offsetX*100/bar.parent().width());
    bar.css({width:  percentage + "%"});
    $.post('/settings', {duration: percentage});
  });

  $('.instrument .progress').click(function (e) {
    var bar = $(this).find('.progress-bar');
    var percentage = Math.round(e.offsetX*100/bar.parent().width());
    var id = parseInt($(this).parents('.instrument').data('id'));
    bar.css({width:  percentage + "%"});
    $.post('/settings', {velocity: percentage, id: id});
  });

  $('.snapshot').click(function (e) {
    e.preventDefault();
    var name = $('.preset-name').val();
    $.post('/settings', {snapshot: name}, function() {
      location.reload();
    });
  });

  $('.load-preset').click(function (e) {
    e.preventDefault();
    var name = $('.preset-select').val();
    $.post('/settings', {preset: name}, function() {
      location.reload();
    });
  });

  $('.change-instrument').click(function (e) {
    var inst = $(this).parents('.instrument');
    var id = inst.data('id');
    var newID = inst.find('.instrument-select').val();
    $.post('/settings', {instrumentID: newID, id: id}, function(data) {
      inst.find('.instrument-name').text(data.newName);
    }, 'json');
  });
});
