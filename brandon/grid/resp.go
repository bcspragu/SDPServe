package main

import (
	"encoding/json"
	"net/http"
)

type BaseResponse struct {
	Host     string
	Grids    map[string]GridDef
	GridJSON string
	CSS      []string
}

var cssFiles = []string{"main", "font"}
var cssFilesBS = []string{"bootstrap", "bootstrap-select", "main", "font"}
var cssFilesNoMain = []string{"bootstrap", "bootstrap-select", "font"}

func newResponse(r *http.Request, cssFiles ...string) BaseResponse {
	cssFiles = append(cssFiles, "font", "all")
	gridString, _ := json.Marshal(Preset{settings, grids})
	br := BaseResponse{Host: r.Host, GridJSON: string(gridString), CSS: cssFiles}
	br.Grids = grids
	return br
}
