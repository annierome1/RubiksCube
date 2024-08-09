package RubiksGT;
/*
August 8th, 2024

Annie Rome, Connor Loudermilk, Luke Talham
CSC 301 Advanced Data Structures


This program initializes and executes a bidirectional search algorithm to solve a Rubik's Cube puzzle.
It first attempts to find a solution using BFS within a specified limit, followed by DFS if BFS fails.
Finally, the program performs bidirectional search by expanding from both the scrambled and solved cube states, checking for a match between the search trees.



 SOURCES:
  Solving Rubik's Cube Using Graph Theory
  Khemani, Chanchal; Doshi, Jay; Duseja, Juhi; Shah, Krapi; Udmale, Sandeep; Sambhe, Vijay
  Advances in Intelligent Systems and Computing, 2019
  DOI: 10.1007/978-981-13-1132-1_24

 Initial Rubik's Cube implementation:
 Simulating Rubik Cube Actions with Java
 https://levelup.gitconnected.com/simulating-rubik-cube-actions-with-java-10cf44bc6014

 https://github.com/troykopec/RubiksCube


 */

import rubikcube.RubikCube;

public class Main {
    public static void main(String[] args) {
        // Define BFS and hash limits
        int limitBFS = 5; // Adjust as needed
        int limitHash = 3; // Adjust as needed
        int depthLimit = 10; // Adjust as needed

        // New instance of BackTree class is created
        BackTree backTree = new BackTree(limitBFS, limitHash);

        // New instance of RCState is created, representing the initial state of the cube
        RCState rootState = new RCState(new RubikCube(3), 0, null);

        boolean foundSolution = backTree.bidirectionalSearch(rootState, limitBFS);

        if (foundSolution) {
            System.out.println("Solution found!");
        } else {
            System.out.println("No solution found.");

        }
        backTree.printInfo();

    }
}

        /*
        // Perform BFS from root
        boolean foundSolution = backTree.BFS(rootState, depthLimit);
        if (foundSolution) {
            System.out.println("Solution found with BFS!");
        } else {
            // If BFS fails, try DLS
            System.out.println("No solution found with BFS. Trying DLS...");

            foundSolution = backTree.DLS(rootState, depthLimit);
            if (foundSolution) {
                System.out.println("Solution found with DLS!");
            } else {
                // If DLS also fails, try bidirectional search
                System.out.println("No solution found with DLS. Trying bidirectional search...");

                foundSolution = backTree.bidirectionalSearch(rootState, depthLimit);
                if (foundSolution) {
                    System.out.println("Solution found with bidirectional search!");
                } else {
                    System.out.println("No solution found.");
                }
            }
        }

        // Print additional information
        backTree.printInfo();
    }
}

/* Testing Portion
public class Main {
    public static void main(String[] args) {

        int limitBFS = 5; // Adjust as needed
        int limitHash = 3; // Adjust as needed
        int depthLimit = 10; // Adjust as needed

        BackTree backTree = new BackTree(limitBFS, limitHash);

        RCState rootState = new RCState(new RubikCube(3), 0, null);

        long startTimeBFS = System.nanoTime();
        boolean foundSolutionBFS = backTree.BFS(rootState, depthLimit);
        long endTimeBFS = System.nanoTime();
        long durationBFS = endTimeBFS - startTimeBFS;

        if (foundSolutionBFS) {
            System.out.println("Solution found with BFS!");
        } else {
            System.out.println("No solution found with BFS.");
        }
        System.out.println("Time taken for BFS: " + durationBFS + " nanoseconds");

        long startTimeDLS = System.nanoTime();
        boolean foundSolutionDLS = backTree.DLS(rootState, depthLimit);
        long endTimeDLS = System.nanoTime();
        long durationDLS = endTimeDLS - startTimeDLS;

        if (foundSolutionDLS) {
            System.out.println("Solution found with DLS!");
        } else {
            System.out.println("No solution found with DLS.");
        }
        System.out.println("Time taken for DLS: " + durationDLS + " nanoseconds");

        long startTimeBidirectional = System.nanoTime();
        boolean foundSolutionBidirectional = backTree.bidirectionalSearch(rootState, depthLimit);
        long endTimeBidirectional = System.nanoTime();
        long durationBidirectional = endTimeBidirectional - startTimeBidirectional;

        if (foundSolutionBidirectional) {
            System.out.println("Solution found with bidirectional search!");
        } else {
            System.out.println("No solution found with bidirectional search.");
        }
        System.out.println("Time taken for bidirectional search: " + durationBidirectional + " nanoseconds");

        backTree.printInfo();
    }
} */

/* Citations

 */