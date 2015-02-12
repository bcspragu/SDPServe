package main

import (
	"encoding/json"
	"fmt"
	"net/http"
)

type Grid [][]bool

func NewGrid(x, y int) Grid {
	grid := Grid{}
	grid = make([][]bool, x)
	for i := range grid {
		grid[i] = make([]bool, y)
	}
	return grid
}

func serveGrids(w http.ResponseWriter, r *http.Request) {
	if r.Method != "GET" {
		http.Error(w, "Method not allowed", 405)
		return
	}
	w.Header().Set("Content-Type", "application/json")
	gridString, _ := json.Marshal(grids)
	fmt.Fprint(w, string(gridString))
}
