package project4;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/** This class represents the puzzle and provides methods for generating
 *  its solutions.
 *
 *  @author Aramis Tanelus
 */
public class Puzzle {
    
    /** Represents a node in a solution to the puzzle.
     * The nodes arrange themselves into a tree, with each node having
     * as many as 2 (for left and right) or as few as no solutions.
     * Leaf nodes represent both solutions and dead ends (zeros that aren't
     * the last element)
     */
    private class SolutionNode {
        private SolutionNode left;
        private SolutionNode right;
        private Puzzle owner;
        private int index;
        private boolean isSolution;
        
        private SolutionNode(Puzzle p) {
            this.owner = p;
            this.left = null;
            this.right = null;
            this.index = 0;
            this.isSolution = false;
        }

        /** Returns a string representation of this step of the solution
         * Looks similar to [  3 ,  4 , 11 , 2L, 25 , 0 ]
         */
        public String toString(char direction) {
            if (this.owner.tiles.length == 1)
                return "[ 0 ]";
            
            // Treats the first segment differently because it doesn't have a comma or
            // the space before the digit token
            StringBuilder resultBuilder = new StringBuilder("[");
            String firstSegment = "%2d%c";
            char directionChar = this.index == 0 ? direction : ' ';
            resultBuilder.append(String.format(
                        firstSegment,
                        this.owner.tiles[0],
                        directionChar
            ));

            // Now add everything except the last digit's segment
            for(int i = 1; i < this.owner.tiles.length - 1; i++) {
                String segmentFormat = ", %2d%c";
                directionChar = this.index == i ? direction : ' ';
                resultBuilder.append(String.format(
                            segmentFormat, 
                            this.owner.tiles[i],
                            directionChar
                ));
            }

            // The last tile is assumed to be a zero
            resultBuilder.append(",  0 ]");
            return resultBuilder.toString();
        }

    }

    private int[] tiles;
    private SolutionNode root;

    /** Initializes the puzzle with the given tile numbers
     * @param slots The tiles of the puzzle
     * @throws IllegalArgumentException when any tile is outside the range [0,99] or the last tile is not 0
     * @throws NullPointerException if tile array is null
     */
    public Puzzle(int[] tiles) throws IllegalArgumentException, NullPointerException{
        if (tiles == null)
            throw new NullPointerException("Cannot instantiate a puzzle with a null tiles array.");
        if (tiles.length == 0)
            throw new IllegalArgumentException("Cannot instantiate a puzzle with no tiles.");
        if (tiles[tiles.length - 1] != 0)
            throw new IllegalArgumentException("The last tile must have a value of 0.");
        for (int i = 0; i < tiles.length; i++) {
            if ( (tiles[i] < 0) || (tiles[i] > 99) )
                throw new IllegalArgumentException("ERROR: the puzzle values have to be positive integers.");
            if (tiles[i] > 99)
                throw new IllegalArgumentException("ERROR: the puzzle values have to be less than 100.");
        }
        this.tiles = tiles;
        this.root = new SolutionNode(this);
        this.findSolutions();
    }


    /** A wrapper functino for populating the solution tree
     */
    private void findSolutions(){
        // Initially, only the first node is visited
        int[] initialHits = {};
        this.findSolutions(root, initialHits);
    }

    /** A helper function for populating the solution tree
     * The algorithm works by recursively considering
     * a traversal down the left or right path (by traveling left
     * or right at any particular index).
     * @param start The node from which the solution search begins
     * @param hits Previously visited tiles
     * @return True if there is a valid solution starting at the node
     */
    private boolean findSolutions(SolutionNode start, int[] hits){
        int puzzleLen = this.tiles.length;
        int tileIndex = start.index;
        // First base case: the index is invalid:
        if ( (tileIndex >= puzzleLen) || (tileIndex < 0) )
            return false;
        int tileValue = this.tiles[start.index];

        // Second base case: the path is already a solution
        if (tileIndex == puzzleLen - 1) {
            start.left = null;
            start.right = null;
            start.isSolution = true;
            return true;
        }
        
        // Third base case: the tile has already been visited
        // Based on the idea that revisiting a node, regardless of direction
        // traveled, is redundant and implies there is a loop in the path
        for (int hit : hits)
            if (tileIndex == hit)
                return false;

        // Fourth base case: The number at the index is zero, but 
        // the node isn't a solution
        // In this case, there is nowhere to go from this node
        if (tileValue == 0)
            return false;

        int[] newHits = new int[hits.length + 1];
        for (int i = 0; i < hits.length; i++)
            newHits[i] = hits[i];
        newHits[hits.length] = tileIndex;

        // Determine whether the left branch can be traversed
        SolutionNode leftNode = new SolutionNode(this);
        leftNode.index = tileIndex - tileValue;
        boolean hasLeftSolutions = this.findSolutions(leftNode, newHits);
        if (hasLeftSolutions)
            // If it has any solutions, insert it into the tree
            start.left = leftNode;

        // Repeat for the right side
        SolutionNode rightNode = new SolutionNode(this);
        rightNode.index = tileIndex + tileValue;
        boolean hasRightSolutions = this.findSolutions(rightNode, newHits);
        if (hasRightSolutions)
            start.right = rightNode;
        return hasRightSolutions || hasLeftSolutions;
    }

    
    /** Gets all solutions in order of increasing length
     * @return A List of Strings containing multiple lines, each line being a step in that solution
     */
    public ArrayList<String> getSolutions(){
        // A queue is used to moderate the search order in a way that
        // ensures all nodes of a certain depth get searched before any
        // deeper nodes. As a result, the output paths are already
        // sorted in order of increasing length
        Queue<SolutionNode> traversalQueue = new LinkedList<>();
        // Each entry in this Queue corresponds to the entry in the
        // same position of `traversalQueue`. Each entry is a single
        // line in the output solution for a particular traversal
        Queue<ArrayList<String> > paths = new LinkedList<>();
        traversalQueue.add(root);
        paths.add(new ArrayList<>());

        // Holds the final solutions as multi-line strings
        ArrayList<String> solutions = new ArrayList<String>();
        
        while (!traversalQueue.isEmpty()) {
            // Assuming this won't be null since I've already determined
            // the queue isn't empty
            SolutionNode currentNode = traversalQueue.poll();
            // Paths will always have the same size as traversalQueue
            ArrayList<String> currentPath = paths.poll();

            if (currentNode.isSolution){
                // This if statement is only ever triggered for the input [ 0 ]
                // For all other puzzles, including this line duplicates the last 
                // step of the puzzle's solution
                if (currentPath.size() == 0)
                    currentPath.add(currentNode.toString(' '));
                solutions.add(stringFromArrayList(currentPath));
                continue;
            }

            // If both are null and the continue wasn't executed, the node is a dead-end
            // (a zero-value tile before the last element) and isn't a part of any solution
            if (currentNode.left != null) {
                String leftAddition = currentNode.toString('L');
                // The arraylist must be copied because the left traversal and
                // right traversal produce two different strings (one with an L
                // and the other with an R)
                ArrayList<String> leftPath = new ArrayList<>(currentPath);
                leftPath.add(leftAddition);
                traversalQueue.add(currentNode.left);
                paths.add(leftPath);
            }
            if (currentNode.right != null) {
                String rightAddition = currentNode.toString('R');
                ArrayList<String> rightPath = new ArrayList<>(currentPath);
                rightPath.add(rightAddition);
                traversalQueue.add(currentNode.right);
                paths.add(rightPath);
            }
        }

        return solutions;
    }

    
    /** Converts a list of strings into a single multi-line string
     * @param al List of strings to join
     * @return A string consisting of the elements of `al` joined by '\n'
     */
    private String stringFromArrayList(ArrayList<String> al) {
        if (al.isEmpty())
            return "";
        StringBuilder builder = new StringBuilder();
        builder.append(al.get(0));
        for (int i = 1; i < al.size(); i++) {
            builder.append("\n");
            builder.append(al.get(i));
        }
        return builder.toString();
    }
}

