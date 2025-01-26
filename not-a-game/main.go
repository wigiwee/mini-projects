package main

import (
	"bytes"
	"fmt"
	"os"
	"os/exec"
	"time"
)

const (
	PLAYER      = 69
	WALL        = 1
	NOTHING     = 0
	MAX_SAMPLES = 100
)

type position struct {
	x, y int
}

type player struct {
	pos     position
	level   *level
	input   *input
	reverse bool
}

func (p *player) update() {
	if p.reverse {
		p.pos.x -= 1
		if p.pos.x == 2 {
			p.reverse = false
		}
	} else {
		p.pos.x += 1
		if p.pos.x == p.level.width-2 {
			// p.pos.x -= 1
			p.reverse = true
		}
	}
}

type stats struct {
	start  time.Time
	frames int
	fps    float64
}

func newStats() *stats {
	return &stats{
		start: time.Now(),
		fps:   69,
	}
}

type input struct {
	pressedKey byte
}

func (i *input) update() {
	// i.pressedKey = 0

	// tick := time.NewTicker(time.Microsecond * 30)

	// ch := make(chan byte, 1)
	// go func() {
	// 	b := make([]byte, 1)
	// 	now := time.Now()
	// 	os.Stdin.Read(b) //Read is blocking
	// 	os.Stdin.SetReadDeadline(now.Add(20 * time.Millisecond))
	// 	// i.pressedKey = b[0]
	// 	ch <- b[0]

	// }()
	// select {
	// case key := <-ch:
	// 	i.pressedKey = key
	// case <-tick.C:
	// 	return
	// }
	// i.pressedKey = 0
	// wg := sync.WaitGroup{}
	// wg.Add(1)
	// go func(wg *sync.WaitGroup) {
	// 	defer wg.Done()
	// 	b := make([]byte, 1)
	// 	b[0] = 0
	// 	now := time.Now()
	// 	os.Stdin.Read(b)
	// 	os.Stdin.SetDeadline(now.Add(20 * time.Millisecond))
	// 	fmt.Println("hello")
	// 	i.pressedKey = b[0]
	// }(&wg)
	// wg.Wait()

	i.pressedKey = 0
	ch := make(chan byte, 1)

	go func() {
		b := make([]byte, 1)
		os.Stdin.Read(b) // Read is blocking
		ch <- b[0]
	}()

	select {
	case key := <-ch:
		i.pressedKey = key
	case <-time.After(30 * time.Millisecond):
		i.pressedKey = 0
	}
}

func (s *stats) update() {
	s.frames++
	if s.frames == MAX_SAMPLES {
		s.fps = float64(s.frames) / time.Since(s.start).Seconds()
		s.frames = 0
		s.start = time.Now()
	}
}

type game struct {
	isRunning bool
	level     *level
	stats     *stats
	drawBuf   *bytes.Buffer
	player    *player
	input     *input
}

func newGame(width, height int) *game {
	exec.Command("stty", "-F", "/dev/tty", "cbreak", "min", "1").Run()
	exec.Command("stty", "-F", "/dev/tty", "-echo").Run()
	lvl := newLevel(width, height)
	in := &input{}
	return &game{
		level:   lvl,
		stats:   newStats(),
		drawBuf: new(bytes.Buffer),
		player: &player{
			level: lvl,
			pos:   position{x: 2, y: 5},
			input: in,
		},
		input: in,
	}
}

type level struct {
	width, height int
	data          [][]int
}

func newLevel(width, height int) *level {
	data := make([][]int, height)

	for h := 0; h < height; h++ {
		for w := 0; w < width; w++ {
			data[h] = make([]int, width)
		}
	}

	for h := 0; h < height; h++ {
		for w := 0; w < width; w++ {
			if w == width-1 || h == height-1 || h == 0 || w == 0 {
				data[h][w] = WALL
			}
		}
	}
	return &level{
		width:  width,
		height: height,
		data:   data,
	}
}

func (l *level) set(pos position, v int) {
	l.data[pos.y][pos.x] = v
}

func (g *game) renderPlayer() {

	g.level.data[g.player.pos.y][g.player.pos.x] = PLAYER
	g.level.set(g.player.pos, PLAYER)
}

func (g *game) renderLevel() {

	for h := 0; h < g.level.height; h++ {
		for w := 0; w < g.level.width; w++ {
			if g.level.data[h][w] == WALL {
				g.drawBuf.WriteString("█")
			} else if g.level.data[h][w] == NOTHING {
				g.drawBuf.WriteString(" ")
			} else if g.level.data[h][w] == PLAYER {
				g.drawBuf.WriteString("త")
			}
		}
		g.drawBuf.WriteString("\n")
	}
}

func (g *game) start() {
	g.isRunning = true
	g.loop()
}

func (g *game) loop() {
	for g.isRunning {
		g.input.update()
		g.update()
		g.render()
		g.stats.update()
		time.Sleep(time.Millisecond * 16)
	}
}

func (g *game) renderStat() {
	g.drawBuf.WriteString("-- STATS\n")
	g.drawBuf.WriteString(fmt.Sprintf("FPS: %.2f\n", g.stats.fps))
	g.drawBuf.WriteString(fmt.Sprintf("KeyPressed: %v", g.input.pressedKey))
}

func (g *game) render() {
	g.drawBuf.Reset()
	fmt.Println("\033[2J\033[1;1H")
	g.renderLevel()
	g.renderStat()
	g.renderPlayer()
	fmt.Println(g.drawBuf.String())

}

func (g *game) update() {
	g.level.set(g.player.pos, NOTHING)
	g.player.update()
	g.level.set(g.player.pos, PLAYER)
}

func main() {
	width := 100
	height := 30

	g := newGame(width, height)
	g.start()
}
