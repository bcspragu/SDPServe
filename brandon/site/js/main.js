var size = 10;
var fadeTime = 100;
var prettyColors = ["#1abc9c", "#2ecc71", "#3498db", "#9b59b6", "#34495e", "#16a085", "#27ae60", "#2980b9", "#8e44ad", "#2c3e50", "#f1c40f", "#e67e22", "#e74c3c", "#ecf0f1", "#95a5a6", "#f39c12", "#d35400", "#c0392b", "#bdc3c7", "#7f8c8d"];

$(function () {
  $(window).resize(function () {
    $('.background-design').resizeCells();
  });
  var gridData = new Array(size);
  for (var i = 0; i < size; i++) {
    gridData[i] = new Array(size);
    for (var j = 0; j < size; j++) {
      gridData[i][j] = false;
    }
  }

  $('.background-design').loadGrid(gridData);
  setInterval(animateBlock, 250);
});

jQuery.fn.extend({
  // Given a name and 2D array of booleans, initialize a DOM element as a
  // grid
  loadGrid: function (gridData) {
    // Selector should only have a single element
    var gridHolder = $(this[0]);

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
  // Sets the size of the cells relative to the size of the given grid
  resizeCells: function () {
    var gridHolder = $(this[0]);

    var width = gridHolder.width();
    var height = gridHolder.height();

    // Margin is 5px for main grid
    var margin = 5;

    // The number of rows is the number of divs with the class row
    var rowCount = gridHolder.find('.row').length
    // The number of cells per row should be the same in a given grid, so we
    // find the number of divs with the class cell in the first row we find
    var cellCount = gridHolder.find('.row:first > .cell').length

    gridHolder.find('.row').height(Math.floor(height/rowCount) - margin);
    gridHolder.find('.cell').width(Math.floor(width/cellCount) - margin);
  }
});

function animateBlock() {
  var index = random(size*size);
  var color = prettyColors[random(prettyColors.length)];
  $('.cell:eq(' + index + ')').animate({
    backgroundColor: color,
  }, 500 + random(1000));
}

// Returns a value [0,n)
function random(n) {
  return Math.floor(Math.random()*n);
}
