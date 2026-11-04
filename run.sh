#!/bin/sh
set -e
SRC=src/main/java
OUT=out

mkdir -p "$OUT"
find "$SRC" -name '*.java' -exec printf '"%s"\n' {} \; > sources.txt
javac -d "$OUT" @sources.txt
rm -f sources.txt

java -cp "$OUT" bridge.Main
