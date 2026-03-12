# HU-Load Mining Game ⛏️

A JavaFX-based 2D mining game developed for BBM104: Introduction to Programming Laboratory II at Hacettepe University. Inspired by the classic Motherload game.

## About the Project

**HU-Load** is a 2D mining game where the player controls a drilling machine underground to collect valuable minerals and gems. The goal is to collect as much money as possible before the machine runs out of fuel.

## Gameplay

- Control the drill with **arrow keys**
- Drill through soil to collect **minerals and gems**
- Avoid **lava** — drilling into it causes instant game over
- Avoid running out of **fuel** — game ends with total money displayed
- **Gravity** applies — the machine falls if there's no ground beneath it
- Machine faces the direction it's moving; flight blades appear when flying upward

## Game Elements

| Element | Description |
|---------|-------------|
| Soil / Grass | Drillable, consumes fuel |
| Valuables | Minerals and gems with weight and money value |
| Boulder | Indestructible, forms the borders of the map |
| Lava | Instant game over on contact |

## Technologies

- Java 8
- JavaFX (pure, no Swing/AWT/FXML)
- Object-Oriented Programming
- JavaDoc commenting style

## How to Run

```bash
javac *.java
java -cp . Main
```

> Assets folder must be placed inside the `src` directory.

## Course

**BBM104** - Introduction to Programming Laboratory II  
Hacettepe University, Spring 2024
