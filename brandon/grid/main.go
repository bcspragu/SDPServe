package main

import (
	"encoding/json"
	"log"
	"net/http"
	"os"
)

var gridSignatures = []gridDef{
	{10, 10, "Piano", "#2ecc71"},
	{10, 10, "Drums", "#3498db"},
	{10, 10, "Guitar", "#e74c3c"},
	{10, 10, "Flute", "#d35400"},
}

type gridDef struct {
	XSize int
	YSize int
	Name  string
	Color string
}

type block struct {
	On   bool
	X    int
	Y    int
	Name string
}

var grids = make(map[string]Grid)

func init() {
	for _, sig := range gridSignatures {
		grids[sig.Name] = NewGrid(sig.XSize, sig.YSize)
	}

	h.addHook(func(message []byte) {
		res := &block{}
		if err := json.Unmarshal(message, &res); err != nil {
			return
		}
		grids[res.Name][res.X][res.Y] = res.On
	})
}

func main() {
	go h.run()

	http.HandleFunc("/", serveHome)
	http.HandleFunc("/grids.json", serveGrids)
	http.HandleFunc("/css/main.css", serveCss)
	http.HandleFunc("/ws", serveWs)

	http.Handle("/js/", http.StripPrefix("/js/", http.FileServer(http.Dir("./js"))))

	port := os.Getenv("PORT")
	if port == "" {
		port = "8080"
	}

	err := http.ListenAndServe(":"+port, nil)
	if err != nil {
		log.Fatal("ListenAndServe: ", err)
	}
}
