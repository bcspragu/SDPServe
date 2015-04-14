package main

import (
	"encoding/json"
	"net/http"
)

type BaseResponse struct {
	Host     string
	Grids    map[string]GridDef
	GridJSON string
}

func newResponse(r *http.Request) BaseResponse {
	gridString, _ := json.Marshal(grids)
	br := BaseResponse{Host: r.Host, GridJSON: string(gridString)}
	br.Grids = grids
	return br
}
