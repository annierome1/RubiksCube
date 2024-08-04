package rubikcube;

import RubiksGT.BackTree;
import RubiksGT.FrontTree;
import RubiksGT.RCState;

public class Main {

    public static void main(String[] args) {
        try {
            // Set the limits for BFS and hash map size
            int limitBFS = 5; // Adjust as needed
            int limitHash = 10; // Adjust as needed

            // Initialize the BackTree with the limits
            BackTree backTree = new BackTree(limitBFS, limitHash);

            // Initialize the root state (solved state) for the cube
            RubikCube solvedCube = new RubikCube(3);
            RCState solvedState = new RCState(solvedCube, 0, null);

            // Perform the bidirectional search
            boolean found = backTree.sequentialDFS(solvedState, 20); // Adjust depth limit as needed

            if (found) {
                System.out.println("Solution found!");
            } else {
                System.out.println("Solution not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
