// Copyright 2013 The Gorilla WebSocket Authors. All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.

package main

import (
	"encoding/json"
	"flag"
	"log"
	"net/http"
	"text/template"
)

const (
	GridCount = 4
	XSize     = 10
	YSize     = 10
)

var addr = flag.String("addr", ":8080", "http service address")

var templates = template.Must(template.ParseGlob("templates/*"))
var grid [GridCount][XSize][YSize]bool

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
	gridString, _ := json.Marshal(grid)
	data := struct {
		XCount    int
		YCount    int
		GridCount int
		Host      string
		Grid      string
	}{
		XSize,
		YSize,
		GridCount,
		r.Host,
		string(gridString),
	}
	err := templates.ExecuteTemplate(w, "home.html", data)
	if err != nil {
		log.Println("Error executing template:", err)
	}
}

func main() {
	flag.Parse()
	go h.run()

	http.HandleFunc("/", serveHome)
	http.HandleFunc("/ws", serveWs)
	http.Handle("/js/", http.StripPrefix("/js/", http.FileServer(http.Dir("./js"))))
	http.Handle("/css/", http.StripPrefix("/css/", http.FileServer(http.Dir("./css"))))

	err := http.ListenAndServe(*addr, nil)
	if err != nil {
		log.Fatal("ListenAndServe: ", err)
	}
}
