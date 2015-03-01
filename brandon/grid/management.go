package main

import (
	"html/template"
	"log"
	"net/http"
)

type Settings struct {
	Instruments []Instrument
}

type Instrument struct {
	Index    int
	Name     string
	Velocity int
}

var manage, _ = template.ParseFiles("templates/management.html")

func serveManagement(w http.ResponseWriter, r *http.Request) {
	if r.URL.Path != "/" {
		http.Error(w, "Not found", 404)
		return
	}
	if r.Method != "GET" {
		http.Error(w, "Method not allowed", 405)
		return
	}
	w.Header().Set("Content-Type", "text/html; charset=utf-8")
	data := struct {
	}{}
	err := manage.Execute(w, data)
	if err != nil {
		log.Println("Error executing template:", err)
	}
}
