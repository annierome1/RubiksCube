package RubiksGT;
import rubikcube.RubikCube;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class RCState {
    private RubikCube rubiksCube;
    private int level;
    private RCState parent;
    private List<RCState> children;

    // Constructor
    public RCState(RubikCube rubiksCube, int level, RCState parent) {
        this.rubiksCube = rubiksCube;
        this.level = level;
        this.parent = parent;
        this.children = new ArrayList<>();
    }

    // Getters
    public RubikCube getRubiksCube() {
        return rubiksCube;
    }

    public RCState getParent() {
        return parent;
    }

    public int getLevel() {
        return level;
    }

    public List<RCState> getChildren() {
        return children;
    }

    // Add a child node
    public void addChild(RCState child) {
        children.add(child);
    }

    // Calculate misplaced facelets
    public int calculateMisplacedFacelets() {
        int misplacedCount = 0;
        List<List<Integer>> currentState = this.getRubiksCube().generateArray();
        RubikCube solved = new RubikCube(3);
        List<List<Integer>> solvedState = solved.generateArray();

        for (int i = 0; i < currentState.size(); i++) {
            for (int j = 0; j < currentState.get(i).size(); j++) {
                if (!currentState.get(i).get(j).equals(solvedState.get(i).get(j))) {
                    misplacedCount++;
                }
            }
        }
        return misplacedCount;
    }

    // Override equals and hashCode methods to compare RubiksCubeState instances
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RCState)) return false;
        RCState other = (RCState) o;
        List<List<Integer>> thisState = this.getRubiksCube().generateArray();
        List<List<Integer>> otherState = other.getRubiksCube().generateArray();
        return Arrays.deepEquals(thisState.toArray(), otherState.toArray());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getRubiksCube().generateArray());
    }
}

