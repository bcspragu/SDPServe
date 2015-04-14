package main

import (
	"encoding/json"
	"fmt"
	"github.com/boltdb/bolt"
	"log"
	"net/http"
	"strconv"
)

type Preset struct {
	Settings
	Grids map[string]GridDef
}

type Settings struct {
	Duration    int
	Instruments []ActiveInstrument
}

type Instrument struct {
	ID    int
	Name  string
	Tuned bool
}

type ActiveInstrument struct {
	Instrument
	Velocity int
}

func serveManagement(w http.ResponseWriter, r *http.Request) {
	if r.Method != "GET" {
		http.Error(w, "Method not allowed", 405)
		return
	}
	data := struct {
		BaseResponse
		Settings
	}{
		newResponse(r),
		settings,
	}
	err := templates.ExecuteTemplate(w, "management.html", data)
	if err != nil {
		log.Println("Error executing template:", err)
	}
}

func serveSettings(w http.ResponseWriter, r *http.Request) {
	if r.Method != "GET" {
		http.Error(w, "Method not allowed", 405)
		return
	}
	w.Header().Set("Content-Type", "application/json")
	gridString, _ := json.Marshal(settings)
	fmt.Fprint(w, string(gridString))
}

func setSettings(w http.ResponseWriter, r *http.Request) {
	if r.Method != "POST" {
		http.Error(w, "Method not allowed", 405)
		return
	}
	r.ParseForm()
	for field := range r.PostForm {
		val := r.PostFormValue(field)
		switch field {
		case "duration":
			if d, err := strconv.Atoi(val); err == nil {
				settings.Duration = d
			}
		case "snapshot":
			savePreset(val)
		}
	}
}

//TODO(bsprague): Finish implementing this
func savePreset(name string) error {
	return db.Update(func(tx *bolt.Tx) error {
		tx.Bucket([]byte("Presets"))
		return nil
	})
}
