package main

import (
	"fmt"
	"github.com/gorilla/websocket"
	"math/rand"
	"net/http"
	"time"
)

// Simulate 20 people using it at once
const WorkerCount = 20

type block struct {
	On   bool   `json:"on"`
	X    int    `json:"x"`
	Y    int    `json:"y"`
	Name string `json:"name"`
}

// Here's the worker, of which we'll run several concurrent instances. Each
// worker will periodically make requests to the site to simulate users
// playing with it
func worker(done chan<- bool) {
	// First connect to the WebSocket server
	dialer := websocket.DefaultDialer
	header := http.Header{}
	header.Set("Origin", "http://bsprague.com")
	conn, _, err := dialer.Dial("ws://bsprague.com/ws", header)
	if err != nil {
		fmt.Println("Error connecting to server", err)
		done <- true
		return
	}

	defer conn.Close()
	go readLoop(conn)

	// Tick regularly somewhere between every 25 and 75 ms
	ticker := time.NewTicker(time.Millisecond * time.Duration(rand.Int63n(51)+25))

	for _ = range ticker.C {
		if err := conn.WriteJSON(randomBlock()); err != nil {
			fmt.Println("Error sending block", err)
			done <- true
			return
		}
	}

	done <- true
}

func readLoop(c *websocket.Conn) {
	for {
		if _, _, err := c.NextReader(); err != nil {
			c.Close()
			break
		}
	}
}

func main() {

	done := make(chan bool, WorkerCount)
	// This starts up 10 workers, initially blocked
	// because there are no jobs yet.
	for i := 0; i < WorkerCount; i++ {
		go worker(done)
	}

	for i := 0; i < WorkerCount; i++ {
		<-done
	}
}

func randomBlock() block {
	x, y, on := rand.Intn(10), rand.Intn(10), rand.Intn(2) == 0
	name := []string{"Piano", "Drums", "Guitar", "Flute"}[rand.Intn(4)]
	return block{on, x, y, name}
}
