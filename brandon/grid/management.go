package main

import (
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"strconv"
)

func serveManagement(w http.ResponseWriter, r *http.Request) {
	if r.Method != "GET" {
		http.Error(w, "Method not allowed", 405)
		return
	}
	data := struct {
		BaseResponse
		Settings
		Presets map[string]Preset
	}{
		newResponseBS(r),
		settings,
		presets(),
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
	gridString, _ := json.Marshal(Preset{settings, grids})
	fmt.Fprint(w, string(gridString))
}

func setSettings(w http.ResponseWriter, r *http.Request) {
	resp := make(map[string]string)
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
			savePreset(val, Preset{settings, grids}, nil)
		case "velocity":
			id, _ := strconv.Atoi(r.PostFormValue("id"))
			vel, _ := strconv.Atoi(val)
			for i, inst := range settings.Instruments {
				if id == inst.ID {
					settings.Instruments[i].Velocity = vel
				}
			}
		case "instrumentID":
			id, _ := strconv.Atoi(r.PostFormValue("id"))
			newID, _ := strconv.Atoi(val)
			for i, inst := range settings.Instruments {
				if id == inst.ID {
					settings.Instruments[i].Instrument = instruments[newID]
					resp["newName"] = instruments[newID].Name
				}
			}
		case "preset":
			p := presets()[val]
			settings = p.Settings
			grids = p.Grids
			broadcastData()
		}
	}
	w.WriteHeader(http.StatusOK)
	w.Header().Set("Content-Type", "application/json")
	respString, _ := json.Marshal(resp)
	fmt.Fprint(w, string(respString))
}

func (p Preset) SettingsJSON() (string, error) {
	b, err := json.Marshal(p.Settings)
	return string(b), err
}

func broadcastData() {
	data := struct {
		Type     string `json:"type"`
		GridData Preset
	}{
		"preset",
		Preset{settings, grids},
	}

	dataString, _ := json.Marshal(data)
	h.broadcast <- dataString
}
