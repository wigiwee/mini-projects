package main

import (
	"encoding/json"
	"fmt"
	"math/rand/v2"
	"net/http"
	"strings"
)

type req struct {
	Url string
}

type res struct {
	Id          string
	RedirectUrl string
}

var urlMapping = make(map[string]string)

const idLen int = 7

const domain string = "localhost:8000"

func main() {

	http.HandleFunc("/registerurl", registerUrl)
	http.HandleFunc("/{id}", redirect)
	http.Handle("/", http.FileServer(http.Dir("./views")))

	err := http.ListenAndServe(":8000", nil)

	if err != nil {
		fmt.Println("Something went wrong while starting the server")
	}

}

func registerUrl(w http.ResponseWriter, r *http.Request) {

	if r.Method != "POST" {
		w.Write([]byte("Method not supported"))
		return
	}

	var requestBody req

	requestBody.Url = r.FormValue("url")

	var response res

	for id, url := range urlMapping {
		if url == requestBody.Url {
			w.Header().Set("Content-Type", "application/json")
			response.Id = id
			response.RedirectUrl = domain + "/" + response.Id
			json.NewEncoder(w).Encode(response)
			return
		}
	}
	id := generateRandomId()

	urlMapping[id] = requestBody.Url

	response.Id = string(id)
	response.RedirectUrl = domain + "/" + response.Id

	fmt.Println(response)
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(response)
}

func redirect(w http.ResponseWriter, r *http.Request) {

	if r.Method != "GET" {
		w.Write([]byte("Invalid request type"))
		return
	}
	// path := strings.Split(r.URL.Path, "/")	//one way to do this
	id := r.PathValue("id") //easier way
	http.Redirect(w, r, urlMapping[strings.ToLower(id)], http.StatusMovedPermanently)
}

func generateRandomId() string {

	id := make([]byte, idLen)

	for i := 0; i < idLen; i++ {
		id[i] = byte(65 + rand.IntN(26))
	}

	return strings.ToLower(string(id))
}
