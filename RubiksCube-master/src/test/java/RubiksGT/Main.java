package RubiksGT;

import rubikcube.RubikCube;
import RubiksGT.BackTree;
import RubiksGT.RCState;

public class Main {
    public static void main(String[] args) {
        //define bfs and hash limits
        int limitBFS = 3;
        int limitHash = 3;
        // new instance of backtree class is created
        BackTree backTree = new BackTree(limitBFS, limitHash);
        // new instance of RC state is created, representing intitial state of cube
        RCState rootState = new RCState(new RubikCube(3), 0, null);
        //preform bfs from root
        boolean foundSolution = backTree.sequentialBFS(rootState, limitBFS);
        if (foundSolution) {
            System.out.println("Solution found!");
        } else {
            System.out.println("No solution found.");
        }
    }
}
