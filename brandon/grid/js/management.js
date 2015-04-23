var newHTML;
$(function () {
  $('select').selectpicker();

  $('.management').on('webkitTransitionEnd transitionend msTransitionEnd oTransitionEnd', '.progress-bar', function() {
    reloadManagement(newHTML); });

  $('.management').on('click', '.master-volume .progress', function (e) {
    var bar = $(this).find('.progress-bar');
    var percentage = Math.round(e.offsetX*100/bar.parent().width());
    bar.css({width:  percentage + "%"});
    $.post('/settings', {master: percentage}, function (data) {
      newHTML = data;
    });
  });

  $('.management').on('click', '.duration .progress', function (e) {
    var bar = $(this).find('.progress-bar');
    var percentage = Math.round(e.offsetX*100/bar.parent().width());
    bar.css({width:  percentage + "%"});
    $.post('/settings', {duration: percentage}, function (data) {
      newHTML = data;
    });
  });

  $('.management').on('click', '.instrument .progress', function (e) {
    var bar = $(this).find('.progress-bar');
    var percentage = Math.round(e.offsetX*100/bar.parent().width());
    var id = parseInt($(this).parents('.instrument').data('id'));
    bar.css({width:  percentage + "%"});
    $.post('/settings', {velocity: percentage, id: id}, function (data) {
      newHTML = data;
    });
  });

  $('.management').on('click', '.snapshot', function (e) {
    e.preventDefault();
    var name = $('.preset-name').val();
    $.post('/settings', {snapshot: name}, function(data) {
      reloadManagement(data);
    });
  });

  $('.management').on('click', '.load-preset', function (e) {
    e.preventDefault();
    var name = $('.preset-select').val();
    $.post('/settings', {preset: name}, function(data) {
      reloadManagement(data);
    });
  });

  $('.management').on('change', '.instrument-select', function (e) {
    var inst = $(this).parents('.instrument');
    var id = inst.data('id');
    var newID = inst.find('.instrument-select').val();
    $.post('/settings', {instrumentID: newID, id: id}, function(data) {
      reloadManagement(data);
    });
  });
});

function reloadManagement(data) {
  console.log("relaoding");
  $('.management').html(data);
  $('select').selectpicker();
}
