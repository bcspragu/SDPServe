package main

import (
	"encoding/json"
	"log"
	"os"
	"strings"
)

type Grid [][]bool

var grids map[string]GridDef

type GridDef struct {
	XSize    int
	YSize    int
	OnColor  string
	OffColor string
	Name     string
	Grid     Grid
}

func initGrids() {
	w, err := os.Open("gridSignatures")
	if err != nil {
		log.Fatal("No grid file found")
	}

	d := json.NewDecoder(w)
	err = d.Decode(&grids)
	if err != nil {
		log.Fatal("Deformed grid file found:", err)
	}

	for name, sig := range grids {
		grid := NewGrid(sig.XSize, sig.YSize)

		gd := grids[name]
		gd.Grid = grid
		gd.Name = name

		grids[name] = gd

		stats.GridClicks[name] = 0
	}
}

func (g GridDef) AsCSSClass() string {
	return "." + strings.Replace(g.Name, " ", "-", -1) + "-grid"
}
