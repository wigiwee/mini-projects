package main

import (
	"bufio"
	"log"
	"math/rand"
	"os"
	"strconv"
	"time"

	"github.com/gen2brain/beeep"
)

func main() {
	if len(os.Args) < 3 {
		log.Fatalf("Usage %s <minutes> <filename>", os.Args[0])
		os.Exit(1)
	}
	minutes, err := strconv.Atoi(os.Args[1])
	if err != nil {
		log.Fatal(err)
		os.Exit(2)
	} else if minutes <= 0 {
		log.Fatal("set minutes value > 0")
		os.Exit(5)
	}
	for {
		time.Sleep(time.Duration(minutes * int(time.Minute)))
		err := beeep.Alert("GRE Word", getRandomWord(os.Args[2]), "assets/information.png")
		if err != nil {
			log.Fatal(err)
			os.Exit(3)
		}
	}
}

func getRandomWord(filename string) string {
	file, err := os.Open(filename)
	if err != nil {
		log.Fatal("Error opening file", err)
	}
	scanner := bufio.NewScanner(file)
	allWords := make([]string, 0)
	for scanner.Scan() {
		if scanner.Text()[0] == ' ' {
			continue
		}
		allWords = append(allWords, scanner.Text())
	}
	return allWords[rand.Intn(len(allWords))]
}
