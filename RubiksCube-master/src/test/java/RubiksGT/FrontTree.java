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

    public FrontTree(BackTree backTree) {
        limitBFS = backTree.limitBFS;
        frontTree = new HashMap<>();
        ArrTree = new HashMap<>();
        this.backTree = backTree;
        RubikCube rubix = new RubikCube(3);
        num_of_nodes = 0;
        try {
            // Perform initial rotations on the cube
            rubix.turnRowToRight(0);
            rubix.turnRowToRight(1);
            rubix.turnColDown(0);
            rubix.turnRowToRight(2);
            rubix.turnRowToLeft(1);
            rubix.turnColDown(0);
            rubix.turnRowToRight(2);
            rubix.turnColDown(0);
            rubix.turnRowToRight(2);
            rubix.turnColUp(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        RCState rubix1 = new RCState(rubix, 0, null);
        ArrTree.put(rubix1, 1);
    }

    // Start generation of front tree
    public void generate() {
        for (int i = 0; i < limitBFS; i++) {
            generateMoreFrontStates();
        }
    }

    // Checks if backTreeNode matches any front tree nodes, if so, return the path
    public List<RCState> getMatchingPath(RCState backTreeNode) {
        Map<RCState, Integer> frontTreeNodes = ArrTree;
        List<RCState> path = new ArrayList<>();

        for (Map.Entry<RCState, Integer> frontEntry : frontTreeNodes.entrySet()) {
            RCState frontNode = frontEntry.getKey();
            Integer frontCost = frontEntry.getValue();

            if (frontCost.equals(backTreeNode.calculateMisplacedFacelets())) {
                List<List<Integer>> frontArray = frontNode.getRubiksCube().generateArray();
                List<List<Integer>> backArray = backTreeNode.getRubiksCube().generateArray();

                Integer[][] frontArrayAsArray = frontArray.stream()
                        .map(inner -> inner.toArray(new Integer[0]))
                        .toArray(Integer[][]::new);
                Integer[][] backArrayAsArray = backArray.stream()
                        .map(inner -> inner.toArray(new Integer[0]))
                        .toArray(Integer[][]::new);

                if (Arrays.deepEquals(frontArrayAsArray, backArrayAsArray)) {
                    System.out.println("Match found!");
                    frontNode = frontNode.getParent();
                    while (frontNode != null) {
                        path.add(frontNode);
                        frontNode = frontNode.getParent();
                    }
                    Collections.reverse(path);
                    return path;
                }
            }
        }
        currentLevel += 1;
        return path;
    }

    // Generates more front levels
    public void generateMoreFrontStates() {
        System.out.println("Expanding Front");
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
                            ArrTree.put(newNode, newNode.calculateMisplacedFacelets());
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

    // Returns number of nodes in the front tree
    public int getNumNodes() {
        return this.num_of_nodes;
    }
}

