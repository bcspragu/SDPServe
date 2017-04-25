// jQuery objects holding our various grids
var mainGrid;
// Holder for each of the minigrids
var miniGrids;

// Websocket object
var conn;

// Number of milliseconds for fade animation
var fadeTime = 100;

var id;

var dragging = false;
var lastElement = null;
var mode = false;

$(function () {
  id = makeID();
  mainGrid = $('.main-grid');
  miniGrids = $('.mini-grids');

  $(window).resize(resizeGrids);

  initWebsockets();

  // When we click on the main grid, we want to update the grid locally and on the server
  $('.main-grid').on('mousedown', '.cell', function (e) {
    e.preventDefault();
    var cell = realCell($(this));

    mode = !cell.hasClass('active');
    lastElement = cell;
    cell.cellTrigger();
  });

  $('.main-grid').on('mouseover', '.cell', function (e) {
    e.preventDefault();
    if (dragging) {
      realCell($(this)).cellTrigger();
    }
  });

  $(document).mousedown(function (e) {
    e.stopPropagation();
    e.preventDefault();
    dragging = true;
  }).mouseup(function (e) {
    e.stopPropagation();
    e.preventDefault();
    dragging = false;
  });


  $('.main-grid, .mini-grids').on('webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend', '.cell', function() {
      $(this).removeClass('animated pulse');
  });

  $('body').on('webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend', '.label', function() {
      $(this).remove();
  });

  $('.main-grid').on('touchmove', '.cell', function (e) {
    e.stopPropagation();
    e.preventDefault();
    var touches = e.originalEvent.changedTouches;
    for (var i = 0; i < touches.length; i++) {
      var x = touches[i].pageX;
      var y = touches[i].pageY;
      var touch = realCell($(document.elementFromPoint(x,y)));

      // If we didn't click on a cell, ignore it
      if (touch != null && !touch.is(lastElement) && touch.parents('.main-grid').length > 0) {
        lastElement = touch;
        touch.cellTrigger();
      }
    }
  });

  $('.mini-grids').on('mousedown', '.mini-grid', function () {
    // Switch this minigrid with the full-size grid
    swapGrids($(this), mainGrid);
  });

  // Load up all of the grids at the start
  loadGrids(startGrid);
  initTouch();
  setInterval(sync, 2500);
});

jQuery.fn.extend({
  // Given a name and 2D array of booleans, initialize a DOM element as a
  // grid
  loadGrid: function (gridName, displayName, gridData) {
    // Selector should only have a single element
    var gridHolder = $(this[0]);
    gridHolder.empty();
    // Store the name of the grid on the holder
    gridHolder.data("name", gridName);
    gridHolder.data("display-name", displayName);
    gridHolder.addClass(nameToClass(gridName));
    
    // We need the length, width, and x/y sizes to compute the size of each
    // individual cell
    var width = gridHolder.width();
    var height = gridHolder.height();

    var xSize = gridData.length;
    var ySize = gridData[0].length;

    for (var i = 0; i < ySize; i++) {
      // Create a new row, add it to the DOM, size it appropriately
      var row = $('<div class="row"></div>');
      gridHolder.append(row);

      for (var j = 0; j < xSize; j++) {
        // Create a new cell, add it to the DOM row, size it appropriately
        var cell = $('<div class="cell"><div class="cell-inner"></div></div>');
        row.append(cell);
        // If this cell is active, make it active
        if (gridData[j][i]) {
          cell.addClass('active', fadeTime);
        }
      }
    }
    gridHolder.resizeCells();
  },
  // Set the status of a block on a given grid
  setBlock: function (x, y, on) {
    var gridHolder = $(this[0]);

    // We can locate the cell with the jQuery equals selector, the cell we
    // want is the zero-indexed xth column and yth row
    var cell = gridHolder
                 .find('.row:eq(' + y + ')')
                 .find('.cell:eq(' + x + ')');

   // Turn the cell on or off
   if (on) {
     cell.addClass('active', fadeTime);
   } else {
     cell.removeClass('active', fadeTime);
   }

   // Pulse
   if (!cell.hasClass('pulse')) {
     cell.addClass('animated pulse');
   }
  },
  // Sets the size of the cells relative to the size of the given grid
  resizeCells: function () {
    var gridHolder = $(this[0]);
    gridHolder.find('.label').remove();

    var label = $('<div class="label animated-slow fadeOut"></div>');
    label.text(gridHolder.data('display-name'));
    gridHolder.append(label);

    var width = gridHolder.width();
    var height = gridHolder.height();

    // The number of rows is the number of divs with the class row
    var rowCount = gridHolder.find('.row').length;
    // The number of cells per row should be the same in a given grid, so we
    // find the number of divs with the class cell in the first row we find
    var cellCount = gridHolder.find('.row:first > .cell').length;

    gridHolder.find('.row').height(Math.floor(height/rowCount));
    gridHolder.find('.cell').width(Math.floor(width/cellCount));
  },
  // Gets cell parent grid and coordinates and state, then sends it to server
  cellTrigger: function () {
    var cell = $(this[0]);

    if (mode) {
      cell.addClass('active', fadeTime);
    } else {
      cell.removeClass('active', fadeTime);
    }
    cell.addClass('animated pulse');

    // Our x location is our index in the row
    var xLoc = cell.index();
    // Our y location is our row's index in the grid
    var yLoc = cell.parents('.row').index();
    var name = cell.parents('.grid').data('name');

    // Build the message from various DOM attributes
    var message = JSON.stringify({type: 'tap', on: mode, x: xLoc, y: yLoc, name: name, id: id, sent: Date.now()});
    // Send the message to the server via WebSockets
    conn.send(message);
  }
});

function realCell(cell) {
  if (cell.hasClass('cell-inner')) {
    return cell.parents('.cell');
  } else if (cell.hasClass('cell')) {
    return cell;
  }
  return null;
}

// Initialize our grids. The first grid takes up the large main grid area, and all subsequent grids get spread out along the bottom
function loadGrids(gridData) {
  var isNew = false;
  if (mainGrid.is(':empty')) {
    isNew = true;
  }

  // Start at -1 because the first one goes in the main grid
  var miniGridCount = -1;
  var grids = gridData.Grids;
  var instruments = gridData.Instruments;
  
  for (var gridName in grids) {
    if (grids.hasOwnProperty(gridName)) {
      miniGridCount++;
    }
  }

  var isMainGrid = true;
  for (var gridName in grids) {
    var grid = grids[gridName];
    var displayName = instruments[grid.Index].Name;
    if (grids.hasOwnProperty(gridName)) {
      if (isNew) {
        // Fill the main grid first
        if (isMainGrid) {
          isMainGrid = false;
          mainGrid.loadGrid(gridName, displayName, grids[gridName].Grid);
        } else {
          var miniGrid = $('<div class="mini-grid grid"></div>');
          miniGrids.append(miniGrid);
          miniGrid.width(Math.floor(miniGrids.width()/miniGridCount));
          miniGrid.loadGrid(gridName, displayName, grids[gridName].Grid);
        }
      } else {
          var displayName = instruments[grid.Index].Name;
          $(nameAsCssClass(gridName)).loadGrid(gridName, displayName, grids[gridName].Grid);
      }
    }
  }
}

function initWebsockets() {
  // Establish a WebSocket connection
  if (window["WebSocket"]) {
      conn = new WebSocket("wss://" + host + "/ws");
      conn.onerror = function(evt) {
        console.log(evt);
      }
      conn.onclose = function(evt) {
        console.log(evt);
      }
      conn.onmessage = function(evt) { // Message received. evt.data is something
        // Parse the JSON out of the data
        var data = JSON.parse(evt.data);

        if (data.type == 'tap') {
          if (data.id == id) {
            var requestTime = (Date.now() - data.sent);
            $.post('/time', {requestDuration: requestTime});
          }
          // Select our grid by the name passed to us
          var grid = $(nameAsCssClass(data.name));
          // Use the other attributes to figure out which cell to set
          grid.setBlock(data.x, data.y, data.on);
        } else if (data.type == 'preset') {
          loadGrids(data.GridData);
        }
      }
  } else {
      // Your browser does not support WebSockets
  }
}

// Resize all our grids to fill the new space. We first resize each of the
// minigrids to fill their allocated space, CSS does the rest. Then we go
// through each grid and make sure each row fills it's allocated height, and
// each cell fills it's allocated width.
function resizeGrids() {
  // Grab all individual minigrids
  var allMiniGrids = $('.mini-grid');

  // The width of each minigrid is the space allocated for all of the
  // minigrids divided by how many minigrids there are
  allMiniGrids.width(Math.floor(miniGrids.width() / allMiniGrids.length));
  
  $('.grid').each(function () {
    var gridHolder = $(this);
    gridHolder.resizeCells();
  });
}

// A helper function for turning our instrument names into classes
function nameToClass(name) {
  return name.replace(" ", "-") + "-grid";
}

function nameAsCssClass(name) {
  return "." + name.replace(" ", "-") + "-grid";
}

// Switch out the grids
function swapGrids(grid1, grid2) {
  var name1 = grid1.data('name');
  var name2 = grid2.data('name');

  var dName1 = grid1.data('display-name');
  var dName2 = grid2.data('display-name');

  grid1.removeClass(nameToClass(name1));
  grid2.removeClass(nameToClass(name2));

  grid1.addClass(nameToClass(name2));
  grid2.addClass(nameToClass(name1));

  grid1.data('name', name2);
  grid2.data('name', name1);

  grid1.data('display-name', dName2);
  grid2.data('display-name', dName1);

  var html1 = grid1.html();
  grid1.html(grid2.html());
  grid2.html(html1);

  resizeGrids();
}

function makeID () {
  var text = "";
  var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

  for( var i=0; i < 5; i++ ) {
    text += possible.charAt(Math.floor(Math.random() * possible.length));
  }

  return text;
}

function touchHandler(event) {
    var touch = event.changedTouches[0];

    var simulatedEvent = document.createEvent("MouseEvent");
        simulatedEvent.initMouseEvent({
        touchstart: "mousedown",
        touchmove: "mousemove",
        touchend: "mouseup"
    }[event.type], true, true, window, 1,
        touch.screenX, touch.screenY,
        touch.clientX, touch.clientY, false,
        false, false, false, 0, null);

    touch.target.dispatchEvent(simulatedEvent);
    event.preventDefault();
}

function initTouch() {
    document.addEventListener("touchstart", touchHandler, true);
    document.addEventListener("touchmove", touchHandler, true);
    document.addEventListener("touchend", touchHandler, true);
    document.addEventListener("touchcancel", touchHandler, true);
}

function sync() {
  $.get('/state', function(data) {
    var state = getState();
    if (data == state) {
      // Do nothing, we're good
    } else {
      // Resync game state from server
      setState(state.split(","), data.split(","));
    }
  });
}

function getState() {
  var names = [];
  $('.grid').each(function() {
    names.push($(this).data('name'));
  });
  names.sort();

  var str = "";
  for (var i = 0; i < names.length; i++) {
    var grid = $(nameAsCssClass(names[i]));
    grid.find('.row').each(function() {
      $(this).find('.cell').each(function() {
        if ($(this).hasClass('active')) {
          str += "1";
        } else {
          str += "0";
        }
      });
    });
    if (i != names.length - 1) {
      str += ",";
    }
  }
  return str;
}

function setState(current, actual) {
  var names = [];
  $('.grid').each(function() {
    names.push($(this).data('name'));
  });
  names.sort();

  for (var i = 0; i < names.length; i++) {
    var grid = $(nameAsCssClass(names[i]));
    var index = 0;
    grid.find('.row').each(function() {
      $(this).find('.cell').each(function() {
        // If the states don't match
        if (current[i][index] != actual[i][index]) {
          if (actual[i][index] == "1") {
            $(this).addClass("active");
          } else {
            $(this).removeClass("active");
          }
          $(this).addClass("animated pulse");
        }
        index++;
      });
    });
  }
}
