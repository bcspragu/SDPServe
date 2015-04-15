package main

import (
	"log"
	"net/http"
	"text/template"
)

var css = template.Must(template.ParseGlob("templates/*.css"))

func serveCSS(w http.ResponseWriter, r *http.Request) {
	if r.Method != "GET" {
		http.Error(w, "Method not allowed", 405)
		return
	}
	w.Header().Set("Content-Type", "text/css")

	data := struct {
		BaseResponse
	}{
		newResponse(r),
	}
	err := css.ExecuteTemplate(w, "main.css", data)
	if err != nil {
		log.Println("Error executing template:", err)
	}
}
