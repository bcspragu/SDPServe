package main

import (
	"strings"
)

var cssGridColors map[string]string = make(map[string]string)

func init() {
	for _, sig := range gridSignatures {
		cssGridColors[nameAsCssClass(sig.Name)] = sig.Color
	}
}

func nameAsCssClass(name string) string {
	return "." + strings.Replace(name, " ", "-", -1) + "-grid"
}
