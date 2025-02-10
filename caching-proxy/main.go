package main

import (
	"fmt"
	"io"
	"log"
	"net/http"
	"net/url"
	"os"
	"strconv"
)

var cache map[url.URL][]byte
var origin string = "https://google.com"
var port int = 8080

func main() {
	cache = make(map[url.URL][]byte)

	for i := 1; i < len(os.Args); i++ {
		if os.Args[i] == "--port" {
			p, err := strconv.Atoi(os.Args[i+1])
			if err != nil {
				os.Exit(1)
			}
			i = i + 1
			port = p
		} else if os.Args[i] == "--origin" {
			if i+1 >= len(os.Args) {
				os.Exit(1)
			}
			origin = os.Args[i+1]
			i = i + 1
		}
	}
	fmt.Println("port:", port)
	fmt.Println("origin:", origin)
	fmt.Printf("visit http://localhost:%d\n", port)
	log.Println("Starting the server")

	http.HandleFunc("/", handleRequest)
	log.Println(http.ListenAndServe(fmt.Sprintf(":%d", port), nil))
}

func handleRequest(w http.ResponseWriter, r *http.Request) {
	log.Printf("received %s ", r.URL)
	res, isCached := cache[*r.URL]
	if isCached {
		w.Header().Add("X-Cache", "HIT")
		w.Write(res)
		log.Printf("HIT %s -> %s ", r.URL.Path, origin+r.URL.Path)
		return
	} else {
		response, err := http.Get(origin + r.URL.String())
		if err != nil {
			log.Println(err)
			return
		}
		defer response.Body.Close()
		bodyBytes, err := io.ReadAll(response.Body)

		response.Header.Add("X-Cache", "MISS")

		for key, values := range response.Header {
			for _, value := range values {
				w.Header().Add(key, value)
			}
		}

		w.WriteHeader(response.StatusCode)
		w.Write(bodyBytes)
		log.Printf("MISS %s -> %s", r.URL.Path, origin+r.URL.Path)

		cache[*r.URL] = bodyBytes
		return
	}
}
