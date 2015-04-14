package main

import (
	"log"
	"net/http"
)

func serveHome(w http.ResponseWriter, r *http.Request) {
	if r.URL.Path != "/" {
		http.Error(w, "Not found", 404)
		return
	}
	if r.Method != "GET" {
		http.Error(w, "Method not allowed", 405)
		return
	}
	err := templates.ExecuteTemplate(w, "home.html", newResponse(r))
	if err != nil {
		log.Println("Error executing template:", err)
	}
}
