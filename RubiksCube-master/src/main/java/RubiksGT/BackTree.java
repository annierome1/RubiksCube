package RubiksGT;

import rubikcube.RubikCube;
import solutioning.strategy.Action;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class BackTree {
    private AtomicBoolean solutionFound = new AtomicBoolean(false);
    public Map<RubikCube, Integer> backTree;
    public FrontTree frontTree;
    public int num_of_nodes;
    public Map<RCState, Integer> ArrTree;
    public int limitBFS;
    public int limitHash;
    public RCState solvedNode;
    public int currentLevel = 0;
    private long timeA;

    public BackTree(int limitBFS, int limitHash) {
        this.timeA = System.currentTimeMillis() / 1000;
        this.limitBFS = limitBFS;
        this.limitHash = limitHash;
        ArrTree = new HashMap<>();
        backTree = new HashMap<>();
        num_of_nodes = 0;
        frontTree = new FrontTree(this);

        // Sequentially generate initial states for front and back trees
        frontTree.generate();
        generateInitialStates();
    }

    public void generateInitialStates() {
        System.out.println("Expanding Back");
        this.solvedNode = new RCState(new RubikCube(3), 0, null);
        ArrTree.put(this.solvedNode, 0);

        // Generate all of the first actions and store into the backtree
        for (Action<RubikCube> action : solvedNode.getRubiksCube().getAllActions()) {
            try {
                RubikCube newState = solvedNode.getRubiksCube().clone();
                newState.performAction(action);
                RCState newNode = new RCState(newState, solvedNode.getLevel() + 1, solvedNode);
                solvedNode.addChild(newNode);
                ArrTree.put(newNode, 1);
                this.num_of_nodes++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        currentLevel += 1;
        // More states of backtree
        for (int i = 0; i < limitHash - 1; i++) {
            generateMoreStates();
        }
    }

    // Generates more states in the tree
    public void generateMoreStates() {
        System.out.println("Expanding Back");
        Map<RCState, Integer> backTreeNodes = new HashMap<>(ArrTree);

        // Generate all of the actions that can be done from this node
        for (Map.Entry<RCState, Integer> backEntry : backTreeNodes.entrySet()) {
            RCState node = backEntry.getKey();
            Action<RubikCube>[] actions = node.getRubiksCube().getAllActions();

            if (currentLevel == node.getLevel()) {
                for (Action<RubikCube> action : actions) {
                    try {
                        RubikCube newState = node.getRubiksCube().clone();
                        newState.performAction(action);
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

    public boolean DFS(RCState currentNode, int depthLimit) {
        if (depthLimit <= 0) {
            return false;
        }

        if (frontTree.ArrTree.containsKey(currentNode)) {
            List<RCState> path = frontTree.getMatchingPath(currentNode);
            if (!path.isEmpty()) {
                RCState current = currentNode;
                while (current != null) {
                    path.add(current);
                    current = current.getParent();
                }
                for (RCState step : path) {
                    step.getRubiksCube().print();
                }
                solutionFound.set(true);
                return true;
            }
        }

        List<RCState> children = currentNode.getChildren();
        for (RCState child : children) {
            if (DFS(child, depthLimit - 1)) {
                solutionFound.set(true);
                return true;
            }
        }
        return false;
    }

    public boolean sequentialDFS(RCState root, int depthLimit) {
        List<RCState> rootChildren = getChildrenNodes(root);
        for (RCState subRoot : rootChildren) {
            if (DFS(subRoot, depthLimit - 1)) {
                solutionFound.set(true);
                printInfo(this);
                return true;
            }
        }
        return false;
    }

    public List<RCState> getChildrenNodes(RCState rootNode) {
        List<RCState> childrenNodes = new ArrayList<>();
        List<RCState> children = rootNode.getChildren();
        for (RCState child : children) {
            childrenNodes.add(child);
        }
        return childrenNodes;
    }

    public void printInfo(BackTree backTest) {
        System.out.println("front tree nodes:" + backTest.frontTree.getNumNodes());
        System.out.println("back tree nodes:" + backTest.getNumNodes());

        long timeB = System.currentTimeMillis() / 1000;
        long KMPTime = timeB - timeA;
        System.out.println("TIME TO COMPLETE: " + KMPTime);
    }
}
