package RubiksGT;

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
