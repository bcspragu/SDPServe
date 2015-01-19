package main

import (
	"log"
	"net/http"
	"strings"
	"text/template"
)

var css, _ = template.ParseFiles("css/main.css")

var cssGridColors map[string]string = make(map[string]string)

func init() {
	for _, sig := range gridSignatures {
		cssGridColors[nameAsCssClass(sig.Name)] = sig.Color
	}
}

func serveCss(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "text/css; charset=utf-8")
	data := struct {
		Colors map[string]string
	}{
		cssGridColors,
	}
	err := css.Execute(w, data)
	if err != nil {
		log.Println("Error executing template:", err)
	}
}

func nameAsCssClass(name string) string {
	return "." + strings.Replace(name, " ", "-", -1) + "-grid"
}
