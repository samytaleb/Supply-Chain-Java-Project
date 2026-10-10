# Supply Chain Scheduling — Java Optimization

Java-based scheduling optimization project comparing a constructive heuristic and local/metaheuristic improvement strategies.

## Implemented approaches

- **BIBA** — constructive method (`Files/construct/BIBA.java`)
- **VND** — variable neighborhood descent (`Files/localsearch/VND.java`)
- **LAHC** — late acceptance hill climbing (`Files/meta/LAHC.java`)

The application compares scheduling objective values and execution times across sample instances.

## Structure

- [`Files/app/Main.java`](Files/app/Main.java) — runnable entry point and sample instances
- [`Files/core/`](Files/core/) — instance, schedule, and objective evaluation
- [`Files/construct/`](Files/construct/) — initial solution construction
- [`Files/localsearch/`](Files/localsearch/) and [`Files/meta/`](Files/meta/) — search methods
- [Report.pdf](Report.pdf) and [Slides.pdf](Slides.pdf) — project documentation

## Compile and run

With a JDK installed, run from the repository root:

```bash
javac -d out Files/core/*.java Files/construct/*.java Files/localsearch/*.java Files/meta/*.java Files/app/Main.java
java -cp out app.Main
```

These commands match the current Java package structure but have not been executed as part of this documentation update.

## Notes

The repository currently includes compiled `.class` artifacts. In a source-focused repository, consider ignoring generated binaries and building them locally or in CI.
