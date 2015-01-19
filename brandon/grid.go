package main

type Grid [][]bool

func NewGrid(x, y int) Grid {
	grid := Grid{}
	grid = make([][]bool, x)
	for i := range grid {
		grid[i] = make([]bool, y)
	}
	return grid
}
