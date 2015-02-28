package main

import (
	"strings"
)

var cssGridColors = make(map[string]State)

type State struct {
	On  string
	Off string
}

func initCss() {
	for _, sig := range gridSignatures {
		cssGridColors[nameAsCssClass(sig.Name)] = State{sig.OnColor, sig.OffColor}
	}
}

func nameAsCssClass(name string) string {
	return "." + strings.Replace(name, " ", "-", -1) + "-grid"
}
