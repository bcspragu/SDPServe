package main

import (
	"encoding/json"
	"fmt"
	"io/ioutil"
	"net/http"
	"os"
	"strings"
)

var unwantedChars = ".-_+!@#$%^&*()="

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

func saveGrids(w http.ResponseWriter, r *http.Request) {
	if r.Method != "POST" {
		http.Error(w, "Method not allowed", 405)
		return
	}
	fileName := sanitizeFilename(r.PostFormValue("name"))
	if fileName == "" {
		http.Error(w, "Missing preset name", 500)
		return
	}
	_, err := os.Open(fileName)
	// Only write the file if it doesn't exist
	if os.IsNotExist(err) {
		err = nil
		gridString, _ := json.Marshal(grids)
		ioutil.WriteFile("presets"+string(os.PathSeparator)+fileName, gridString, 0644)
	} else {
		http.Error(w, "File exists", 500)
		return
	}
}

func sanitizeFilename(fileName string) string {
	for c := range unwantedChars {
		fileName = strings.Replace(fileName, string(c), "", -1)
	}
	return fileName
}
