package main

import (
	"encoding/json"
	"log"
	"net/http"
	"os"
)

var gridSignatures = []gridDef{
	{12, 12, "Piano", "#2ecc71"},
	{12, 12, "Guitar", "#e74c3c"},
	{12, 12, "Drum1", "#3498db"},
	{12, 12, "Drum2", "#d35400"},
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
	http.HandleFunc("/ws", serveWs)

	//http.Handle("/js/", http.StripPrefix("/js/", http.FileServer(http.Dir("./js"))))
	//http.Handle("/css/", http.StripPrefix("/css/", http.FileServer(http.Dir("./css"))))

	port := os.Getenv("PORT")
	if port == "" {
		port = "8080"
	}

	err := http.ListenAndServe(":"+port, nil)
	if err != nil {
		log.Fatal("ListenAndServe: ", err)
	}
}
