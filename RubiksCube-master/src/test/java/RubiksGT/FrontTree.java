package RubiksGT;

import solutioning.strategy.Action;
import rubikcube.RubikCube;

import java.util.*;

public class FrontTree {
    public Map<RubikCube, Integer> frontTree;
    public Map<RCState, Integer> ArrTree;
    public BackTree backTree;
    public int num_of_nodes;
    public int limitBFS;
    public int currentLevel;

//Constructors
    public FrontTree(BackTree backTree) {
        limitBFS = backTree.limitBFS;
        frontTree = new HashMap<>();
        ArrTree = new HashMap<>();
        this.backTree = backTree;
        RubikCube rubix = new RubikCube(3);
        num_of_nodes = 0;

        rubix.randomize(); // Randomize the initial state of the Rubik's cube
        RCState rubix1 = new RCState(rubix, 0, null);
        ArrTree.put(rubix1, 1);
    }
//generate initial states
    public void generate() {
        for (int i = 0; i < limitBFS; i++) {
            generateMoreFrontStates();
        }
    }
    // Return path if a back tree node matches any on the front tree
    public List<RCState> getMatchingPath(RCState backTreeNode) {
        Map<RCState, Integer> frontTreeNodes = ArrTree;
        List<RCState> path = new ArrayList<>();

        for (Map.Entry<RCState, Integer> frontEntry : frontTreeNodes.entrySet()) {
            RCState frontNode = frontEntry.getKey();

            if (frontNode.equals(backTreeNode)) {
                System.out.println("Match found!");

            RCState currentNode = frontNode;
                while (frontNode != null) {
                    path.add(frontNode);
                    frontNode = frontNode.getParent();


                    //frontNode = frontNode.getParent();
                //while (frontNode != null) {
                  //  path.add(frontNode);
                   // frontNode = frontNode.getParent();
                }
                Collections.reverse(path);
                return path;
            }
        }
        currentLevel += 1;
        return path;
    }

//generate more states for the front tree
    public void generateMoreFrontStates() {
        Map<RCState, Integer> frontTreeNodes = new HashMap<>(ArrTree);

        for (Map.Entry<RCState, Integer> frontEntry : frontTreeNodes.entrySet()) {
            RCState node = frontEntry.getKey();
            Action<RubikCube>[] actions = node.getRubiksCube().getAllActions();

            if (currentLevel == node.getLevel()) {
                for (Action<RubikCube> action : actions) {
                    try {
                        RubikCube newState = node.getRubiksCube().clone();
                        newState.performAction(action);

                        if (newState.isComplete()) {
                            System.out.println("Match found during BFS.");
                            List<RCState> path = new ArrayList<>();
                            RCState current = node;
                            while (current != null) {
                                path.add(current);
                                current = current.getParent();
                            }
                            Collections.reverse(path);
                            for (RCState step : path) {
                                step.getRubiksCube().print();
                            }
                            System.exit(0);
                        }

                        RCState newNode = new RCState(newState, node.getLevel() + 1, node);
                        if (!ArrTree.containsKey(newNode)) {
                            node.addChild(newNode);
                            ArrTree.put(newNode, 1);
                            this.num_of_nodes++;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        currentLevel += 1;
    }

    public int getNumNodes() {
        return this.num_of_nodes;
    }
}
