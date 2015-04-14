package main

import (
	"bytes"
	"encoding/gob"
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
		Presets map[string]Preset
	}{
		newResponse(r),
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
	gridString, _ := json.Marshal(settings)
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
				}
			}
		}
	}
	w.WriteHeader(http.StatusOK)
	w.Header().Set("Content-Type", "application/json")
	respString, _ := json.Marshal(resp)
	fmt.Fprint(w, string(respString))
}

func presets() map[string]Preset {
	presets := make(map[string]Preset)
	db.View(func(tx *bolt.Tx) error {
		b := tx.Bucket([]byte("Presets"))
		b.ForEach(func(k, v []byte) error {
			buf := bytes.NewBuffer(v)
			dec := gob.NewDecoder(buf)
			preset := Preset{}
			err := dec.Decode(&preset)
			if err != nil {
				return err
			}
			presets[string(k)] = preset
			return nil
		})
		return nil
	})
	return presets
}

func savePreset(name string, preset Preset, t *bolt.Tx) error {
	updateFunc := func(tx *bolt.Tx) error {
		b := tx.Bucket([]byte("Presets"))

		var buf bytes.Buffer
		enc := gob.NewEncoder(&buf)
		err := enc.Encode(preset)
		if err != nil {
			return err
		}

		return b.Put([]byte(name), buf.Bytes())
	}
	// If tx isn't nil, we're already in the block
	if t == nil {
		return db.Update(func(tx *bolt.Tx) error {
			return updateFunc(tx)
		})
	} else {
		return updateFunc(t)
	}
}

func (p Preset) SettingsJSON() (string, error) {
	b, err := json.Marshal(p.Settings)
	return string(b), err
}

func DefaultPreset() Preset {
	preset := Preset{Settings: Settings{Duration: 100}}
	preset.Instruments = []ActiveInstrument{
		{instruments[0], 100},
		{instruments[24], 100},
		{instruments[117], 100},
	}
	preset.Grids = grids
	return preset
}

func (inst Instrument) Similar() []Instrument {
	insts := make([]Instrument, 0)
	for _, in := range instruments {
		if in.Tuned == inst.Tuned {
			insts = append(insts, in)
		}
	}
	return insts
}
