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

func newResponse(r *http.Request) BaseResponse {
	gridString, _ := json.Marshal(Preset{settings, grids})
	br := BaseResponse{Host: r.Host, GridJSON: string(gridString), CSS: cssFiles}
	br.Grids = grids
	return br
}

func newResponseBS(r *http.Request) BaseResponse {
	gridString, _ := json.Marshal(Preset{settings, grids})
	br := BaseResponse{Host: r.Host, GridJSON: string(gridString), CSS: cssFilesBS}
	br.Grids = grids
	return br
}
