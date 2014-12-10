// WebSocket object
var conn;

// Local map
var papers = new Array(GRIDCOUNT);
var canvases = new Array(GRIDCOUNT);
var colors = ["red", "orange", "green", "blue"];
var grids = new Array(GRIDCOUNT);

for (var i = 0; i < GRIDCOUNT; i++) {
  grids[i] = new Array(XCOUNT);
  for (var j = 0; j < XCOUNT; j++) {
    grids[i][j] = new Array(YCOUNT);
  }
}


$(function(){
  // Set the canvas
  canvases[0] = $("#canvas");
  canvases[1] = $("#mini1");
  canvases[2] = $("#mini2");
  canvases[3] = $("#mini3");
  papers[0] = Raphael(canvases[0].get(0), "100%", "100%");
  papers[1] = Raphael(canvases[1].get(0), "100%", "100%");
  papers[2] = Raphael(canvases[2].get(0), "100%", "100%");
  papers[3] = Raphael(canvases[3].get(0), "100%", "100%");

  // Establish a WebSocket connection
  if (window["WebSocket"]) {
      conn = new WebSocket("ws://localhost:8080/ws");
      conn.onerror = function(evt) {
        console.log(evt);
      }
      conn.onclose = function(evt) {
        console.log(evt);
      }
      conn.onmessage = function(evt) { // Message received. evt.data is something
        var data = JSON.parse(evt.data);

        setBlock(data.grid, data.x, data.y, data.on);
      }
  } else {
      // Your browser does not support WebSockets
  }

  drawCanvas(0);
  drawCanvas(1);
  drawCanvas(2);
  drawCanvas(3);
  initState();
  $(window).resize(function() {
    drawCanvas(0, "red");
    drawCanvas(1, "orange");
    drawCanvas(2, "yellow");
    drawCanvas(3, "green");
  });
});

function drawCanvas(grid) {
  // Subtract the spacing so we don't butt up against the right/bottom edge
  var width = canvases[grid].width();
  var height = canvases[grid].height();
  var paper = papers[grid];

  var size;
  var spacing
  var extra_x = 0;
  var extra_y = 0;
  if (width < height) {
    spacing = Math.ceil(width / XCOUNT / 10);
    size = Math.floor((width - spacing) / XCOUNT);
    extra_y = Math.floor((height - YCOUNT * size - spacing) / YCOUNT);
  } else {
    spacing = Math.ceil(height / YCOUNT / 10);
    size = Math.floor((height - spacing) / YCOUNT);
    extra_x = Math.floor((width - XCOUNT * size - spacing) / XCOUNT);
  }

  for (var i = 0; i < XCOUNT; i++) {
    for (var j = 0; j < YCOUNT; j++) {
      if (typeof grids[grid][i][j] === "undefined") {
        grids[grid][i][j] = paper.rect(i * (size + extra_x) + spacing,
                                  j * (size + extra_y) + spacing,
                                  size + extra_x - spacing,
                                  size + extra_y - spacing)
                                  .attr({fill: "white"})
                                  .data('x', i)
                                  .data('y', j)
                                  .data('on', false)
                                  .click(function() {
                                    if (typeof conn !== "undefined") {
                                      var x = this.data('x');
                                      var y = this.data('y');
                                      // Toggle on
                                      var on = !this.data('on');
                                      setBlock(grid,x,y,on);
                                      conn.send(JSON.stringify({x: x, y: y, on: on, grid: grid}));
                                    }
                                  })
                                  .touchstart(function(e) {
                                    e.preventDefault();
                                    if (typeof conn !== "undefined") {
                                      var x = this.data('x');
                                      var y = this.data('y');
                                      // Toggle on
                                      var on = !this.data('on');
                                      setBlock(grid,x,y,on);
                                      conn.send(JSON.stringify({x: x, y: y, on: on, grid: grid}));
                                    }
                                  });
        } else {
          var block = grids[grid][i][j];
          block.attr({x: i * (size + extra_x) + spacing,
                      y: j * (size + extra_y) + spacing,
                      width: size + extra_x - spacing,
                      height: size+ extra_y - spacing});
        }
    }
  }
}

function setBlock(grid, x, y, state) {
  var block = grids[grid][x][y];
  if (state) {
    block.attr({fill: colors[grid]});
  } else {
    block.attr({fill: 'white'});
  }
  block.data('on', state);
}

function initState() {
  for (var i = 0; i < GRIDCOUNT; i++) {
    for (var j = 0; j < XCOUNT; j++) {
      for (var k = 0; k < YCOUNT; k++) {
        setBlock(i, j, k, start_grid[i][j][k]);
      }
    }
  }
}
