package main

import (
	"bytes"
	"encoding/gob"
	"encoding/json"
	"github.com/boltdb/bolt"
	"html/template"
	"io/ioutil"
	"log"
	"net/http"
	"os"
	"strings"
)

var (
	templates   = template.Must(template.ParseGlob("templates/*.html"))
	stats       = Stats{GridClicks: make(map[string]int64)}
	settings    = Settings{Duration: 100}
	instruments []Instrument
	db          *bolt.DB
)

type WSMessage struct {
	Type string
}

type Block struct {
	On   bool
	X    int
	Y    int
	Name string
}

var presets = make(map[string]map[string]Grid)

func init() {
	gob.Register(Instrument{})
	initDB()
	setHooks()
	loadPresets()
}

func main() {
	go h.run()

	var err error
	db, err = bolt.Open("beat.db", 0600, nil)
	if err != nil {
		panic(err)
	}
	defer db.Close()

	// Serve the main page
	http.HandleFunc("/", serveHome)

	// Update the average response time
	http.HandleFunc("/avg", saveAverage)
	// View realtime usage statistics
	http.HandleFunc("/stats", serveStats)

	// Dyanmic CSS based on gridSignatures
	http.HandleFunc("/css/main.css", serveCSS)

	// Management console endpoints
	http.HandleFunc("/management", serveManagement)
	http.HandleFunc("/management.json", serveSettings)
	// Updating management console settings
	http.HandleFunc("/settings", setSettings)

	// WebSocket connection point
	http.HandleFunc("/ws", serveWs)

	// Grid state endpoint
	http.HandleFunc("/grids.json", serveGrids)
	http.HandleFunc("/preset", saveGrids)

	http.Handle("/js/", http.StripPrefix("/js/", http.FileServer(http.Dir("./js"))))
	http.Handle("/css/", http.StripPrefix("/css/", http.FileServer(http.Dir("./css"))))
	http.Handle("/fonts/", http.StripPrefix("/fonts/", http.FileServer(http.Dir("./fonts"))))

	port := os.Getenv("PORT")
	if port == "" {
		port = "8080"
	}

	err = http.ListenAndServe(":"+port, nil)
	if err != nil {
		log.Fatal("ListenAndServe: ", err)
	}
}

func setHooks() {
	// Whenever we receive a message on WebSockets, call this hook
	h.addHook(func(message []byte) {
		res := &Block{}
		typ := &WSMessage{}
		if err := json.Unmarshal(message, &typ); err != nil {
			return
		}

		switch typ.Type {
		case "tap":
			if err := json.Unmarshal(message, &res); err != nil {
				return
			}
			grids[res.Name].Grid[res.X][res.Y] = res.On
			stats.GridClicks[res.Name]++
		}
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

func initDB() {
	var err error
	db, err = bolt.Open("beat.db", 0600, nil)
	if err != nil {
		panic(err)
	}
	defer db.Close()

	err = db.Update(func(tx *bolt.Tx) error {
		_, err := tx.CreateBucketIfNotExists([]byte("Presets"))
		if err != nil {
			return err
		}

		inst := []byte("Instruments")

		b := tx.Bucket(inst)
		if b != nil {
			// Means we can just load them to memory
			instrumentsFromDB(b)
			log.Println("Loaded instruments from DB")
		} else {
			// Means we have to make the bucket and fill it
			b, _ = tx.CreateBucket(inst)
			err = instrumentsToDB(b)
			log.Println("Made bucket and saved instruments to DB")
		}

		return err
	})

	if err != nil {
		panic(err)
	}
}

func instrumentsToDB(b *bolt.Bucket) error {
	d, err := ioutil.ReadFile("instruments")
	if err != nil {
		return err
	}

	lines := strings.Split(string(d), "\n")
	lines = lines[:len(lines)-1]
	instruments = make([]Instrument, len(lines))

	var buf bytes.Buffer
	enc := gob.NewEncoder(&buf)
	for i, line := range lines {
		s := strings.Split(line, ",")
		inst := Instrument{ID: i, Name: s[0], Tuned: s[1] == "1"}
		instruments[i] = inst
		err = enc.Encode(inst)
		if err != nil {
			return err
		}
		err = b.Put([]byte(s[0]), buf.Bytes())
		if err != nil {
			return err
		}
	}

	return nil
}

func instrumentsFromDB(b *bolt.Bucket) []Instrument {
	insts := make([]Instrument, b.Stats().KeyN)
	i := 0
	var inst Instrument
	b.ForEach(func(k, v []byte) error {
		buf := bytes.NewBuffer(v)
		dec := gob.NewDecoder(buf)
		err := dec.Decode(&inst)
		insts[i] = inst
		i++
		return err
	})

	return insts
}
