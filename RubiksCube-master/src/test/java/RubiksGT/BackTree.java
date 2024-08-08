package RubiksGT;

import rubikcube.RubikCube;
import solutioning.strategy.Action;

import java.util.*;
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

        frontTree.generate();
        generateInitialStates();
    }
 ///expands intial states of the cube and adds resulting tree states
    public void generateInitialStates() {
        System.out.println("Expanding Back");
        this.solvedNode = new RCState(new RubikCube(3), 0, null);
        ArrTree.put(this.solvedNode, 0);

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
        for (int i = 0; i < limitHash - 1; i++) {
            generateMoreStates();
        }
    }

    public void generateMoreStates() {

        Map<RCState, Integer> backTreeNodes = new HashMap<>(ArrTree);

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

//Performs BFS to a given limit
    public boolean BFS(RCState root, int depthLimit) {
        // create queue for BFS
        Queue<RCState> queue = new LinkedList<>();
        //set to keep track of states and avoid processing the same states more than once
        Set<RCState> visited = new HashSet<>();
        Map<RCState, Integer> costMap = new HashMap<>();

        //add root to node queue and mark it as visited
        queue.add(root);
        visited.add(root);
        costMap.put(root, 0);
        //continue processing until queue is empty
        while (!queue.isEmpty()) {
            // remove the next node from queue
            RCState current = queue.poll();
            int currentCost = costMap.get(current);
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
                for (Action<RubikCube> action : current.getRubiksCube().getAllActions()) {
                    try {
                        RubikCube newState = current.getRubiksCube().clone();
                        newState.performAction(action);
                        RCState child = new RCState(newState, current.getLevel() + 1, current);
                        current.addChild(child);

                        if (!visited.contains(child)) {
                            queue.add(child);
                            visited.add(child);
                            costMap.put(child, currentCost + 1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }
//Performs DLS on the back tree ot a given limit 
    public boolean DLS(RCState node, int depthLimit) {
        if (depthLimit <= 0) {
            return false;
        }

        if (node.getRubiksCube().isComplete()) {
            List<RCState> path = getPathToRoot(node);
            for (RCState step : path) {
                step.getRubiksCube().print();
            }
            return true;
        }

        for (Action<RubikCube> action : node.getRubiksCube().getAllActions()) {
            try {
                RubikCube newState = node.getRubiksCube().clone();
                newState.performAction(action);
                RCState child = new RCState(newState, node.getLevel() + 1, node);
                node.addChild(child);
                if (DLS(child, depthLimit - 1)) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean bidirectionalSearch(RCState root, int depthLimit) {
        for (int depth = 1; depth <= depthLimit; depth++) {
            Set<RCState> frontVisited = new HashSet<>();
            Set<RCState> backVisited = new HashSet<>();

            Queue<RCState> frontQueue = new LinkedList<>();
            Queue<RCState> backQueue = new LinkedList<>();

            frontQueue.add(root);
            backQueue.add(solvedNode);

            frontVisited.add(root);
            backVisited.add(solvedNode);

            while (!frontQueue.isEmpty() && !backQueue.isEmpty()) {
                if (BFSLevel(frontQueue, frontVisited, backVisited)) {
                    return true;
                }

                if (BFSLevel(backQueue, backVisited, frontVisited)) {
                    return true;
                }

                //Check for matching path
                for (RCState backNode : backVisited) {
                    List<RCState> path = frontTree.getMatchingPath(backNode);
                    if (!path.isEmpty()) {
                        path.forEach(node -> node.getRubiksCube().print());
                        return true;
                    }
                }

            }
        }
        return false;
    }

    private boolean BFSLevel(Queue<RCState> queue, Set<RCState> visited, Set<RCState> otherVisited) {
        int size = queue.size();
        for (int i = 0; i < size; i++) {
            RCState current = queue.poll();

            if (otherVisited.contains(current)) {
                printSolutionPath(current);
                return true;
            }

            for (Action<RubikCube> action : current.getRubiksCube().getAllActions()) {
                try {
                    RubikCube newState = current.getRubiksCube().clone();
                    newState.performAction(action);
                    RCState child = new RCState(newState, current.getLevel() + 1, current);
                    current.addChild(child);

                    if (!visited.contains(child)) {
                        queue.add(child);
                        visited.add(child);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private void printSolutionPath(RCState node) {
        List<RCState> path = new ArrayList<>();
        while (node != null) {
            path.add(node);
            node = node.getParent();
        }
        Collections.reverse(path);
        for (RCState step : path) {
            step.getRubiksCube().print();
        }
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

    public void printInfo() {
        System.out.println("Front tree nodes: " + frontTree.getNumNodes());
        System.out.println("Back tree nodes: " + getNumNodes());

        long timeB = System.currentTimeMillis() / 1000;
        long KMPTime = timeB - timeA;
        System.out.println("TIME TO COMPLETE: " + KMPTime);
    }

    public int getNumNodes() {
        return this.num_of_nodes;
    }
}