package main

import (
	"encoding/json"
	"html/template"
	"log"
	"net/http"
)

var index, _ = template.ParseFiles("templates/home.html")

func serveHome(w http.ResponseWriter, r *http.Request) {
	if r.URL.Path != "/" {
		http.Error(w, "Not found", 404)
		return
	}
	if r.Method != "GET" {
		http.Error(w, "Method not allowed", 405)
		return
	}
	w.Header().Set("Content-Type", "text/html; charset=utf-8")
	gridString, _ := json.Marshal(grids)
	data := struct {
		Host   string
		Grids  string
		Colors map[string]string
	}{
		r.Host,
		string(gridString),
		cssGridColors,
	}
	err := index.Execute(w, data)
	if err != nil {
		log.Println("Error executing template:", err)
	}
}
