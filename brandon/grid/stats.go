package main

import (
	"encoding/json"
	"log"
	"net/http"
	"sort"
	"strconv"
)

type Stats struct {
	ResponseTimes []int
	Min           int
	Max           int
	GridClicks    map[string]int64
	ActiveUsers   int
}

type StatMessage struct {
	Type              string `json:"type"`
	CumulativeAverage string
	RunningAverage    string
	Median            string
	Mode              string
	TotalClicks       int
	ActiveUsers       int
	Min               string
	Max               string
}

func addTime(w http.ResponseWriter, r *http.Request) {
	if r.Method != "POST" {
		http.Error(w, "Method not allowed", 405)
		return
	}
	a, err := strconv.ParseInt(r.PostFormValue("requestDuration"), 10, 64)
	average := int(a)
	if err == nil {
		stats.ResponseTimes = append(stats.ResponseTimes, average)
	}

	if average < stats.Min || len(stats.ResponseTimes) == 1 {
		stats.Min = average
	}

	if average > stats.Max {
		stats.Max = average
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
		newResponse(r, "bootstrap", "bootstrap-select"),
		stats,
	}
	err := templates.ExecuteTemplate(w, "stats.html", data)
	if err != nil {
		log.Println("Error executing template:", err)
	}
}

func (s Stats) CumulativeAverage() string {
	if len(s.ResponseTimes) == 0 {
		return "No clicks yet"
	}
	return strconv.FormatFloat(s.TotalResponseTime()/float64(len(s.ResponseTimes)), 'f', 3, 64) + " ms"
}

func (s Stats) Median() string {
	if len(s.ResponseTimes) == 0 {
		return "No clicks yet"
	}

	sorted := make([]int, len(s.ResponseTimes))
	copy(sorted, s.ResponseTimes)
	sort.Ints(sorted)

	return strconv.Itoa(sorted[len(s.ResponseTimes)/2]) + " ms"
}

func (s Stats) MaxResponse() string {
	if len(s.ResponseTimes) == 0 {
		return "No clicks yet"
	}

	return strconv.Itoa(s.Max) + " ms"
}

func (s Stats) MinResponse() string {
	if len(s.ResponseTimes) == 0 {
		return "No clicks yet"
	}

	return strconv.Itoa(s.Min) + " ms"
}

func (s Stats) Mode() string {
	if len(s.ResponseTimes) == 0 {
		return "No clicks yet"
	}

	count := make(map[int]int)
	for _, time := range s.ResponseTimes {
		count[time]++
	}
	mode, freq := 0, 0
	for time, f := range count {
		if f > freq {
			mode = time
			freq = f
		}
	}
	return strconv.Itoa(mode) + " ms"
}

func (s Stats) RunningAverage() string {
	var count int64
	if len(s.ResponseTimes) == 0 {
		return "No clicks yet"
	} else if len(s.ResponseTimes) > 40 {
		count = 40
	} else {
		count = int64(len(s.ResponseTimes))
	}

	return strconv.FormatFloat(s.RunningResponseTime(count)/float64(count), 'f', 3, 64) + " ms"
}

func (s Stats) RunningResponseTime(partial int64) float64 {
	size := int64(len(s.ResponseTimes))
	return sum(s.ResponseTimes[size-partial : size])
}

func (s Stats) TotalResponseTime() float64 {
	return sum(s.ResponseTimes)
}

func sum(slice []int) float64 {
	var total int64
	for _, num := range slice {
		total += int64(num)
	}
	return float64(total)
}

func (s Stats) TotalClicks() int {
	return len(s.ResponseTimes)
}

func broadcastStats() {
	statString, _ := json.Marshal(StatMessage{
		Type: "stat",

		CumulativeAverage: stats.CumulativeAverage(),
		RunningAverage:    stats.RunningAverage(),

		Median: stats.Median(),
		Mode:   stats.Mode(),

		TotalClicks: stats.TotalClicks(),
		ActiveUsers: stats.ActiveUsers,

		Max: stats.MaxResponse(),
		Min: stats.MinResponse(),
	})
	h.broadcast <- statString
}
