$(function () {
  $('.progress').click(function (e) {
    var bar = $(this).find('.progress-bar');
    var percentage = e.offsetX*100/bar.parent().width();
    bar.css({width:  percentage + "%"});
    $.post('/settings', {duration: percentage});
  });

  $('.snapshot').click(function (e) {
    e.preventDefault();
    var name = $('.preset-name').val();
    $.post('/settings', {snapshot: name});
  });
});
