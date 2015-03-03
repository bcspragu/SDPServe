// jQuery objects holding our various grids
var mainGrid;
// Holder for each of the minigrids
var miniGrids;

// Websocket object
var conn;

// Number of milliseconds for fade animation
var fadeTime = 100;

var miniMargin = 2;
var mainMargin = 6;

var id;

var requestCount = 0;
var requestTime = 0;

$(function () {
  id = makeID();
  mainGrid = $('.main-grid');
  miniGrids = $('.mini-grids');

  $(window).resize(resizeGrids);

  initWebsockets();

  // When we click on the main grid, we want to update the grid locally and on the server
  $('.main-grid').on('click', '.cell', function () {
    $(this).ripple();
    $(this).cellTrigger();
  });


  $('.main-grid, .mini-grids').on('webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend', '.cell', function() {
    var cell = $(this);
    if (cell.hasClass('pulse')) {
      cell.removeClass('animated pulse');
    } else if (cell.hasClass('fadeOut')) {
      cell.remove();
    }
  });

  $('.main-grid').on('touchstart', function (evt) {
    evt.preventDefault();
    var touches = evt.originalEvent.changedTouches;
    for (var i = 0; i < touches.length; i++) {
      var touch = $(touches[i].target);

      // If we didn't click on a cell, ignore it
      if (touch.hasClass('cell')) {
        touch.cellTrigger();
      }
    }
  });

  $('.mini-grids').on('click', '.mini-grid', function () {
    // Switch this minigrid with the full-size grid
    swapGrids($(this), mainGrid);
  });

  // Load up all of the grids at the start
  loadGrids(startGrid);
});

jQuery.fn.extend({
  // Given a name and 2D array of booleans, initialize a DOM element as a
  // grid
  loadGrid: function (gridName, gridData) {
    // Selector should only have a single element
    var gridHolder = $(this[0]);
    // Store the name of the grid on the holder
    gridHolder.data("name", gridName);
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
        var cell = $('<div class="cell"></div>');
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

   // Only start the ripple if we aren't already in that state
   if ((cell.hasClass('active') && !on)
       || (!cell.hasClass('active') && on)) {
     cell.ripple();
   }
   // Turn the cell on or off
   if (on) {
     cell.addClass('active', fadeTime);
   } else {
     cell.removeClass('active', fadeTime);
   }

  },
  // Sets the size of the cells relative to the size of the given grid
  resizeCells: function () {
    var gridHolder = $(this[0]);
    gridHolder.find('.label').remove();

    var label = $('<div class="label animated-slow fadeOut"></div>');
    label.text(gridHolder.data('name'));
    gridHolder.append(label);

    var width = gridHolder.width();
    var height = gridHolder.height();

    // Margin is 5px for main grid, 2px for mini grid
    var margin = gridHolder.hasClass("mini-grid") ? miniMargin : mainMargin;

    // The number of rows is the number of divs with the class row
    var rowCount = gridHolder.find('.row').length
    // The number of cells per row should be the same in a given grid, so we
    // find the number of divs with the class cell in the first row we find
    var cellCount = gridHolder.find('.row:first > .cell').length

    gridHolder.find('.row').height(Math.floor(height/rowCount) - margin);
    gridHolder.find('.cell').width(Math.floor(width/cellCount) - margin);
  },
  // Gets cell parent grid and coordinates and state, then sends it to server
  cellTrigger: function () {
    var cell = $(this[0]);
    
    // Cell is on
    var on = !cell.hasClass('active');

    cell.toggleClass('active', fadeTime);

    // Our x location is our index in the row
    var xLoc = cell.index();
    // Our y location is our row's index in the grid
    var yLoc = cell.parents('.row').index();

    var name = cell.parents('.grid').data('name');

    // Build the message from various DOM attributes
    var message = JSON.stringify({on: on, x: xLoc, y: yLoc, name: name, id: id, sent: Date.now()});
    // Send the message to the server via WebSockets
    conn.send(message);
  },
  // Animates the cells around us when we're done
  ripple: function () {
    var cell = $(this[0]);
    var gridHolder = cell.parents('.grid');

    // Start our pulse
    cell.addClass('animated pulse');
    
    var yCount = gridHolder.find('.row').length
    // The number of cells per row should be the same in a given grid, so we
    // find the number of divs with the class cell in the first row we find
    var xCount = gridHolder.find('.row:first > .cell').length

    // Our x location is our index in the row
    var xLoc = cell.index();
    // Our y location is our row's index in the grid
    var yLoc = cell.parents('.row').index();

    var furthest = Math.max(xCount-xLoc, yCount-yLoc);
    for (var i = 0; i < 10; i++) {
      (function (i) {
        setTimeout(function () {
          for (var x = -i; x <= i; x++) {
            for (var y = -i; y <= i; y++) {
              if ((x != 0 || y != 0)
                  && (Math.abs(x) == i || Math.abs(y) == i)
                  && xLoc+x >= 0
                  && xLoc+x <= xCount
                  && yLoc+y >= 0
                  && yLoc+y <= yCount) {
                var cell = gridHolder
                             .find('.row:eq(' + (yLoc+y) + ')')
                             .find('.cell:eq(' + (xLoc+x) + ')');
                cell.addClass('animated pulse');
              }
            }
          }
      }, Math.pow(i,0.5)*100)})(i);
    }
  }
});

// Initialize our grids. The first grid takes up the large main grid area, and all subsequent grids get spread out along the bottom
function loadGrids(gridData) {
  // Clear out anything in there
  mainGrid.empty();
  miniGrids.empty();

  // Start at -1 because the first one goes in the main grid
  var miniGridCount = -1;
  for (var gridName in gridData) {
    if (gridData.hasOwnProperty(gridName)) {
      miniGridCount++;
    }
  }

  var isMainGrid = true;
  for (var gridName in gridData) {
    if (gridData.hasOwnProperty(gridName)) {
      // Fill the main grid first
      if (isMainGrid) {
        isMainGrid = false;
        mainGrid.loadGrid(gridName, gridData[gridName]);
      } else {
        var miniGrid = $('<div class="mini-grid grid"></div>');
        miniGrids.append(miniGrid);
        miniGrid.width(Math.ceil(miniGrids.width()/miniGridCount) - miniMargin);
        miniGrid.loadGrid(gridName, gridData[gridName]);
      }
    }
  }
}

function initWebsockets() {
  // Establish a WebSocket connection
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

        if (data.id == id) {
          requestCount++;
          requestTime += (Date.now() - data.sent);
          console.log("Running average: " + requestTime/requestCount);
        }
        // Select our grid by the name passed to us
        var grid = $(nameAsCssClass(data.name));
        // Use the other attributes to figure out which cell to set
        grid.setBlock(data.x, data.y, data.on);
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
  allMiniGrids.width(Math.ceil(miniGrids.width() / allMiniGrids.length) - miniMargin);
  
  $('.grid').each(function () {
    var gridHolder = $(this);
    gridHolder.resizeCells();
  });
}

// A helper function for turning our instrument names into classes
function nameToClass(name) {
  return name.replace(" ", "-") + "-grid"
}

function nameAsCssClass(name) {
  return "." + name.replace(" ", "-") + "-grid"
}

// Switch out the grids
function swapGrids(grid1, grid2) {
  var name1 = grid1.data('name');
  var name2 = grid2.data('name');

  grid1.removeClass(nameToClass(name1));
  grid2.removeClass(nameToClass(name2));

  grid1.addClass(nameToClass(name2));
  grid2.addClass(nameToClass(name1));

  grid1.data('name', name2);
  grid2.data('name', name1);

  var html1 = grid1.html();
  grid1.html(grid2.html());
  grid2.html(html1);

  resizeGrids();
}

function makeID () {
  var text = "";
  var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

  for( var i=0; i < 5; i++ )
    text += possible.charAt(Math.floor(Math.random() * possible.length));

  return text;
}
