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
    $.post('/settings', {snapshot: name});
  });

  $('.change-instrument').click(function (e) {
    var id = $(this).parents('.instrument').data('id');
    var newID = $(this).parents('.instrument').find('.instrument-select').val();
    $.post('/settings', {instrumentID: newID, id: id});
  });
});
