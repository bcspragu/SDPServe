package main

import (
	"encoding/json"
	"log"
	"net/http"
	"strconv"
)

type Stats struct {
	TotalClicks   int64
	TotalResponse int64
	MinResponse   int
	MaxResponse   int
	GridClicks    map[string]int64
	ActiveUsers   int
}

type StatMessage struct {
	Stats
	Type        string `json:"type"`
	AverageResp string
}

func saveAverage(w http.ResponseWriter, r *http.Request) {
	if r.Method != "POST" {
		http.Error(w, "Method not allowed", 405)
		return
	}
	average, err := strconv.ParseInt(r.PostFormValue("requestDuration"), 10, 64)
	if err == nil {
		stats.TotalClicks++
		stats.TotalResponse += average
	}
	broadcastStats()
}

func serveStats(w http.ResponseWriter, r *http.Request) {
	if r.Method != "GET" {
		http.Error(w, "Method not allowed", 405)
		return
	}
	data := struct {
		BaseResponse
		Stats
	}{
		newResponseBS(r),
		stats,
	}
	err := templates.ExecuteTemplate(w, "stats.html", data)
	if err != nil {
		log.Println("Error executing template:", err)
	}
}

func (s Stats) AverageResp() string {
	if s.TotalClicks == 0 {
		return "No clicks yet"
	}
	return strconv.FormatFloat(float64(s.TotalResponse)/float64(s.TotalClicks), 'f', 3, 64) + " ms"
}

func broadcastStats() {
	statString, _ := json.Marshal(StatMessage{stats, "stat", stats.AverageResp()})
	h.broadcast <- statString
}
