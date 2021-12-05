# Horse Transport to the Olympic Games

## Project Description
This project addresses the problem of optimizing horse transport to the Olympic Games in Paris.
The program uses available airplanes to transport all horses as quickly as possible, considering the coordinates of horses and airplanes, horse weight, loading/unloading times, and the payload capacity of the airplanes.
It is a combination of the traveling salesman problem and the knapsack problem.

## Features
- **Horse Transport**: Finding the most efficient route for each airplane to transport assigned horses to Paris.
- **Horse Allocation**: Efficiently distributing horses between airplanes based on their speed.
- **Cargo Optimization**: Considering the maximum payload of the airplanes when loading horses.
- **Output Data**: Displaying takeoff times, loading/unloading times, and arrival times in Paris.
- **Extensions**: Program stepping, airplane statistics, user-friendly environment, data generator, JavaDoc documentation, code quality checks using the PMD tool.

## Problem Analysis
The problem is a combination of NP-complete problems:
- **Traveling Salesman Problem**: Finding the minimum Hamiltonian path for each airplane.
- **Knapsack Problem**: Ensuring that the airplanes do not exceed their maximum capacity.

The solution is based on an approximation algorithm using the nearest neighbor approach with modifications to account for the airplane capacity.

## Program Design
The program is implemented in Java using an object-oriented approach.

### UML Diagrams
Include:
- Basic program logic.
- Unified in the `Main` class.
- Detailed view of important methods and attributes of key classes.

### Class Descriptions

#### Key Classes
- **Main**: The entry point of the program, unites the application, assigns horses to airplanes, and starts the simulation.
- **Parser**: Processes the input file containing data about horses and airplanes.
- **Generator**: Generates test data.
- **GraphNode**: Represents a point on the map (basis for horses and airplanes).
- **Horse**: Represents a horse (weight, loading/unloading time).
- **Aircraft**: Represents an airplane (speed, maximum load, methods for flying, loading, and unloading).
- **DistFunc**: Interface for calculating distance.
- **CartesianDist**: Implementation of `DistFunc` for Euclidean metric.
- **MinHeap**: Implementation of a priority queue using a heap for efficient nearest horse search.
- **HeapElement**: An element in the priority queue.
- **ClosestNeighborPath**: Wrapper class for the nearest horse search algorithm.
- **MetricsGraph**: Represents a complete, undirected weighted graph.
- **FlightSimulator**: Starts and controls the flight simulation.
- **FlightState**: Maintains the current flight state.

## User Manual

### Running the Program
1. Compile and run the `Main.java` file.

```
javac Main.java
java Main [arguments]
```

### Command-Line Arguments
The program supports command-line arguments (see documentation).

### Input File Format
The input file must follow a specific format (see documentation).

### Program Execution
The program loads the data, performs calculations, and outputs results to the console.
