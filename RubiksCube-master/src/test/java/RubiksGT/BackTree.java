//////////////////////////////////////////////////////////////////////////////////
///Backtree class
///manages backward search tree and implements the algorithms

package RubiksGT;

import rubikcube.RubikCube;
import solutioning.strategy.Action;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Queue;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.Set;
import java.util.Collections; // Add this import for Collections.reverse

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

//////initilizes trees and sets up initial parameters, generates initial states
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
///expands initial state of the cube and adds resulting tree states
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

    public boolean BFS(RCState root, int depthLimit) {
        // create queue for BFS
        Queue<RCState> queue = new LinkedList<>();
        //set to keep track of states and avoid processing the same states more than once
        Set<RCState> visited = new HashSet<>();

        //add root to node queue and mark it as visited
        queue.add(root);
        visited.add(root);
        //continue processing until queue is empty
        while (!queue.isEmpty()) {
            // remove the next node from queue
            RCState current = queue.poll();
            //check if node represents a solved state
            if (current.getRubiksCube().isComplete()) {
                //if solved, find and print path from the root
                List<RCState> path = getPathToRoot(current);
                for (RCState step : path) {
                    step.getRubiksCube().print();
                }
                return true;
            }
            // if current nodes level is less than the depth limit, explore its children
            if (current.getLevel() < depthLimit) {
                //iterate each child of the current node
                for (RCState child : current.getChildren()) {
                    //if the child has not yet been visited, add child to the queue and mark as visited
                    if (!visited.contains(child)) {
                        queue.add(child);
                        visited.add(child);
                    }
                }
            }
        }
        return false;
    }

    private List<RCState> getPathToRoot(RCState node) {
        List<RCState> path = new ArrayList<>();
        while (node != null) {
            path.add(node);
            node = node.getParent();
        }
        Collections.reverse(path);
        return path;
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

    public boolean sequentialBFS(RCState root, int depthLimit) {
        Queue<RCState> queue = new LinkedList<>();
        Set<RCState> visited = new HashSet<>();

        queue.add(root);
        visited.add(root);

        while (!queue.isEmpty() && depthLimit > 0) {
            RCState current = queue.poll();
            if (current.getRubiksCube().isComplete()) {
                List<RCState> path = getPathToRoot(current);
                for (RCState step : path) {
                    step.getRubiksCube().print();
                }
                return true;
            }

            for (RCState child : current.getChildren()) {
                if (!visited.contains(child)) {
                    queue.add(child);
                    visited.add(child);
                }
            }
            depthLimit--;
        }
        return false;
    }

    public void printInfo(BackTree backTest) {
        System.out.println("front tree nodes:" + backTest.frontTree.getNumNodes());
        System.out.println("back tree nodes:" + backTest.getNumNodes());

        long timeB = System.currentTimeMillis() / 1000;
        long KMPTime = timeB - timeA;
        System.out.println("TIME TO COMPLETE: " + KMPTime);
    }
}
