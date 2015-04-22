package main

import (
	"bytes"
	"encoding/json"
	"log"
	"os"
	"sort"
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
	Index    int
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

func GridState() string {
	var data bytes.Buffer

	keys := []string{}
	for k, _ := range grids {
		keys = append(keys, k)
	}
	sort.Strings(keys)

	for _, key := range keys {
		for _, notes := range grids[key].Grid {
			for _, note := range notes {
				if note {
					data.WriteString("1")
				} else {
					data.WriteString("0")
				}
			}
		}
	}
	return data.String()
}
