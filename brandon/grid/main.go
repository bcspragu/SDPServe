package main

import (
	"encoding/json"
	"io/ioutil"
	"log"
	"net/http"
	"os"
)

var gridSignatures []gridDef

type gridDef struct {
	XSize    int
	YSize    int
	Name     string
	OnColor  string
	OffColor string
}

type block struct {
	On   bool
	X    int
	Y    int
	Name string
}

var grids = make(map[string]Grid)
var presets = make(map[string]map[string]Grid)

func init() {
	initGrids()
	setHooks()
	loadPresets()
}

func main() {
	go h.run()

	http.HandleFunc("/", serveHome)
	http.HandleFunc("/management", serveManagement)
	http.HandleFunc("/ws", serveWs)
	http.HandleFunc("/grids.json", serveGrids)
	http.HandleFunc("/preset", saveGrids)

	http.Handle("/js/", http.StripPrefix("/js/", http.FileServer(http.Dir("./js"))))
	http.Handle("/css/", http.StripPrefix("/css/", http.FileServer(http.Dir("./css"))))

	port := os.Getenv("PORT")
	if port == "" {
		port = "8080"
	}

	err := http.ListenAndServe(":"+port, nil)
	if err != nil {
		log.Fatal("ListenAndServe: ", err)
	}
}

func initGrids() {
	w, err := os.Open("gridSignatures")
	if err != nil {
		log.Fatal("No grid file found")
	}

	d := json.NewDecoder(w)
	err = d.Decode(&gridSignatures)
	if err != nil {
		log.Fatal("Deformed grid file found")
	}

	for _, sig := range gridSignatures {
		grids[sig.Name] = NewGrid(sig.XSize, sig.YSize)
	}

	initCss()
}

func setHooks() {
	// Whenever we receive a message on WebSockets, call this hook
	h.addHook(func(message []byte) {
		res := &block{}
		if err := json.Unmarshal(message, &res); err != nil {
			return
		}
		grids[res.Name][res.X][res.Y] = res.On
	})
}

func loadPresets() {
	files, _ := ioutil.ReadDir("presets")
	for _, f := range files {
		w, _ := os.Open("presets" + string(os.PathSeparator) + f.Name())
		d := json.NewDecoder(w)
		var g map[string]Grid
		d.Decode(&g)
		presets[f.Name()] = g
	}
}
